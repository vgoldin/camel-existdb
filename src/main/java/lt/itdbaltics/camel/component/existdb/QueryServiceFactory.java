package lt.itdbaltics.camel.component.existdb;

import org.xmldb.api.base.Collection;

public class QueryServiceFactory {
    public QueryService createQueryService(Collection collection, QueryServiceType serviceType) {
        return new QueryService(collection, serviceType);
    }
}
