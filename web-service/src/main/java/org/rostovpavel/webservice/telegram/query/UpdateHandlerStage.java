package org.rostovpavel.webservice.telegram.query;

public enum UpdateHandlerStage {
    CALLBACK,
    COMMAND,
   ;

    public int getOrder() {
        return ordinal();
    }
}
