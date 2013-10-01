package lt.itdbaltics.camel.component.existdb;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.exist.xmldb.EXistResource;
import org.w3c.dom.Node;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;

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
            String xmldbServiceType =  exchange.getIn().getHeader("XMLDB_SERVICE_TYPE", String.class);

            if (xPath != null) {
                QueryService service = endpoint.getQueryServiceFactory().createQueryService(collection, QueryServiceType.XPATH);

                Object results = service.query(xPath);
                exchange.getIn().setBody(results);
            } else if (xmldbServiceType != null) {
                QueryServiceType serviceType = QueryServiceType.valueOf(xmldbServiceType);
                QueryService service = endpoint.getQueryServiceFactory().createQueryService(collection, serviceType);

                String queryExpression = exchange.getIn().getBody(String.class);
                if (queryExpression == null) {
                    throw new RuntimeCamelException("Exchange body is empty. Expected XMLDB query expression.");
                }

                Object results;
                if (serviceType.equals(QueryServiceType.XUPDATE)) {
                    results = service.update(queryExpression);
                } else {
                    results = service.query(queryExpression);
                }

                exchange.getIn().setBody(results);
            } else {
                //FIXME: Make storing explicit by providing a camel header with the operation name
                resource = storeResource(exchange, collection);
            }
        } finally {
            if (resource != null) {
                try {
                    if (resource instanceof EXistResource) {
                        ((EXistResource) resource).freeResources();
                    }
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
            try {
                if (document instanceof EXistResource) {
                    ((EXistResource) document).freeResources();
                }
            } catch (XMLDBException ex) {}
            try { collection.close(); } catch (XMLDBException ex) {}
        }

        return document;
    }

}
