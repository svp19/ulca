package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * specifies how the dataset is curated.
 */
@Schema(description = "specifies how the dataset is curated.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-11-25T09:44:34.039Z[GMT]")


public class GlossaryDatasetCollectionMethod   {
  /**
   * Gets or Sets collectionDescription
   */
  public enum CollectionDescriptionEnum {
    AUTO_ALIGNED("auto-aligned"),
    
    MANUAL_CURATED("manual-curated"),
    
    MACHINE_GENERATED("machine-generated"),
    
    MACHINE_GENERATED_POST_EDITED("machine-generated-post-edited");

    private String value;

    CollectionDescriptionEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CollectionDescriptionEnum fromValue(String text) {
      for (CollectionDescriptionEnum b : CollectionDescriptionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("collectionDescription")
  @Valid
  private List<CollectionDescriptionEnum> collectionDescription = new ArrayList<CollectionDescriptionEnum>();

  @JsonProperty("collectionDetails")
  private OneOfGlossaryDatasetCollectionMethodCollectionDetails collectionDetails = null;

  public GlossaryDatasetCollectionMethod collectionDescription(List<CollectionDescriptionEnum> collectionDescription) {
    this.collectionDescription = collectionDescription;
    return this;
  }

  public GlossaryDatasetCollectionMethod addCollectionDescriptionItem(CollectionDescriptionEnum collectionDescriptionItem) {
    this.collectionDescription.add(collectionDescriptionItem);
    return this;
  }

  /**
   * various collection methods user have used to create the dataset
   * @return collectionDescription
   **/
  @Schema(required = true, description = "various collection methods user have used to create the dataset")
      @NotNull

    public List<CollectionDescriptionEnum> getCollectionDescription() {
    return collectionDescription;
  }

  public void setCollectionDescription(List<CollectionDescriptionEnum> collectionDescription) {
    this.collectionDescription = collectionDescription;
  }

  public GlossaryDatasetCollectionMethod collectionDetails(OneOfGlossaryDatasetCollectionMethodCollectionDetails collectionDetails) {
    this.collectionDetails = collectionDetails;
    return this;
  }

  /**
   * Get collectionDetails
   * @return collectionDetails
   **/
  @Schema(description = "")
  
    public OneOfGlossaryDatasetCollectionMethodCollectionDetails getCollectionDetails() {
    return collectionDetails;
  }

  public void setCollectionDetails(OneOfGlossaryDatasetCollectionMethodCollectionDetails collectionDetails) {
    this.collectionDetails = collectionDetails;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GlossaryDatasetCollectionMethod glossaryDatasetCollectionMethod = (GlossaryDatasetCollectionMethod) o;
    return Objects.equals(this.collectionDescription, glossaryDatasetCollectionMethod.collectionDescription) &&
        Objects.equals(this.collectionDetails, glossaryDatasetCollectionMethod.collectionDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(collectionDescription, collectionDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GlossaryDatasetCollectionMethod {\n");
    
    sb.append("    collectionDescription: ").append(toIndentedString(collectionDescription)).append("\n");
    sb.append("    collectionDetails: ").append(toIndentedString(collectionDetails)).append("\n");
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
