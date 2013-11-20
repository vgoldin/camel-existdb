package lt.itdbaltics.camel.component.existdb;

import org.exist.xmldb.DatabaseImpl;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.XmldbURI;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;

public class DatabaseFactory {
    private static DatabaseImpl database;
    private static Collection current;
    private static Boolean started = false;

    public DatabaseFactory() throws Exception {
        synchronized (started) {
            if (database == null) {
                database = new DatabaseImpl();
                database.setProperty("create-database", "true");

                current = database.getCollection(XmldbURI.EMBEDDED_SERVER_URI, null, null);
                DatabaseManager.registerDatabase(database);
                started  = true;
            }
        }
    }

    public void destroy() {
        synchronized (started) {
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
                    started = false;
                } catch (Exception ex) {
                    // -- ignore
                }
            }
        }
    }
}
