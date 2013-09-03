package lt.itdbaltics.camel.component.existdb;

import org.apache.camel.util.ObjectHelper;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

import java.util.Vector;

public class QueryService {
    private Collection collection;
    private QueryServiceType serviceType;

    public QueryService(Collection collection, QueryServiceType serviceType){
        this.collection = collection;
        this.serviceType = serviceType;
    }

    private ResourceIterator createResourceIterator(String query) throws XMLDBException {
        ResourceSet resourceSet;

        if (query == null) {
            ObjectHelper.notNull(query, "query");
        }

        if (serviceType.equals(QueryServiceType.XPATH)) {
            XPathQueryService service = (XPathQueryService) collection.getService(serviceType.getServiceName(),
                        serviceType.getVersion());
            service.setProperty("indent", "yes");

            resourceSet = service.query(query);
        } else if (serviceType.equals(QueryServiceType.XQUERY)) {
            XQueryService service = (XQueryService) collection.getService(serviceType.getServiceName(),
                    serviceType.getVersion());
            service.setProperty("indent", "yes");

            resourceSet = service.query(query);
        } else {
            throw new IllegalArgumentException("Unsupported QueryService type is requested. " + serviceType);
        }

        return resourceSet.getIterator();
    }

    public Vector<Object> query(String xpath) throws XMLDBException {
        ResourceIterator resourceIterator = createResourceIterator(xpath);
        Vector<Object> result = new Vector<Object>();

        while (resourceIterator.hasMoreResources()) {
            Resource resource = resourceIterator.nextResource();
            result.add(resource.getContent());

            try {
                ((EXistResource) resource).freeResources();
            } catch (XMLDBException ex) {}
        }

        return result;
    }
}
