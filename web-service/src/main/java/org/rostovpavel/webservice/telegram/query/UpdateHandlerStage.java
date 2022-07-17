package org.rostovpavel.webservice.telegram.query;

public enum UpdateHandlerStage {
    CALLBACK,
   ;

    public int getOrder() {
        return ordinal();
    }
}
