/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * The class that bridges the COM Archive logic to the actual database backend.
 *
 * @author Cesar Coelho
 */
public class DatabaseBackend {

    private static final String DROP_TABLE_PROPERTY =
            "esa.mo.com.impl.provider.ArchiveManager.droptable";
    private static final String PERSISTENCE_UNIT_NAME = "ArchivePersistenceUnit";
    private static final boolean OPTIMIZED_STARTUP = false;

    //    private static final String DRIVER_CLASS_NAME = "org.apache.derby.jdbc.EmbeddedDriver"; //
    // Derby Embedded Driver
    //    private static final String DATABASE_NAME = "derby"; // Derby
    //    private static final String DATABASE_LOCATION_NAME = "databaseV0.4";
    private static final String DRIVER_CLASS_NAME = "org.sqlite.JDBC"; // SQLite JDBC Driver

    private static final String DATABASE_NAME = "sqlite"; // SQLite

    private static final String DATABASE_LOCATION_NAME = "comArchive.db";

    private final Semaphore emAvailability =
            new Semaphore(0, true); // true for fairness, because we want FIFO

    private final String jdbcDriver;

    private final String url;

    private final String user;

    private final String password;

    private EntityManagerFactory emf;

    private EntityManager em;

    private Connection serverConnection;

    public DatabaseBackend() {
        String url = System.getProperty("esa.nmf.archive.persistence.jdbc.url");

        if (null != url && !"".equals(url)) {
            this.url = url;
        } else {
            this.url = "jdbc:" + DATABASE_NAME + ":" + DATABASE_LOCATION_NAME;
        }

        String driver = System.getProperty("esa.nmf.archive.persistence.jdbc.driver");

        if (null != driver && !"".equals(driver)) {
            this.jdbcDriver = driver;
        } else {
            this.jdbcDriver = DRIVER_CLASS_NAME;
        }

        String user = System.getProperty("esa.nmf.archive.persistence.jdbc.user");

        if (null != user && !"".equals(user)) {
            this.user = user;
        } else {
            this.user = null;
        }

        String password = System.getProperty("esa.nmf.archive.persistence.jdbc.password");

        if (null != password && !"".equals(password)) {
            this.password = password;
        } else {
            this.password = null;
        }
    }

    public Semaphore getEmAvailability() {
        return emAvailability;
    }

    public Connection getConnection() {
        return serverConnection;
    }

    /**
     * Starts the database backend by creates the Entity Manager Factory.
     *
     * @param dbProcessor The transactions processor.
     */
    public void startBackendDatabase(final TransactionsProcessor dbProcessor) {
        if (OPTIMIZED_STARTUP) {
            dbProcessor.submitExternalTask(
                    () -> {
                        createEMFactory();
                        emAvailability.release();
                        Logger.getLogger(DatabaseBackend.class.getName())
                                .log(Level.INFO, "The EntityManagerFactory was created.");
                    });
        } else {
            createEMFactory();
            emAvailability.release();
            Logger.getLogger(DatabaseBackend.class.getName())
                    .log(Level.INFO, "The EntityManagerFactory was created.");
        }

        startDatabaseDriver(this.url, this.user, this.password);
    }

    private void startDatabaseDriver(String url2, String user, String password) {
        //        System.setProperty("derby.drda.startNetworkServer", "true");
        // Loads a new instance of the database driver
    /*try {
      Logger.getLogger(DatabaseBackend.class.getName())
          .log(Level.INFO, "Creating a new instance of the database driver: " + jdbcDriver);
      Class.forName(jdbcDriver).newInstance();
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException ex) {
      Logger.getLogger(DatabaseBackend.class.getName())
          .log(Level.SEVERE, "Unexpected exception ! ", ex);
    }*/

        // Create unique URL that identifies the driver to use for the connection
        //        String url2 = this.url + ";decryptDatabase=true"; // new
        try {
            // Connect to the database
            Logger.getLogger(ArchiveManager.class.getName())
                    .log(Level.INFO, "Attempting to establish a connection to the database: " + url2);

            if (jdbcDriver.equals(DRIVER_CLASS_NAME)) {
                serverConnection = DriverManager.getConnection(url2);
            } else {
                serverConnection = DriverManager.getConnection(url2, user, password);
            }
        } catch (SQLException ex) {

            if (jdbcDriver.equals(DRIVER_CLASS_NAME)) {
                Logger.getLogger(ArchiveManager.class.getName())
                        .log(Level.WARNING, "Unexpected exception ! ", ex);
                Logger.getLogger(ArchiveManager.class.getName())
                        .log(
                                Level.INFO,
                                "There was an SQLException, maybe the "
                                + DATABASE_LOCATION_NAME
                                + " folder/file does not exist. Attempting to create it...");
                try {
                    // Connect to the database but also create the database if it does not exist
                    serverConnection = DriverManager.getConnection(url2 + ";create=true");
                    Logger.getLogger(ArchiveManager.class.getName()).log(Level.INFO, "Successfully created!");
                } catch (SQLException ex2) {
                    Logger.getLogger(ArchiveManager.class.getName())
                            .log(Level.INFO, "Other connection already exists! Error: " + ex2.getMessage(), ex2);
                    Logger.getLogger(ArchiveManager.class.getName())
                            .log(Level.INFO,
                                 "Most likely there is another instance of the same application already running. "
                                 + "Two instances of the same application are not allowed. The application will exit.");
                    System.exit(0);
                }
            } else {
                Logger.getLogger(ArchiveManager.class.getName())
                        .log(Level.SEVERE, "Unexpected exception ! " + ex.getMessage(), ex);
                System.exit(0);
            }
        }
    }

    private void createEMFactory() {
        // Is the status of the dropTable flag on?
        boolean dropTable = "true".equals(System.getProperty(DROP_TABLE_PROPERTY));
        Map<String, String> persistenceMap = new HashMap<String, String>();

        // Add the url property of the connection to the database
        persistenceMap.put("javax.persistence.jdbc.url", this.url);

        if (!this.jdbcDriver.equals(DRIVER_CLASS_NAME)) {
            persistenceMap.put("javax.persistence.jdbc.driver", this.jdbcDriver);
            persistenceMap.put("javax.persistence.jdbc.user", null == this.user ? "" : this.user);
            persistenceMap.put("javax.persistence.jdbc.password", null == this.password ? "" : this.password);
        }

        if (dropTable) {
            persistenceMap.put("javax.persistence.schema-generation.database.action", "drop-and-create");
            Logger.getLogger(ArchiveManager.class.getName())
                    .log(
                            Level.INFO,
                            "The droptable flag in the properties file is enabled! The table will be dropped upon start-up.");
        }

        Logger.getLogger(ArchiveManager.class.getName())
                .log(Level.INFO, "Creating Entity Manager Factory...");
        this.emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, persistenceMap);
    }

    public void createEntityManager() {
        try {
            this.emAvailability.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.em = this.emf.createEntityManager();
    }

    public void closeEntityManager() {
        this.em.close();
        this.emAvailability.release();
    }

    public EntityManager getEM() {
        return this.em;
    }

    public void restartEMF() {
        this.emf.close();
        this.createEMFactory();
        this.emAvailability.release();
    }

    public void safeCommit() {
        try { // This is where the db takes longer!!
            this.em.getTransaction().commit(); // 1.220 ms
        } catch (Exception ex) {
            if (ex instanceof java.lang.IllegalStateException) {
                Logger.getLogger(ArchiveManager.class.getName())
                        .log(Level.WARNING, "The database file might be locked by another application...");
            }

            Logger.getLogger(ArchiveManager.class.getName())
                    .log(
                            Level.WARNING,
                            "The object could not be commited! Waiting 2500 ms and trying again...");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ex1) {
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex1);
            }

            try {
                this.em.getTransaction().commit(); // 1.220 ms
            } catch (Exception ex2) {
                Logger.getLogger(ArchiveManager.class.getName())
                        .log(Level.SEVERE, "The objects could not be commited on the second try!", ex2);
            }
        }
    }
}
