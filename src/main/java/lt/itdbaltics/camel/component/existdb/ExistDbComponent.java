package lt.itdbaltics.camel.component.existdb;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

public class ExistDbComponent extends DefaultComponent {
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ExistDbEndpoint endpoint = new ExistDbEndpoint(uri, remaining, this);
        setProperties(endpoint, parameters);

        return endpoint;
    }
}
