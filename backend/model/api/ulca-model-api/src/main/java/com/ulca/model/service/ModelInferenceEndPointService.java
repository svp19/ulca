package com.ulca.model.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.swagger.model.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulca.benchmark.util.FileUtility;
import com.ulca.model.exception.ModelComputeException;
import com.ulca.model.request.Input;
import com.ulca.model.request.ModelComputeRequest;
import com.ulca.model.response.ModelComputeResponse;
import com.ulca.model.response.ModelComputeResponseOCR;
import com.ulca.model.response.ModelComputeResponseTranslation;
import com.ulca.model.response.ModelComputeResponseTTS;
import com.ulca.model.response.ModelComputeResponseTransliteration;
import com.ulca.model.response.ModelComputeResponseTxtLangDetection;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
public class ModelInferenceEndPointService {

	@Autowired
	WebClient.Builder builder;

	@Value("${ulca.model.upload.folder}")
	private String modelUploadFolder;

	@Autowired
	FileUtility fileUtility;

	public InferenceAPIEndPoint validateSyncCallBackUrl(InferenceAPIEndPoint inferenceAPIEndPoint)
			throws URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException {

		String callBackUrl = inferenceAPIEndPoint.getCallbackUrl();
		OneOfInferenceAPIEndPointSchema schema = inferenceAPIEndPoint.getSchema();

		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TranslationInference")) {
			io.swagger.model.TranslationInference translationInference = (io.swagger.model.TranslationInference) schema;
			TranslationRequest request = translationInference.getRequest();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			// OkHttpClient client = new OkHttpClient();
			OkHttpClient client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build();

			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			Response httpResponse = client.newCall(httpRequest).execute();
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			String responseJsonStr = httpResponse.body().string();

			TranslationResponse response = objectMapper.readValue(responseJsonStr, TranslationResponse.class);
			translationInference.setResponse(response);
			schema = translationInference;

		}else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.ASRInference")) {

		
			io.swagger.model.ASRInference asrInference = (io.swagger.model.ASRInference) schema;
			ASRRequest request = asrInference.getRequest();

			ASRResponse response = null;
			SslContext sslContext;
			try {
				sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

				HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

				response = builder.clientConnector(new ReactorClientHttpConnector(httpClient)).build().post()
						.uri(callBackUrl).body(Mono.just(request), ASRRequest.class).retrieve()
						.bodyToMono(ASRResponse.class).block();

			} catch (SSLException e) {
				e.printStackTrace();
			}

			ObjectMapper objectMapper = new ObjectMapper();
			log.info("logging asr inference point response" + objectMapper.writeValueAsString(response));
			asrInference.setResponse(response);
			schema = asrInference;

		

		} else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.OCRInference")) {
			io.swagger.model.OCRInference ocrInference = (io.swagger.model.OCRInference) schema;
			OCRRequest request = ocrInference.getRequest();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			// OkHttpClient client = new OkHttpClient();
			OkHttpClient client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build();

			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			Response httpResponse = client.newCall(httpRequest).execute();
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			String responseJsonStr = httpResponse.body().string();
			OCRResponse response = objectMapper.readValue(responseJsonStr, OCRResponse.class);
			ocrInference.setResponse(response);
			schema = ocrInference;

			log.info("logging ocr inference point response" + responseJsonStr);
		

		} else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TTSInference")) {
			io.swagger.model.TTSInference ttsInference = (io.swagger.model.TTSInference) schema;
			TTSRequest request = ttsInference.getRequest();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			// OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			OkHttpClient newClient = getTrustAllCertsClient();

			Response httpResponse = newClient.newCall(httpRequest).execute();

			// Response httpResponse = client.newCall(httpRequest).execute();
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			String responseJsonStr = httpResponse.body().string();
			TTSResponse response = objectMapper.readValue(responseJsonStr, TTSResponse.class);
			ttsInference.setResponse(response);
			schema = ttsInference;

			log.info("logging tts inference point response" + responseJsonStr);
		
		} else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TransliterationInference")) {
			io.swagger.model.TransliterationInference transliterationInference = (io.swagger.model.TransliterationInference) schema;
			TransliterationRequest request = transliterationInference.getRequest();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			// OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			OkHttpClient newClient = getTrustAllCertsClient();

			Response httpResponse = newClient.newCall(httpRequest).execute();

			// Response httpResponse = client.newCall(httpRequest).execute();
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			String responseJsonStr = httpResponse.body().string();
			TransliterationResponse response = objectMapper.readValue(responseJsonStr, TransliterationResponse.class);
			transliterationInference.setResponse(response);
			schema = transliterationInference;

			log.info("logging TransliterationInference point response" + responseJsonStr);
		
		} else if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TxtLangDetectionInference")) {
			io.swagger.model.TxtLangDetectionInference txtLangDetectionInference = (io.swagger.model.TxtLangDetectionInference) schema;
			TxtLangDetectionRequest request = txtLangDetectionInference.getRequest();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			OkHttpClient newClient = getTrustAllCertsClient();

			Response httpResponse = newClient.newCall(httpRequest).execute();

			String responseJsonStr = httpResponse.body().string();
			TxtLangDetectionResponse response = objectMapper.readValue(responseJsonStr, TxtLangDetectionResponse.class);
			txtLangDetectionInference.setResponse(response);
			schema = txtLangDetectionInference;

			log.info("logging TransliterationInference point response" + responseJsonStr);
		}

		inferenceAPIEndPoint.setSchema(schema);
		return inferenceAPIEndPoint;

	}

	public InferenceAPIEndPoint validateAsyncUrl(InferenceAPIEndPoint inferenceAPIEndPoint) throws URISyntaxException,
			IOException, KeyManagementException, NoSuchAlgorithmException, InterruptedException {

		String callBackUrl = inferenceAPIEndPoint.getCallbackUrl();
		AsyncApiDetails asyncApiDetails = inferenceAPIEndPoint.getAsyncApiDetails();
		String pollingUrl = asyncApiDetails.getPollingUrl();
		Integer pollInterval = asyncApiDetails.getPollInterval();

		OneOfAsyncApiDetailsAsyncApiSchema asyncApiSchema = asyncApiDetails.getAsyncApiSchema();
		OneOfAsyncApiDetailsAsyncApiPollingSchema asyncApiPollingSchema = asyncApiDetails.getAsyncApiPollingSchema();

		if (asyncApiSchema.getClass().getName().equalsIgnoreCase("io.swagger.model.TranslationAsyncInference")) {
			io.swagger.model.TranslationAsyncInference translationAsyncInference = (io.swagger.model.TranslationAsyncInference) asyncApiSchema;
			TranslationRequest request = translationAsyncInference.getRequest();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			Response httpResponse = okHttpClientPostCall(requestJson, callBackUrl);
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			String responseJsonStr = httpResponse.body().string();

			PollingRequest response = objectMapper.readValue(responseJsonStr, PollingRequest.class);
			translationAsyncInference.setResponse(response);

			while (true) {
				Thread.sleep(pollInterval);
				String pollRequestJson = objectMapper.writeValueAsString(response);
				Response pollHttpResponse = okHttpClientPostCall(pollRequestJson, pollingUrl);
				if (pollHttpResponse.code() == 202) {
					continue;
				} else if (pollHttpResponse.code() == 200) {

					String pollResponseJsonStr = pollHttpResponse.body().string();
					log.info(pollResponseJsonStr);
					TranslationResponse translationResponse = objectMapper.readValue(pollResponseJsonStr,
							TranslationResponse.class);
					io.swagger.model.TranslationAsyncPollingInference translationAsyncPollingInference = (io.swagger.model.TranslationAsyncPollingInference) asyncApiPollingSchema;

					translationAsyncPollingInference.setRequest(response);
					translationAsyncPollingInference.setResponse(translationResponse);

					asyncApiDetails.setAsyncApiPollingSchema(translationAsyncPollingInference);

					break;

				} else {
					throw new ModelComputeException("Model Submit Failed", "Model Submit Failed",
							HttpStatus.INTERNAL_SERVER_ERROR);

				}
			}
			asyncApiDetails.asyncApiSchema(translationAsyncInference);
			inferenceAPIEndPoint.setAsyncApiDetails(asyncApiDetails);

		}
		return inferenceAPIEndPoint;
	}

	public Response okHttpClientPostCall(String requestJson, String url)
			throws IOException, KeyManagementException, NoSuchAlgorithmException {

		// OkHttpClient client = new OkHttpClient();

		/*
		 * OkHttpClient client = new OkHttpClient.Builder() .readTimeout(60,
		 * TimeUnit.SECONDS) .build();
		 */

		RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
		Request httpRequest = new Request.Builder().url(url).post(body).build();

		OkHttpClient newClient = getTrustAllCertsClient();
		Response httpResponse = newClient.newCall(httpRequest).execute();

		return httpResponse;
	}

	public InferenceAPIEndPoint validateCallBackUrl(InferenceAPIEndPoint inferenceAPIEndPoint)
			throws URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException,
			InterruptedException {

		if (inferenceAPIEndPoint.isIsSyncApi()) {
			inferenceAPIEndPoint = validateSyncCallBackUrl(inferenceAPIEndPoint);
		} else {
			inferenceAPIEndPoint = validateAsyncUrl(inferenceAPIEndPoint);
		}

		return inferenceAPIEndPoint;

	}

	public ModelComputeResponse computeAsyncModel(InferenceAPIEndPoint inferenceAPIEndPoint,
			ModelComputeRequest compute)
			throws KeyManagementException, NoSuchAlgorithmException, IOException, InterruptedException {
		ModelComputeResponse response = new ModelComputeResponseTranslation();

		String callBackUrl = inferenceAPIEndPoint.getCallbackUrl();
		AsyncApiDetails asyncApiDetails = inferenceAPIEndPoint.getAsyncApiDetails();
		String pollingUrl = asyncApiDetails.getPollingUrl();
		Integer pollInterval = asyncApiDetails.getPollInterval();

		log.info("callBackUrl :: " + callBackUrl);
		log.info("pollingUrl :: " + pollingUrl);

		OneOfAsyncApiDetailsAsyncApiSchema asyncApiSchema = asyncApiDetails.getAsyncApiSchema();
		

		if (asyncApiSchema.getClass().getName().equalsIgnoreCase("io.swagger.model.TranslationAsyncInference")) {
			io.swagger.model.TranslationAsyncInference translationAsyncInference = (io.swagger.model.TranslationAsyncInference) asyncApiSchema;

			TranslationRequest request = translationAsyncInference.getRequest();
			List<Input> input = compute.getInput();
			Sentences sentences = new Sentences();
			for (Input ip : input) {
				Sentence sentense = new Sentence();
				sentense.setSource(ip.getSource());
				sentences.add(sentense);
			}
			request.setInput(sentences);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			Response httpResponse = okHttpClientPostCall(requestJson, callBackUrl);
			
			String responseJsonStr = httpResponse.body().string();

			log.info("********* callBackUrl responseJson ****** :: " + responseJsonStr);

			PollingRequest pollingRequest = objectMapper.readValue(responseJsonStr, PollingRequest.class);
			translationAsyncInference.setResponse(pollingRequest);

			while (true) {
				Thread.sleep(pollInterval);
				String pollRequestJson = objectMapper.writeValueAsString(pollingRequest);
				Response pollHttpResponse = okHttpClientPostCall(pollRequestJson, pollingUrl);
				if (pollHttpResponse.code() == 202) {
					log.info("translation in progress");
					continue;
				} else if (pollHttpResponse.code() == 200) {

					String pollResponseJsonStr = pollHttpResponse.body().string();
					log.info(pollResponseJsonStr);
					TranslationResponse translationResponse = objectMapper.readValue(pollResponseJsonStr,
							TranslationResponse.class);

					if (translationResponse.getOutput() == null || translationResponse.getOutput().size() <= 0) {
						throw new ModelComputeException("Translation Model Compute Response Empty",
								"Translation Model Compute Response is Empty", HttpStatus.BAD_REQUEST);
					}
					List<String>  outputTextList = new ArrayList<>();
					for (Sentence sentence :translationResponse.getOutput()){
						outputTextList.add(sentence.getTarget());
					}

					//response.setOutputTextList(outputTextList);
					response = (ModelComputeResponse) translationResponse;

					break;

				} else {
					log.info("compute model failed");
					log.info("Inference end point respose received ::  " + objectMapper.writeValueAsString(pollHttpResponse));					
					throw new ModelComputeException("Translation Model Compute Failed",
							"Translation Model Compute Failed", HttpStatus.valueOf(pollHttpResponse.code()));
				}
			}
		}
		return response;
	}

	public ModelComputeResponse compute(InferenceAPIEndPoint inferenceAPIEndPoint, ModelComputeRequest computeRequest)

			throws URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException,
			InterruptedException {

		if (inferenceAPIEndPoint.isIsSyncApi()) {
			return computeSyncModel(inferenceAPIEndPoint, computeRequest);
		} else {
			return computeAsyncModel(inferenceAPIEndPoint, computeRequest);
		}

	}

	public ModelComputeResponse computeSyncModel(InferenceAPIEndPoint inferenceAPIEndPoint, ModelComputeRequest compute)

			throws URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException {

		String callBackUrl = inferenceAPIEndPoint.getCallbackUrl();
		OneOfInferenceAPIEndPointSchema schema = inferenceAPIEndPoint.getSchema();

		ModelComputeResponse response = null;

		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TranslationInference")) {
			io.swagger.model.TranslationInference translationInference = (io.swagger.model.TranslationInference) schema;
			TranslationRequest request = translationInference.getRequest();

			List<Input> input = compute.getInput();
			Sentences sentences = new Sentences();
			for (Input ip : input) {
				Sentence sentense = new Sentence();
				sentense.setSource(ip.getSource());
				sentences.add(sentense);
			}
			request.setInput(sentences);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			Response httpResponse = client.newCall(httpRequest).execute();
			if (httpResponse.code() < 200 || httpResponse.code() > 204) {

				log.info(httpResponse.toString());

				throw new ModelComputeException(httpResponse.message(), "Translation Model Compute Failed",
						HttpStatus.valueOf(httpResponse.code()));
			}
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			String responseJsonStr = httpResponse.body().string();

			TranslationResponse translation = objectMapper.readValue(responseJsonStr, TranslationResponse.class);

			if (translation.getOutput() == null || translation.getOutput().size() <= 0) {
				throw new ModelComputeException(httpResponse.message(), "Translation Model Compute Response is Empty",
						HttpStatus.BAD_REQUEST);

			}
			List<String>  outputTextList = new ArrayList<>();
			for (Sentence sentence :translation.getOutput()){
				outputTextList.add(sentence.getTarget());
			}

			ModelComputeResponseTranslation resp = new ModelComputeResponseTranslation();
			BeanUtils.copyProperties(translation, resp);
			
			response = resp;
			return response;
			
		}

		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.OCRInference")) {
			io.swagger.model.OCRInference ocrInference = (io.swagger.model.OCRInference) schema;

			ImageFiles imageFiles = new ImageFiles();
			ImageFile imageFile = new ImageFile();
			imageFile.setImageUri(compute.getImageUri());
			imageFiles.add(imageFile);

			OCRRequest request = ocrInference.getRequest();
			request.setImage(imageFiles);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			Response httpResponse = client.newCall(httpRequest).execute();
			if (httpResponse.code() < 200 || httpResponse.code() > 204) {

				log.info(httpResponse.toString());

				throw new ModelComputeException(httpResponse.message(), "OCR Model Compute Failed",
						HttpStatus.valueOf(httpResponse.code()));
			}

			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			OCRResponse ocrResponse = objectMapper.readValue(httpResponse.body().string(), OCRResponse.class);

			if (ocrResponse.getOutput() == null || ocrResponse.getOutput().size() <= 0
					|| ocrResponse.getOutput().get(0).getSource().isBlank()) {
				throw new ModelComputeException(httpResponse.message(), "OCR Model Compute Response is Empty",
						HttpStatus.BAD_REQUEST);

			}

			ModelComputeResponseOCR resp = new ModelComputeResponseOCR();
			BeanUtils.copyProperties(ocrResponse, resp);
			
			response = resp;
			return response;

		}

		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TTSInference")) {
			io.swagger.model.TTSInference ttsInference = (io.swagger.model.TTSInference) schema;

			TTSRequest request = ttsInference.getRequest();

			List<Input> input = compute.getInput();
			Sentences sentences = new Sentences();
			for (Input ip : input) {
				Sentence sentense = new Sentence();
				sentense.setSource(ip.getSource());
				sentences.add(sentense);
			}
			request.setInput(sentences);
			TTSConfig config = request.getConfig();
			config.setGender(compute.getGender());
			request.setConfig(config);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			// OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			OkHttpClient newClient = getTrustAllCertsClient();
			Response httpResponse = newClient.newCall(httpRequest).execute();

			// Response httpResponse = client.newCall(httpRequest).execute();
			if (httpResponse.code() < 200 || httpResponse.code() > 204) {

				log.info(httpResponse.toString());

				throw new ModelComputeException(httpResponse.message(), "TTS Model Compute Failed",
						HttpStatus.valueOf(httpResponse.code()));
			}

			String ttsResponseStr = httpResponse.body().string();

			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			TTSResponse ttsResponse = objectMapper.readValue(ttsResponseStr, TTSResponse.class);

			if (ttsResponse.getAudio() == null || ttsResponse.getAudio().size() <= 0) {
				throw new ModelComputeException(httpResponse.message(), "TTS Model Compute Response is Empty",
						HttpStatus.BAD_REQUEST);

			}
			
			ModelComputeResponseTTS resp = new ModelComputeResponseTTS();
			BeanUtils.copyProperties(ttsResponse, resp);
			
			response = resp;
			return response;
			
			
			/*
			if (ttsResponse.getAudio().get(0).getAudioContent() != null) {
				String encodedString = Base64.getEncoder()
						.encodeToString(ttsResponse.getAudio().get(0).getAudioContent());
				response.setOutputText(encodedString);
			} else if (!ttsResponse.getAudio().get(0).getAudioUri().isBlank()) {
				String audioUrl = ttsResponse.getAudio().get(0).getAudioUri();
				try {
					String fileName = UUID.randomUUID().toString();
					String uploadFolder = modelUploadFolder + "/model";
					String filePath = fileUtility.downloadUsingNIO(audioUrl, uploadFolder, fileName);
					byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
					String encodedString = Base64.getEncoder().encodeToString(bytes);
					response.setOutputText(encodedString);

					// delete the downloaded file
					FileUtils.delete(new File(filePath));
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new ModelComputeException(ex.getMessage(), "TTS Output file not available",
							HttpStatus.BAD_REQUEST);
				}

			} else {
				throw new ModelComputeException(httpResponse.message(), "TTS Model Compute Response is Empty",
						HttpStatus.BAD_REQUEST);
			}
			*/
		}
		
		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TransliterationInference")) {
			io.swagger.model.TransliterationInference transliterationInference = (io.swagger.model.TransliterationInference) schema;
			TransliterationRequest request = transliterationInference.getRequest();

			List<Input> input = compute.getInput();
			Sentences sentences = new Sentences();
			for (Input ip : input) {
				Sentence sentense = new Sentence();
				sentense.setSource(ip.getSource());
				sentences.add(sentense);
			}
			request.setInput(sentences);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			Response httpResponse = client.newCall(httpRequest).execute();
			if (httpResponse.code() < 200 || httpResponse.code() > 204) {

				log.info(httpResponse.toString());

				throw new ModelComputeException(httpResponse.message(), "Transliteration Model Compute Failed",
						HttpStatus.valueOf(httpResponse.code()));
			}
			// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			// false);
			String responseJsonStr = httpResponse.body().string();

			TransliterationResponse transliterationResponse = objectMapper.readValue(responseJsonStr, TransliterationResponse.class);

			if (transliterationResponse.getOutput() == null || transliterationResponse.getOutput().size() <= 0 ) {
					
				throw new ModelComputeException(httpResponse.message(), "Transliteration Model Compute Response is Empty",
						HttpStatus.BAD_REQUEST);

			}
			ModelComputeResponseTransliteration resp = new ModelComputeResponseTransliteration();
			BeanUtils.copyProperties(transliterationResponse, resp);
			
			response = resp;
			return response;
		}
		if (schema.getClass().getName().equalsIgnoreCase("io.swagger.model.TxtLangDetectionInference")) {
			io.swagger.model.TxtLangDetectionInference txtLangDetectionInference = (io.swagger.model.TxtLangDetectionInference) schema;
			TxtLangDetectionRequest request = txtLangDetectionInference.getRequest();

			List<Input> input = compute.getInput();
			Sentences sentences = new Sentences();
			for (Input ip : input) {
				Sentence sentense = new Sentence();
				sentense.setSource(ip.getSource());
				sentences.add(sentense);
			}
			request.setInput(sentences);
			
			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			OkHttpClient newClient = getTrustAllCertsClient();

			Response httpResponse = newClient.newCall(httpRequest).execute();

			String responseJsonStr = httpResponse.body().string();
			TxtLangDetectionResponse txtLangDetectionResponse = objectMapper.readValue(responseJsonStr, TxtLangDetectionResponse.class);
			
			ModelComputeResponseTxtLangDetection resp = new ModelComputeResponseTxtLangDetection();
			BeanUtils.copyProperties(txtLangDetectionResponse, resp);
			
			response = resp;
			return response;
			
		}

		return response;
	}

	/*
	 * compute for OCR model
	 */
	public ModelComputeResponse compute(String callBackUrl, OneOfInferenceAPIEndPointSchema schema, String imagePath) {

		try {
			
			ModelComputeResponse response = new ModelComputeResponseOCR();

			io.swagger.model.OCRInference ocrInference = (io.swagger.model.OCRInference) schema;

			byte[] bytes = FileUtils.readFileToByteArray(new File(imagePath));

			ImageFile imageFile = new ImageFile();
			imageFile.setImageContent(bytes);

			ImageFiles imageFiles = new ImageFiles();
			imageFiles.add(imageFile);

			OCRRequest request = ocrInference.getRequest();
			request.setImage(imageFiles);

			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(request);

			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(requestJson, MediaType.parse("application/json"));
			Request httpRequest = new Request.Builder().url(callBackUrl).post(body).build();

			Response httpResponse = client.newCall(httpRequest).execute();
			String responseJsonStr = httpResponse.body().string();

			OCRResponse ocrResponse = objectMapper.readValue(responseJsonStr, OCRResponse.class);
			if (ocrResponse != null && ocrResponse.getOutput() != null && ocrResponse.getOutput().size() > 0 && !ocrResponse.getOutput().get(0).getSource().isBlank()) {
				
				ModelComputeResponseOCR resp = new ModelComputeResponseOCR();
				BeanUtils.copyProperties(ocrResponse, resp);
				response = resp;
				
			} else {
				log.info("Ocr try me response is null or not proper");
				log.info("callBackUrl :: " + callBackUrl);
				log.info("Request Json :: " + requestJson);
				log.info("ResponseJson :: " + responseJsonStr);
				FileUtils.delete(new File(imagePath));
				throw new ModelComputeException("Model unable to infer the image", "Model unable to infer the image", HttpStatus.INTERNAL_SERVER_ERROR);

			}
			return response;
			
		}catch(Exception ex) {
			
			throw new ModelComputeException(ex.getMessage(), "Model unable to infer the image", HttpStatus.INTERNAL_SERVER_ERROR);
			
		}finally {
			try {
				FileUtils.delete(new File(imagePath));
			} catch (IOException e) {
				log.info("Unable to delete the file : " + imagePath);
				e.printStackTrace();
			}
		}
	}

	public static OkHttpClient getTrustAllCertsClient() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
		} };

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

		OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
		newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
		newBuilder.hostnameVerifier((hostname, session) -> true);
		return newBuilder.readTimeout(60, TimeUnit.SECONDS).build();
	}

}
