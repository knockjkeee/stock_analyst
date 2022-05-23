package org.rostovpavel.base.models;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;


@Value
@Builder
public class Stock {
   // @SerializedName("open")
    BigDecimal open;
//    @SerializedName("high")
    BigDecimal high;
//    @SerializedName("low")
    BigDecimal low;
//    @SerializedName("close")
    BigDecimal close;
//    @SerializedName("volume")
    long volume;
//    @SerializedName("date")
    String date;
}
