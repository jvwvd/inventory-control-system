import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {

    private static final String DATABASE_URL =
            "jdbc:sqlite:database/inventory.db";

    private static final String SAMPLE_DATA_FILE_PATH =
            "database/sample_data.sql";

    public static void main(String[] args) {

        try {
            Class.forName("org.sqlite.JDBC");

            String sampleDataSql =
                    Files.readString(
                            Path.of(SAMPLE_DATA_FILE_PATH)
                    );

            try (
                Connection connection =
                        DriverManager.getConnection(DATABASE_URL);

                Statement statement =
                        connection.createStatement()
            ) {
                statement.execute("PRAGMA foreign_keys = ON");

                executeSqlScript(statement, sampleDataSql);

                System.out.println(
                        "Sample data loaded successfully."
                );

                verifyRecordCounts(connection);
                displayUsers(connection);
                displayProducts(connection);
                displayStockMovements(connection);
                verifyForeignKeys(connection);
            }

        } catch (ClassNotFoundException exception) {
            System.out.println(
                    "SQLite JDBC driver was not found."
            );
            exception.printStackTrace();

        } catch (IOException exception) {
            System.out.println(
                    "Could not read sample_data.sql."
            );
            exception.printStackTrace();

        } catch (SQLException exception) {
            System.out.println(
                    "Sample data loading failed."
            );
            exception.printStackTrace();
        }
    }

    private static void executeSqlScript(
            Statement statement,
            String sqlScript
    ) throws SQLException {

        String[] sqlStatements = sqlScript.split(";");

        for (String sqlStatement : sqlStatements) {

            String cleanedStatement =
                    sqlStatement.trim();

            if (!cleanedStatement.isEmpty()) {
                statement.execute(cleanedStatement);
            }
        }
    }

    private static void verifyRecordCounts(
            Connection connection
    ) throws SQLException {

        System.out.println();
        System.out.println("Record counts:");

        printRecordCount(connection, "users");
        printRecordCount(connection, "categories");
        printRecordCount(connection, "suppliers");
        printRecordCount(connection, "products");
        printRecordCount(connection, "stock_movements");
    }

    private static void printRecordCount(
            Connection connection,
            String tableName
    ) throws SQLException {

        String query =
                "SELECT COUNT(*) AS total FROM " + tableName;

        try (
            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(query)
        ) {
            if (resultSet.next()) {
                System.out.println(
                        "- " + tableName
                                + ": "
                                + resultSet.getInt("total")
                );
            }
        }
    }

    private static void displayUsers(
            Connection connection
    ) throws SQLException {

        String query =
                "SELECT username, full_name, is_active " +
                "FROM users " +
                "ORDER BY user_id";

        System.out.println();
        System.out.println("Users:");

        try (
            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(query)
        ) {
            while (resultSet.next()) {

                boolean active =
                        resultSet.getInt("is_active") == 1;

                System.out.println(
                        "- "
                                + resultSet.getString("username")
                                + " | "
                                + resultSet.getString("full_name")
                                + " | Active: "
                                + active
                );
            }
        }
    }

    private static void displayProducts(
            Connection connection
    ) throws SQLException {

        String query =
                "SELECT sku, product_name, product_type, " +
                "quantity, reorder_level, unit_price " +
                "FROM products " +
                "ORDER BY product_id";

        System.out.println();
        System.out.println("Products:");

        try (
            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(query)
        ) {
            while (resultSet.next()) {
                System.out.printf(
                        "- %s | %s | %s | "
                                + "Quantity: %d | "
                                + "Reorder level: %d | "
                                + "Price: £%.2f%n",

                        resultSet.getString("sku"),
                        resultSet.getString("product_name"),
                        resultSet.getString("product_type"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("reorder_level"),
                        resultSet.getDouble("unit_price")
                );
            }
        }
    }

    private static void displayStockMovements(
            Connection connection
    ) throws SQLException {

        String query =
                "SELECT " +
                "p.sku, " +
                "u.username, " +
                "m.movement_type, " +
                "m.quantity, " +
                "m.quantity_before, " +
                "m.quantity_after " +
                "FROM stock_movements AS m " +
                "INNER JOIN products AS p " +
                "ON m.product_id = p.product_id " +
                "INNER JOIN users AS u " +
                "ON m.user_id = u.user_id " +
                "ORDER BY m.movement_id";

        System.out.println();
        System.out.println("Stock movements:");

        try (
            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(query)
        ) {
            while (resultSet.next()) {
                System.out.println(
                        "- SKU: "
                                + resultSet.getString("sku")
                                + " | User: "
                                + resultSet.getString("username")
                                + " | Type: "
                                + resultSet.getString(
                                        "movement_type"
                                )
                                + " | Quantity: "
                                + resultSet.getInt("quantity")
                                + " | Before: "
                                + resultSet.getInt(
                                        "quantity_before"
                                )
                                + " | After: "
                                + resultSet.getInt(
                                        "quantity_after"
                                )
                );
            }
        }
    }

    private static void verifyForeignKeys(
            Connection connection
    ) throws SQLException {

        String query = "PRAGMA foreign_key_check";

        boolean violationFound = false;

        try (
            Statement statement =
                    connection.createStatement();

            ResultSet resultSet =
                    statement.executeQuery(query)
        ) {
            while (resultSet.next()) {
                violationFound = true;

                System.out.println(
                        "Foreign-key violation found in table: "
                                + resultSet.getString("table")
                );
            }
        }

        System.out.println();

        if (violationFound) {
            System.out.println(
                    "Foreign-key verification failed."
            );
        } else {
            System.out.println(
                    "No foreign-key violations found."
            );
        }
    }
}