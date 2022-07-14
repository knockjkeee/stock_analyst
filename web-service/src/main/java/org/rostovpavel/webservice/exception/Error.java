package org.rostovpavel.webservice.exception;


import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Error {
    String error;
}
