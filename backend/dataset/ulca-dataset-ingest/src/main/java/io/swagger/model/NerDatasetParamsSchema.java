package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.model.DatasetCommonParamsSchema;
import io.swagger.model.DatasetType;
import io.swagger.model.Domain;
import io.swagger.model.LanguagePair;
import io.swagger.model.License;
import io.swagger.model.NerDatasetCollectionMethod;
import io.swagger.model.Source;
import io.swagger.model.Submitter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NerDatasetParamsSchema
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-11-25T09:44:34.039Z[GMT]")


public class NerDatasetParamsSchema extends DatasetCommonParamsSchema implements OneOfDatasetParamsSchemaParams {
  /**
   * The accepted standard based on which NER tags are labelled in the dataset.
   */
  public enum TagsFormatEnum {
    CONLL_2003("CoNLL-2003");

    private String value;

    TagsFormatEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TagsFormatEnum fromValue(String text) {
      for (TagsFormatEnum b : TagsFormatEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("tagsFormat")
  private TagsFormatEnum tagsFormat = null;

  @JsonProperty("collectionMethod")
  private NerDatasetCollectionMethod collectionMethod = null;

  public NerDatasetParamsSchema tagsFormat(TagsFormatEnum tagsFormat) {
    this.tagsFormat = tagsFormat;
    return this;
  }

  /**
   * The accepted standard based on which NER tags are labelled in the dataset.
   * @return tagsFormat
   **/
  @Schema(required = true, description = "The accepted standard based on which NER tags are labelled in the dataset.")
      @NotNull

    public TagsFormatEnum getTagsFormat() {
    return tagsFormat;
  }

  public void setTagsFormat(TagsFormatEnum tagsFormat) {
    this.tagsFormat = tagsFormat;
  }

  public NerDatasetParamsSchema collectionMethod(NerDatasetCollectionMethod collectionMethod) {
    this.collectionMethod = collectionMethod;
    return this;
  }

  /**
   * Get collectionMethod
   * @return collectionMethod
   **/
  @Schema(description = "")
  
    @Valid
    public NerDatasetCollectionMethod getCollectionMethod() {
    return collectionMethod;
  }

  public void setCollectionMethod(NerDatasetCollectionMethod collectionMethod) {
    this.collectionMethod = collectionMethod;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NerDatasetParamsSchema nerDatasetParamsSchema = (NerDatasetParamsSchema) o;
    return Objects.equals(this.tagsFormat, nerDatasetParamsSchema.tagsFormat) &&
        Objects.equals(this.collectionMethod, nerDatasetParamsSchema.collectionMethod) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagsFormat, collectionMethod, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NerDatasetParamsSchema {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    tagsFormat: ").append(toIndentedString(tagsFormat)).append("\n");
    sb.append("    collectionMethod: ").append(toIndentedString(collectionMethod)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
