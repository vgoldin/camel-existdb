package lt.itdbaltics.camel.component.existdb;

import org.exist.xmldb.DatabaseImpl;
import org.exist.xmldb.DatabaseInstanceManager;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;

public class DatabaseFactory {
    private static DatabaseImpl database;
    private static Collection current;

    public DatabaseFactory() throws Exception {
        if (database == null) {
            database = new DatabaseImpl();
            database.setProperty("create-database", "true");
            database.setProperty("configuration", "conf.xml");

            DatabaseManager.registerDatabase(database);

            //FIXME: Externalize database URL
            current = DatabaseManager.getCollection("xmldb:exist:///db");
        }
    }

    public void destroy() {
        if (database != null) {
            try {
                if (current != null) {
                    final DatabaseInstanceManager mgr = (DatabaseInstanceManager) current.getService("DatabaseInstanceManager", "1.0");

                    if (mgr != null && mgr.isLocalInstance()) {
                        mgr.shutdown();
                    }
                }

                DatabaseManager.deregisterDatabase(database);
                database = null;
            } catch (Exception ex) {
                // -- ignore
            }
        }
    }
}
