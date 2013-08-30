package lt.itdbaltics.camel.component.existdb;

import org.apache.camel.util.ObjectHelper;
import org.exist.xmldb.EXistResource;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;

import java.util.Vector;

public class QueryService {
    private static final String QUERY_SERVICE_VERSION = "1.0";
    private static final String QUERY_SERVICE_NAME = "XPathQueryService";

    private Collection collection;
    private XPathQueryService service;

    public QueryService(Collection collection){
        this.collection = collection;

        initQueryService();
    }

    private XPathQueryService initQueryService() {
        try {
            service = (XPathQueryService) collection.getService(QUERY_SERVICE_NAME, QUERY_SERVICE_VERSION);
            service.setProperty("indent", "yes");

            return service;
        } catch (XMLDBException ex) {
            throw new CamelExistDbException(ex);
        }
    }

    private ResourceIterator createResourceIterator(String xpath) throws XMLDBException {
        ResourceIterator iterator = null;

        if (xpath != null) {
            ResourceSet resourceSet = service.query(xpath);
            iterator = resourceSet.getIterator();
        } else {
            ObjectHelper.notNull(xpath, "xpath");
        }

        return iterator;
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
