package lt.itdbaltics.camel.component.existdb;

import org.apache.camel.util.ObjectHelper;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XUpdateQueryService;

import java.util.Vector;

public class QueryService {
    private Collection collection;
    private QueryServiceType serviceType;

    protected QueryService(Collection collection, QueryServiceType serviceType){
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

    public Object query(String xpath) throws XMLDBException {
        ResourceIterator resourceIterator = createResourceIterator(xpath);
        Vector<Object> result = new Vector<Object>();
        Object answer = null;

        while (resourceIterator.hasMoreResources()) {
            Resource resource = resourceIterator.nextResource();

            if (resource instanceof XMLResource) {
                result.add(((XMLResource) resource).getContentAsDOM());
            } else {
                result.add(resource.getContent());
            }

            try {
                ((EXistResource) resource).freeResources();
            } catch (XMLDBException ex) {}
        }

        if (result.size() == 1) {
            answer = result.get(0);
        } else if (result.size() > 1) {
            answer = result;
        }

        return answer;
    }

    public Object update(String queryExpression) throws XMLDBException {
        XUpdateQueryService service =
                (XUpdateQueryService) collection.getService(serviceType.getServiceName(),
                        serviceType.getVersion());
        service.setProperty("indent", "yes");


        return service.update(queryExpression);
    }
}
