package com.backbase.mashup.camel.component.existdb;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.exist.xmldb.EXistResource;
import org.w3c.dom.Node;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XQueryService;

import java.util.Vector;

public class ExistDbProducer extends DefaultProducer {
    private ExistDbEndpoint endpoint;
    
    public ExistDbProducer(ExistDbEndpoint endpoint) {
        super(endpoint);
        
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        Collection collection = null;
        Resource resource = null;

        try {
            collection = endpoint.getCollection();

            String xPath = endpoint.getXPath();
            if (xPath != null) {
                QueryService service = new QueryService(collection);

                Vector<Object> results = service.query(xPath);
                exchange.getIn().setBody(results);
            } else {
                resource = storeResource(exchange, collection);
            }
        } finally {
            if (resource != null) {
                try {
                    ((EXistResource) resource).freeResources();
                } catch (XMLDBException ex) {
                    // -- ignore
                }
            }

            if (collection != null) {
                try {
                    collection.close();
                } catch (XMLDBException ex) {
                    // -- ignore
                }
            }
        }
    }

    private XMLResource storeResource(Exchange exchange, Collection collection) throws XMLDBException {
        XMLResource document;
        document = (XMLResource) collection.createResource(null, "XMLResource");
        Object body = exchange.getIn().getBody();

        if (body instanceof Node) {
            document.setContentAsDOM((Node) body);
        } else {
            document.setContent(body);
        }

        try {
            collection.storeResource(document);
        } finally {
            if (document != null) {
                try { ((EXistResource) document).freeResources(); } catch (XMLDBException ex) {}
            }

            if (collection != null) {
                try { collection.close(); } catch (XMLDBException ex) {}
            }
        }

        return document;
    }

}
