package lt.itdbaltics.camel.component.existdb;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultPollingEndpoint;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import java.net.URI;

public class ExistDbEndpoint extends DefaultPollingEndpoint {
    private String xpath;
    private String username;
    private String password;
    private String databaseUri;
    private String collectionUri;

    public ExistDbEndpoint(String uri, String remaining, ExistDbComponent component) throws Exception {
        super(uri, component);

        collectionUri = new URI(remaining).getPath();
        databaseUri = "xmldb:" + remaining.replaceFirst(collectionUri, "");
    }
    
    public Producer createProducer() throws Exception {
        return new ExistDbProducer(this);    
    }

    protected Collection getCollection() throws XMLDBException {
        return getOrCreateCollection(collectionUri, 0);
    }

    public boolean isSingleton() {
        return true;
    }

    public void setXPath(String xpath) {
        this.xpath = xpath;
    }
    
    public String getXPath() {
        return xpath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    private Collection getOrCreateCollection(String collectionUri, int pathSegmentOffset) throws
            XMLDBException {

        Collection col =
                DatabaseManager.getCollection(databaseUri + collectionUri, username, password);

        if (col == null) {
            if (collectionUri.startsWith("/")) {
                collectionUri = collectionUri.substring(1);
            }

            String pathSegments[] = collectionUri.split("/");

            if (pathSegments.length > 0) {
                StringBuilder path = new StringBuilder();

                for (int i = 0; i <= pathSegmentOffset; i++) {
                    path.append("/");
                    path.append(pathSegments[i]);
                }

                Collection start = DatabaseManager.getCollection(databaseUri + path, username, password);

                if (start == null) {
                    //collection does not exist, so create
                    String parentPath = path.substring(0, path.lastIndexOf("/"));

                    Collection parent = DatabaseManager.getCollection(databaseUri + parentPath, username, password);
                    CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");

                    col = mgt.createCollection(pathSegments[pathSegmentOffset]);
                    col.close();

                    parent.close();
                } else {
                    start.close();
                }
            }

            col = getOrCreateCollection("/" + collectionUri, ++pathSegmentOffset);
        }

        return col;
    }
}
