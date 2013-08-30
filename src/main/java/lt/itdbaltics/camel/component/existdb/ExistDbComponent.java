package lt.itdbaltics.camel.component.existdb;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

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
