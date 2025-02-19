package com.ulca.benchmark.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;

//Import the Base64 encoding library.
//import org.apache.commons.codec.binary.Base64;

import javax.sound.sampled.AudioFormat;

import java.util.Collection;
import java.util.Date;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.ulca.benchmark.dao.BenchmarkProcessDao;
import com.ulca.benchmark.model.BenchmarkProcess;
import com.ulca.benchmark.model.ModelInferenceResponse;
import com.ulca.model.dao.ModelExtended;
import com.ulca.model.dao.ModelInferenceResponseDao;

import io.swagger.model.Benchmark;
import io.swagger.model.ImageFile;
import io.swagger.model.ImageFiles;
import io.swagger.model.InferenceAPIEndPoint;
import io.swagger.model.OCRInference;
import io.swagger.model.OCRRequest;
import io.swagger.model.OneOfInferenceAPIEndPointSchema;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;


@Slf4j
@Service
public class OcrBenchmark {

	@Autowired
	private KafkaTemplate<String, String> benchmarkMetricKafkaTemplate;

	@Autowired
	OkHttpClientService okHttpClientService;
	@Value("${kafka.ulca.bm.metric.ip.topic}")
	private String mbMetricTopic;

	@Autowired
	ModelInferenceResponseDao modelInferenceResponseDao;
	
	@Autowired
	BenchmarkProcessDao benchmarkProcessDao;
	
	public boolean prepareAndPushToMetric(ModelExtended model, Benchmark benchmark, Map<String,String> fileMap, Map<String, String> benchmarkProcessIdsMap) throws IOException, URISyntaxException {
		
		InferenceAPIEndPoint inferenceAPIEndPoint = model.getInferenceEndPoint();
		String callBackUrl = inferenceAPIEndPoint.getCallbackUrl();
		OneOfInferenceAPIEndPointSchema schema = inferenceAPIEndPoint.getSchema();

		String dataFilePath = fileMap.get("baseLocation")  + File.separator + "data.json";
		log.info("data.json file path :: " + dataFilePath);

		InputStream inputStream = Files.newInputStream(Path.of(dataFilePath));
		JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
		String userId = model.getUserId();
		reader.beginArray();

		List<String> ip = new ArrayList<String>();
		List<String> tgtList = new ArrayList<String>();

		String baseLocation = fileMap.get("baseLocation")  + File.separator ;
		JSONArray corpus = new JSONArray();
		int totalRecords = 0;
		int failedRecords = 0;
		while (reader.hasNext()) {

			Object rowObj = new Gson().fromJson(reader, Object.class);
			ObjectMapper mapper = new ObjectMapper();
			String dataRow = mapper.writeValueAsString(rowObj);

			JSONObject inputJson =  new JSONObject(dataRow);
			String imageFilename = inputJson.getString("imageFilename");
			String imagePath = baseLocation + imageFilename;

			byte[] bytes = FileUtils.readFileToByteArray(new File(imagePath));

			ImageFile imageFile = new ImageFile();
			imageFile.setImageContent(bytes);

			ImageFiles imageFiles = new ImageFiles();
			imageFiles.add(imageFile);

			log.info("start time for calling the inference end point");
			log.info("dataRow :: " + dataRow);
			OCRInference ocrInference = (OCRInference) schema;

			OCRRequest ocrRequest = ocrInference.getRequest();
			ocrRequest.setImage(imageFiles);

			String resultText = okHttpClientService.ocrCompute(callBackUrl, ocrRequest);

			log.info("result :: " + resultText);
			log.info("end time for calling the inference end point");

			String targetText = inputJson.getString("groundTruth");
			totalRecords++;
			if(resultText != null) {
				JSONObject target =  new JSONObject();
				target.put("tgt", targetText);
				target.put("mtgt", resultText);
				corpus.put(target);
			}else {
				failedRecords++;
			}
		}
		reader.endArray();
		reader.close();
		inputStream.close();
		
		List<String> benchmarkProcessIdsList =  new ArrayList<String>(benchmarkProcessIdsMap.keySet()); 
		
        for (String benchmarkingProcessId:benchmarkProcessIdsList) {

        	String metric = benchmarkProcessIdsMap.get(benchmarkingProcessId);
        	
			JSONArray benchmarkDatasets = new JSONArray();
			JSONObject benchmarkDataset = new JSONObject();
			benchmarkDataset.put("datasetId", benchmark.getBenchmarkId());
			benchmarkDataset.put("metric", metric);
			benchmarkDataset.put("corpus", corpus);
			benchmarkDatasets.put(benchmarkDataset);

			JSONObject metricRequest = new JSONObject();
			metricRequest.put("benchmarkingProcessId", benchmarkingProcessId);
			metricRequest.put("modelId", model.getModelId());
			metricRequest.put("modelName", model.getName());
			if (benchmark.getLanguages() != null && benchmark.getLanguages().getTargetLanguage() != null) {
				String targetLanguage = benchmark.getLanguages().getTargetLanguage().toString();
				metricRequest.put("targetLanguage", targetLanguage);
			}

			metricRequest.put("userId", userId);
			metricRequest.put("modelTaskType", model.getTask().getType().toString());
			metricRequest.put("benchmarkDatasets", benchmarkDatasets);
			log.info("total recoords :: " + totalRecords + " failedRecords :: " + failedRecords);
			log.info("data before sending to metric");
			log.info(metricRequest.toString());

			//update the total record count
			int datasetCount = corpus.length();
			BenchmarkProcess bmProcessUpdate = benchmarkProcessDao.findByBenchmarkProcessId(benchmarkingProcessId);
			bmProcessUpdate.setRecordCount(datasetCount);
			bmProcessUpdate.setLastModifiedOn(Instant.now().toEpochMilli());
			benchmarkProcessDao.save(bmProcessUpdate);

			benchmarkMetricKafkaTemplate.send(mbMetricTopic, metricRequest.toString());

			//save the model inference response
			ModelInferenceResponse modelInferenceResponse = new ModelInferenceResponse();
			modelInferenceResponse.setBenchmarkingProcessId(benchmarkingProcessId);
			modelInferenceResponse.setCorpus(corpus.toString());
			modelInferenceResponse.setBenchmarkDatasetId(benchmark.getBenchmarkId());
			modelInferenceResponse.setMetric(metric);
			modelInferenceResponse.setModelId(model.getModelId());
			modelInferenceResponse.setModelName(model.getName());
			modelInferenceResponse.setUserId(userId);
			modelInferenceResponse.setModelTaskType(model.getTask().getType().toString());
			modelInferenceResponseDao.save(modelInferenceResponse);
		}
      return true;
	}

}