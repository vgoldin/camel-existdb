package com.backbase.mashup.camel.component.existdb;

import org.xmldb.api.base.XMLDBException;

public class CamelExistDbException extends RuntimeException {
    private static final long serialVersionUID = -1282037397443288060L;

    public CamelExistDbException(XMLDBException ex) {
        super(ex);
    }
    
}
