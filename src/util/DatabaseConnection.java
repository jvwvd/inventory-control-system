package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import exception.DatabaseException;

public final class DatabaseConnection {

    private static final String JDBC_DRIVER =
            "org.sqlite.JDBC";

    private static final String DATABASE_URL =
            "jdbc:sqlite:database/inventory.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection()
            throws DatabaseException {

        Connection connection = null;

        try {
            Class.forName(JDBC_DRIVER);

            connection =
                    DriverManager.getConnection(DATABASE_URL);

            try (Statement statement =
                    connection.createStatement()) {

                statement.execute(
                        "PRAGMA foreign_keys = ON"
                );
            }

            return connection;

        } catch (ClassNotFoundException exception) {
            throw new DatabaseException(
                    "SQLite JDBC driver was not found.",
                    exception
            );

        } catch (SQLException exception) {
            closeQuietly(connection);

            throw new DatabaseException(
                    "Database connection could not be established.",
                    exception
            );
        }
    }

    private static void closeQuietly(
            Connection connection
    ) {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException ignored) {
            // Original database error is more important.
        }
    }
}