package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import exception.AuthenticationException;
import exception.DatabaseException;
import exception.InsufficientStockException;
import exception.ValidationException;
import model.InventoryUser;
import model.Product;
import model.StockMovement;
import service.AuthenticationService;
import service.ProductService;
import service.StockService;
import util.DatabaseConnection;

public class StockTransactionTest {

    private static final int TEST_QUANTITY = 2;

    public static void main(String[] args) {

        AuthenticationService authenticationService =
                new AuthenticationService();

        ProductService productService =
                new ProductService();

        StockService stockService =
                new StockService();

        String testMarker =
                "STOCK_TRANSACTION_TEST_"
                        + System.currentTimeMillis();

        Product selectedProduct = null;
        int originalQuantity = 0;
        int originalMovementCount = 0;

        System.out.println(
                "========================================"
        );

        System.out.println(
                "STOCK TRANSACTION TEST"
        );

        System.out.println(
                "========================================"
        );

        try {
            InventoryUser loggedInUser =
                    authenticationService.login(
                            "admin",
                            "admin123"
                    );

            List<Product> products =
                    productService.getAllProducts();

            if (products.isEmpty()) {
                throw new IllegalStateException(
                        "No products are available for testing."
                );
            }

            selectedProduct = products.get(0);

            originalQuantity =
                    selectedProduct.getQuantity();

            originalMovementCount =
                    stockService
                            .getAllStockMovements()
                            .size();

            System.out.println(
                    "Product: "
                            + selectedProduct.getSku()
                            + " - "
                            + selectedProduct
                                    .getProductName()
            );

            System.out.println(
                    "Original quantity: "
                            + originalQuantity
            );

            System.out.println(
                    "Original movement count: "
                            + originalMovementCount
            );

            StockMovement stockIn =
                    stockService.recordStockIn(
                            selectedProduct.getId(),
                            loggedInUser.getId(),
                            TEST_QUANTITY,
                            testMarker + "_IN"
                    );

            Product productAfterStockIn =
                    productService.getProductById(
                            selectedProduct.getId()
                    );

            assertValue(
                    "Quantity after stock-in",
                    originalQuantity
                            + TEST_QUANTITY,
                    productAfterStockIn
                            .getQuantity()
            );

            assertValue(
                    "Stock-in movement type",
                    "IN",
                    stockIn.getMovementType()
            );

            assertValue(
                    "Stock-in quantity before",
                    originalQuantity,
                    stockIn.getQuantityBefore()
            );

            assertValue(
                    "Stock-in quantity after",
                    originalQuantity
                            + TEST_QUANTITY,
                    stockIn.getQuantityAfter()
            );

            StockMovement stockOut =
                    stockService.recordStockOut(
                            selectedProduct.getId(),
                            loggedInUser.getId(),
                            TEST_QUANTITY,
                            testMarker + "_OUT"
                    );

            Product productAfterStockOut =
                    productService.getProductById(
                            selectedProduct.getId()
                    );

            assertValue(
                    "Quantity after stock-out",
                    originalQuantity,
                    productAfterStockOut
                            .getQuantity()
            );

            assertValue(
                    "Stock-out movement type",
                    "OUT",
                    stockOut.getMovementType()
            );

            assertValue(
                    "Stock-out quantity before",
                    originalQuantity
                            + TEST_QUANTITY,
                    stockOut.getQuantityBefore()
            );

            assertValue(
                    "Stock-out quantity after",
                    originalQuantity,
                    stockOut.getQuantityAfter()
            );

            int movementCountAfterTest =
                    stockService
                            .getAllStockMovements()
                            .size();

            assertValue(
                    "Movement count after operations",
                    originalMovementCount + 2,
                    movementCountAfterTest
            );

            System.out.println();
            System.out.println(
                    "[PASS] Stock-in and stock-out "
                            + "transactions completed."
            );

        } catch (AuthenticationException exception) {
            printFailure(
                    "Authentication failed.",
                    exception
            );

        } catch (ValidationException exception) {
            printFailure(
                    "Validation failed.",
                    exception
            );

        } catch (InsufficientStockException exception) {
            printFailure(
                    "Stock-out failed.",
                    exception
            );

        } catch (DatabaseException exception) {
            printFailure(
                    "Database operation failed.",
                    exception
            );

        } catch (IllegalStateException exception) {
            printFailure(
                    "Test verification failed.",
                    exception
            );

        } finally {
            if (selectedProduct != null) {
                try {
                    cleanupTestData(
                            testMarker,
                            selectedProduct.getId(),
                            originalQuantity
                    );

                    Product restoredProduct =
                            productService.getProductById(
                                    selectedProduct.getId()
                            );

                    int restoredMovementCount =
                            stockService
                                    .getAllStockMovements()
                                    .size();

                    System.out.println();
                    System.out.println(
                            "CLEANUP VERIFICATION"
                    );

                    assertValue(
                            "Restored product quantity",
                            originalQuantity,
                            restoredProduct.getQuantity()
                    );

                    assertValue(
                            "Restored movement count",
                            originalMovementCount,
                            restoredMovementCount
                    );

                    System.out.println(
                            "[PASS] Test data removed "
                                    + "and quantity restored."
                    );

                } catch (DatabaseException
        | ValidationException
        | SQLException
        | IllegalStateException exception) {

    printFailure(
            "Automatic cleanup failed.",
            exception
    );
}
            }
        }

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "STOCK TRANSACTION TEST FINISHED"
        );

        System.out.println(
                "========================================"
        );
    }

    private static void cleanupTestData(
            String testMarker,
            int productId,
            int originalQuantity
    ) throws DatabaseException,
            SQLException {

        String deleteMovementsSql =
                "DELETE FROM stock_movements "
                        + "WHERE notes LIKE ?";

        String restoreQuantitySql =
                "UPDATE products "
                        + "SET quantity = ? "
                        + "WHERE product_id = ?";

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            try {
                connection.setAutoCommit(false);

                try (PreparedStatement statement =
                        connection.prepareStatement(
                                deleteMovementsSql
                        )) {

                    statement.setString(
                            1,
                            testMarker + "%"
                    );

                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                        connection.prepareStatement(
                                restoreQuantitySql
                        )) {

                    statement.setInt(
                            1,
                            originalQuantity
                    );

                    statement.setInt(
                            2,
                            productId
                    );

                    int affectedRows =
                            statement.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException(
                                "Original product quantity "
                                        + "could not be restored."
                        );
                    }
                }

                connection.commit();

            } catch (SQLException exception) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                    // Original cleanup error is reported.
                }

                throw exception;
            }
        }
    }

    private static void assertValue(
            String testName,
            int expected,
            int actual
    ) {
        if (expected != actual) {
            throw new IllegalStateException(
                    testName
                            + " failed. Expected: "
                            + expected
                            + ", actual: "
                            + actual
            );
        }

        System.out.println(
                "[PASS] "
                        + testName
                        + ": "
                        + actual
        );
    }

    private static void assertValue(
            String testName,
            String expected,
            String actual
    ) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(
                    testName
                            + " failed. Expected: "
                            + expected
                            + ", actual: "
                            + actual
            );
        }

        System.out.println(
                "[PASS] "
                        + testName
                        + ": "
                        + actual
        );
    }

    private static void printFailure(
            String message,
            Exception exception
    ) {
        System.out.println();
        System.out.println(
                "[FAILED] " + message
        );

        System.out.println(
                exception.getMessage()
        );

        if (exception.getCause() != null) {
            System.out.println(
                    "Cause: "
                            + exception.getCause()
                                    .getMessage()
            );
        }
    }
}
