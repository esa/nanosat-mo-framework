/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.com.impl.archive.db;

import esa.mo.com.impl.provider.ArchiveManager;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class that bridges the COM Archive logic to the actual database backend.
 *
 * @author Cesar Coelho
 */
public class DatabaseBackend {

    public static final Logger LOGGER = Logger.getLogger(ArchiveManager.class.getName());

    private static final String DRIVER_CLASS_NAME = "org.sqlite.JDBC"; // SQLite JDBC Driver

    private static final String DATABASE_NAME = "sqlite"; // SQLite

    private static final String DATABASE_LOCATION_NAME = "comArchive.db";

    private static final String MEMORY_DB_OPTION = ":memory:";

    // true for fairness, because we want FIFO
    private final Semaphore availability = new Semaphore(0, true);

    private final String jdbcDriver;

    private final String url;

    private final String user;

    private final String password;

    private Connection serverConnection;

    private boolean indexCreated = false;

    public boolean isPostgres = false;

    // Prepared statements storage for currently active connection
    private PreparedStatements preparedStatements;

    public DatabaseBackend() {
        String urlP = System.getProperty("esa.nmf.archive.persistence.jdbc.url");

        if (null != urlP && !"".equals(urlP)) {
            this.url = urlP;
        } else {
            File dbFile = new File(DATABASE_LOCATION_NAME);
            boolean writableFs = true;
            // Check if we are operating on Read-Only FS
            if (dbFile.exists()) {
                if (!dbFile.canWrite()) {
                    writableFs = false;
                }
            } else {
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    writableFs = false;
                }
            }
            if (writableFs) {
                LOGGER.info("DB file writable - defaulting to non-volatile COM Archive");
                this.url = "jdbc:" + DATABASE_NAME + ":" + DATABASE_LOCATION_NAME;
            } else {
                LOGGER.info("DB file not writable - defaulting to in-memory COM Archive");
                this.url = "jdbc:" + DATABASE_NAME + ":" + MEMORY_DB_OPTION;
            }
        }

        String driver = System.getProperty("esa.nmf.archive.persistence.jdbc.driver");
        this.jdbcDriver = (null != driver && !"".equals(driver)) ? driver : DRIVER_CLASS_NAME;
        String userP = System.getProperty("esa.nmf.archive.persistence.jdbc.user");
        this.user = (null != userP && !"".equals(userP)) ? userP : null;
        String pass = System.getProperty("esa.nmf.archive.persistence.jdbc.password");
        this.password = (null != pass && !"".equals(pass)) ? pass : null;
    }

    public Semaphore getAvailability() {
        return availability;
    }

    public Connection getConnection() {
        return serverConnection;
    }

    public PreparedStatements getPreparedStatements() {
        return preparedStatements;
    }

    /**
     * Starts the database backend by starting the Database Driver, check if a
     * migration to the new tables is needed, and creates the main COM Objects
     * table if it does not exist.
     *
     * @param dbProcessor The transactions processor.
     */
    public void startBackendDatabase(final TransactionsProcessor dbProcessor) {
        availability.release();
        startDatabaseDriver(this.url, this.user, this.password);

        try {
            checkIfMigrationNeeded();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseBackend.class.getName()).log(Level.FINE, "Migration not needed...");
        }

        try {
            String blobType = "BLOB";
            if (this.url.contains("postgresql")) {
                blobType = "bytea";
                isPostgres = true;
            }
            Statement query = serverConnection.createStatement();
            query.execute("CREATE TABLE IF NOT EXISTS COMObjectEntity (objectTypeId INTEGER NOT NULL, objId BIGINT NOT NULL, domainId INTEGER NOT NULL, network INTEGER, objBody " + blobType + ", providerURI INTEGER, relatedLink BIGINT, sourceLinkDomainId INTEGER, sourceLinkObjId BIGINT, sourceLinkObjectTypeId INTEGER, timestampArchiveDetails BIGINT, PRIMARY KEY (objectTypeId, objId, domainId))");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseBackend.class.getName()).log(Level.SEVERE, null, ex);
        }

        preparedStatements = new PreparedStatements(serverConnection);
        try {
            preparedStatements.init(isPostgres);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Could not prepare the SQL statements. Application will exit!", ex);
            System.exit(0);
        }

    }

    private void checkIfMigrationNeeded() throws SQLException {
        Statement mig = serverConnection.createStatement();
        // The first query will throw exception to the outside
        mig.execute("ALTER TABLE COMObjectEntity RENAME COLUMN OBJ TO objBody");

        Logger.getLogger(TransactionsProcessor.class.getName()).log(Level.INFO,
            "Migrating the database to new Table schemas...");
        try {
            mig.execute("ALTER TABLE DomainHolderEntity RENAME COLUMN domainString TO value");
            mig.execute("ALTER TABLE DomainHolderEntity RENAME TO FastDomain");
            mig.execute("ALTER TABLE NetworkHolderEntity RENAME COLUMN networkString TO value");
            mig.execute("ALTER TABLE NetworkHolderEntity RENAME TO FastNetwork");
            mig.execute("ALTER TABLE ObjectTypeHolderEntity RENAME COLUMN objectType TO value");
            mig.execute("ALTER TABLE ObjectTypeHolderEntity RENAME TO FastObjectType");
            mig.execute("ALTER TABLE ProviderURIHolderEntity RENAME COLUMN providerURIString TO value");
            mig.execute("ALTER TABLE ProviderURIHolderEntity RENAME TO FastProviderURI");
        } catch (SQLException ex1) {
            Logger.getLogger(TransactionsProcessor.class.getName()).log(Level.SEVERE, null, ex1);
        }
        Logger.getLogger(TransactionsProcessor.class.getName()).log(Level.INFO, "Database migrated successfully!");
    }

    public void createIndexesIfFirstTime() {
        if (indexCreated) {
            return;
        }
        try {
            Statement statement = serverConnection.createStatement();
            statement.execute("CREATE INDEX IF NOT EXISTS index_related2 ON COMObjectEntity (relatedLink)");
            statement.execute(
                "CREATE INDEX IF NOT EXISTS index_timestampArchiveDetails2 ON COMObjectEntity (timestampArchiveDetails)");
            indexCreated = true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startDatabaseDriver(String url2, String user, String password) {
        try {
            // Connect to the database
            LOGGER.log(Level.INFO, "Attempting to establish a connection to the database:\n >> " + url2);

            if (jdbcDriver.equals(DRIVER_CLASS_NAME)) {
                serverConnection = DriverManager.getConnection(url2);
            } else {
                serverConnection = DriverManager.getConnection(url2, user, password);
            }
        } catch (SQLException ex) {
            if (jdbcDriver.equals(DRIVER_CLASS_NAME)) {
                LOGGER.log(Level.WARNING, "Unexpected exception ! ", ex);
                LOGGER.log(Level.INFO, "There was an SQLException, maybe the " + DATABASE_LOCATION_NAME +
                    " folder/file does not exist. Attempting to create it...");
                try {
                    // Connect to the database but also create the database if it does not exist
                    serverConnection = DriverManager.getConnection(url2 + ";create=true");
                    LOGGER.log(Level.INFO, "Successfully created!");
                } catch (SQLException ex2) {
                    LOGGER.log(Level.SEVERE, "Other connection already exists! Error: " + ex2.getMessage(), ex2);
                    LOGGER.log(Level.SEVERE,
                        "Most likely there is another instance of the same application already running. " +
                            "Two instances of the same application are not allowed. The application will exit.");
                    System.exit(0);
                }
            } else {
                LOGGER.log(Level.SEVERE, "Unexpected exception ! " + ex.getMessage(), ex);
                System.exit(0);
            }
        }
    }
}
