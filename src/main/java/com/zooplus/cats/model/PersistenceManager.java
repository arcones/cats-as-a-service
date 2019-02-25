package com.zooplus.cats.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.zooplus.cats.Main.shutdownServer;
import static com.zooplus.cats.model.Database.DB_URL;
import static com.zooplus.cats.model.Database.createDatabase;

class PersistenceManager {

    private static final String ERROR_TRACE_PREFIX = "The persistence is failing: ";

    private Statement statement;

    PersistenceManager() {
        try {
            createDatabase();
            Connection connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            createInitialDataSet();
        } catch (SQLException sqlException) {
            System.err.println("The persistence is failing: " + sqlException.getMessage());
            shutdownServer();
        }
    }

    private void createInitialDataSet() {
        try {
            schemaCreation();
            System.out.println("Table has been re-created");
            catsInitialLoad();
            foodInitialLoad();
            System.out.println("Initial data set has been created");
        } catch (SQLException sqlException) {
            System.err.println(ERROR_TRACE_PREFIX + sqlException.getMessage());
            shutdownServer();
        }

    }

    Statement getStatement() {
        return this.statement;
    }

    private void schemaCreation() throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXISTS FOOD");
        statement.executeUpdate("DROP TABLE IF EXISTS CAT");
        statement.executeUpdate("CREATE TABLE CAT (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, NAME TEXT NOT NULL UNIQUE, AGE INTEGER NOT NULL, PARENT_ID INTEGER NOT NULL DEFAULT 0);");
        statement.executeUpdate("CREATE TABLE FOOD (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,CAT_ID INTEGER,BRAND TEXT, CONSTRAINT CAT_ID FOREIGN KEY (CAT_ID) REFERENCES CAT (ID)); CREATE UNIQUE INDEX FOOD_ID_uindex ON FOOD (ID);");
    }

    private void foodInitialLoad() throws SQLException {
        statement.executeUpdate("INSERT INTO FOOD (CAT_ID, BRAND) VALUES (1, 'Cosma')");
        statement.executeUpdate("INSERT INTO FOOD (CAT_ID, BRAND) VALUES (2, 'Concept for life')");
        statement.executeUpdate("INSERT INTO FOOD (CAT_ID, BRAND) VALUES (3, 'Tigerino')");
    }

    private void catsInitialLoad() throws SQLException {
        statement.executeUpdate("INSERT INTO CAT (NAME, AGE, PARENT_ID) VALUES('Perry', 9, 0)");
        statement.executeUpdate("INSERT INTO CAT (NAME, AGE, PARENT_ID) VALUES('Felix', 3, 1)");
        statement.executeUpdate("INSERT INTO CAT (NAME, AGE, PARENT_ID) VALUES('Jacobo', 9, 0)");
    }

    //TODO testing con el dataset inicial!!!
}
