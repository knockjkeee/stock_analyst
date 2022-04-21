package org.rostovpavel.models;

import com.google.protobuf.Timestamp;
//import com.google.type.Date;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;


@Value
@Builder
public class Stock {
    BigDecimal open;
    BigDecimal close;
    BigDecimal high;
    BigDecimal low;
    long volume;
    String date;
}
