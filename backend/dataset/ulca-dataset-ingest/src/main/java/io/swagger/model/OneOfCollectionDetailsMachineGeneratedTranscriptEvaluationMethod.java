package io.swagger.model;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
/**
* OneOfCollectionDetailsMachineGeneratedTranscriptEvaluationMethod
*/
//commented as we are deserializing manually to have better control on deserializer
/*
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = TranscriptionEvaluationMethod1.class, name = "TranscriptionEvaluationMethod1")
})
*/
public interface OneOfCollectionDetailsMachineGeneratedTranscriptEvaluationMethod {

}
