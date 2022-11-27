package org.rostovpavel.webservice.telegram.query.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.rostovpavel.webservice.telegram.query.command.Command;

@Data
@RequiredArgsConstructor
public class ParseDTO {
    private final Command command;
    private final String text;
}