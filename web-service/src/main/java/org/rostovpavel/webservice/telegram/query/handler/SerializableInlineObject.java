package org.rostovpavel.webservice.telegram.query.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.rostovpavel.webservice.telegram.query.SerializableInlineType;

import java.util.Objects;

@Getter
@Setter
public abstract class SerializableInlineObject {

  @JsonProperty("i")
  private int index;

  public SerializableInlineObject(SerializableInlineType type) {
    this.index = type.getIndex();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SerializableInlineObject that = (SerializableInlineObject) o;
    return index == that.index;
  }

  @Override
  public int hashCode() {
    return Objects.hash( index);
  }
}
