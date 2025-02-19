package io.swagger.model;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
/**
* OneOfCollectionDetailsMachineTranslatedEvaluationMethod
*/
//commented as we are deserializing manually to have better control on deserializer
/*
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MTAutomaticEvaluationMethod.class, name = "MTAutomaticEvaluationMethod"),
  @JsonSubTypes.Type(value = MachineTranslatedEvaluationMethod2.class, name = "MachineTranslatedEvaluationMethod2")
})
*/
public interface OneOfCollectionDetailsMachineTranslatedEvaluationMethod {

}
