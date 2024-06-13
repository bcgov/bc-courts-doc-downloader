package ca.bc.gov.ag.courts.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OrdsPushResponse
 */

@JsonTypeName("ordsPush_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-06-13T14:46:58.355951900-07:00[America/Vancouver]", comments = "Generator version: 7.6.0")
public class OrdsPushResponse {

  private String filename;

  private String mimetype;

  private String sizeval;

  public OrdsPushResponse filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Get filename
   * @return filename
  */
  
  @Schema(name = "filename", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filename")
  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public OrdsPushResponse mimetype(String mimetype) {
    this.mimetype = mimetype;
    return this;
  }

  /**
   * Get mimetype
   * @return mimetype
  */
  
  @Schema(name = "mimetype", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mimetype")
  public String getMimetype() {
    return mimetype;
  }

  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  public OrdsPushResponse sizeval(String sizeval) {
    this.sizeval = sizeval;
    return this;
  }

  /**
   * Get sizeval
   * @return sizeval
  */
  
  @Schema(name = "sizeval", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sizeval")
  public String getSizeval() {
    return sizeval;
  }

  public void setSizeval(String sizeval) {
    this.sizeval = sizeval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrdsPushResponse ordsPushResponse = (OrdsPushResponse) o;
    return Objects.equals(this.filename, ordsPushResponse.filename) &&
        Objects.equals(this.mimetype, ordsPushResponse.mimetype) &&
        Objects.equals(this.sizeval, ordsPushResponse.sizeval);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename, mimetype, sizeval);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrdsPushResponse {\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    mimetype: ").append(toIndentedString(mimetype)).append("\n");
    sb.append("    sizeval: ").append(toIndentedString(sizeval)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

