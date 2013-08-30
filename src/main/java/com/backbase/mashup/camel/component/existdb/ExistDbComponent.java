package com.backbase.mashup.camel.component.existdb;

import java.net.URI;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

public class ExistDbComponent extends DefaultComponent {
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ExistDbEndpoint endpoint = new ExistDbEndpoint(uri, remaining, this);
        endpoint.setUsername((String) parameters.get("username"));
        endpoint.setPassword((String) parameters.get("password"));
        endpoint.setXPath((String) parameters.get("XPath"));

        return endpoint;
    }
}
