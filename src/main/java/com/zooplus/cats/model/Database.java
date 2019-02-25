package com.zooplus.cats.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Database {

    static final String DB_URL = "jdbc:sqlite:src/main/resources/cats.db";

    static void createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("The database " + DB_URL + " has been created");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}