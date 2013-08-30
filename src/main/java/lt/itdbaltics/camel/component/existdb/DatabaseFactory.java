package lt.itdbaltics.camel.component.existdb;

import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;

public class DatabaseFactory {
    private static DatabaseImpl database;

    public DatabaseFactory() throws Exception {
        if (database == null) {
            database = new DatabaseImpl();
            database.setProperty("create-database", "true");
            database.setProperty("configuration", "conf.xml");

            DatabaseManager.registerDatabase(database);
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
