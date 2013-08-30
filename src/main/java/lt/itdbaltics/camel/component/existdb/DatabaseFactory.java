package lt.itdbaltics.camel.component.existdb;

import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;

public class DatabaseFactory {
    private static DatabaseImpl database;

    public DatabaseFactory() throws Exception {
        if (database == null) {
            database = new DatabaseImpl();
            database.setProperty("create-database", "true");
            database.setProperty("configuration", "conf.xml");

            DatabaseManager.registerDatabase(database);

            //FIXME: Externalize database URL
            DatabaseManager.getCollection("xmldb:exist:///db");
        }
    }

    public void destroy() {
        if (database != null) {
            try {
                DatabaseManager.deregisterDatabase(database);
                database = null;
            } catch (Exception ex) {

            }
        }
    }
}
