import lt.itdbaltics.camel.component.existdb.DatabaseFactory;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;


public class ExistRouteTest extends CamelTestSupport {
    private DatabaseFactory factory;

    @Test
    public void testSendMessagesIntoEsper() throws Exception {
        template.sendBody("direct:start", "<account partyId='3'><bban>Тест</bban></account>");

        MockEndpoint endpoint = getMockEndpoint("mock:results");
        endpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:query", "", "partyId", "3");
        assertMockEndpointsSatisfied();


        List<Exchange> list = endpoint.getReceivedExchanges();
        for (Exchange exchange : list) {
            Object value = exchange.getIn().getBody();
            log.info("Received {}", value);
        }
    }

    @Override
    public void setUp() throws Exception {
        factory = new DatabaseFactory();
        factory.setConfiguration("conf.xml");
        factory.start();

        super.setUp();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start").to("xmldb:exist:///db/test/accounts?username=admin&password=");
                from("direct:query").to("xmldb:exist:///db/test/accounts?XPath=/account[@partyId = '3']&username=admin&password=").to("log:debug?showAll=true").to("mock:results");
            }
        };
    }

    @Override
    public void tearDown() {
        factory.destroy();
    }
}
