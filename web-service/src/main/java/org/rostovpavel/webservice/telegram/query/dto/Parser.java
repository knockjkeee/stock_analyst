package org.rostovpavel.webservice.telegram.query.dto;

import java.util.Optional;

public interface Parser {
    Optional<ParseDTO> parseCommand(String msg);
}
