package org.rostovpavel.webservice.telegram.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.rostovpavel.webservice.telegram.query.handler.SerializableInlineObject;
import org.rostovpavel.webservice.telegram.query.SerializableInlineType;

@Getter
@Setter
public class IndicatorDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private Long tickerId;

  public IndicatorDTO() {
    super(SerializableInlineType.INDICATOR);
  }

  public IndicatorDTO(Long tickerId) {
    this();
    this.tickerId = tickerId;
  }
}
