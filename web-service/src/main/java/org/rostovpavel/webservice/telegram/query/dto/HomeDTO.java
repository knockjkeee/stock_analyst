package org.rostovpavel.webservice.telegram.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.rostovpavel.webservice.telegram.query.SerializableInlineType;
import org.rostovpavel.webservice.telegram.query.handler.SerializableInlineObject;

@Getter
@Setter
public class HomeDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private Long tickerId;

  public HomeDTO() {
    super(SerializableInlineType.HOME);
  }

  public HomeDTO(Long tickerId) {
    this();
    this.tickerId = tickerId;
  }
}
