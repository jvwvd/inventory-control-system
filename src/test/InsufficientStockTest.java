package test;

import java.util.List;

import exception.AuthenticationException;
import exception.DatabaseException;
import exception.InsufficientStockException;
import exception.ValidationException;
import model.InventoryUser;
import model.Product;
import service.AuthenticationService;
import service.ProductService;
import service.StockService;

public class InsufficientStockTest {

    public static void main(String[] args) {

        AuthenticationService authenticationService =
                new AuthenticationService();

        ProductService productService =
                new ProductService();

        StockService stockService =
                new StockService();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "INSUFFICIENT STOCK TEST"
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

            Product selectedProduct =
                    products.get(0);

            int productId =
                    selectedProduct.getId();

            int originalQuantity =
                    selectedProduct.getQuantity();

            int requestedQuantity =
                    originalQuantity + 1;

            int originalMovementCount =
                    stockService
                            .getAllStockMovements()
                            .size();

            int originalProductMovementCount =
                    stockService
                            .getStockMovementsByProductId(
                                    productId
                            )
                            .size();

            System.out.println(
                    "Product: "
                            + selectedProduct.getSku()
                            + " - "
                            + selectedProduct
                                    .getProductName()
            );

            System.out.println(
                    "Available quantity: "
                            + originalQuantity
            );

            System.out.println(
                    "Requested stock-out quantity: "
                            + requestedQuantity
            );

            boolean insufficientStockRejected =
                    false;

            try {
                stockService.recordStockOut(
                        productId,
                        loggedInUser.getId(),
                        requestedQuantity,
                        "INSUFFICIENT_STOCK_TEST"
                );

            } catch (InsufficientStockException exception) {
                insufficientStockRejected = true;

                System.out.println();
                System.out.println(
                        "[PASS] Insufficient stock rejected."
                );

                System.out.println(
                        "Message: "
                                + exception.getMessage()
                );
            }

            if (!insufficientStockRejected) {
                throw new IllegalStateException(
                        "Stock-out was incorrectly accepted."
                );
            }

            Product productAfterAttempt =
                    productService.getProductById(
                            productId
                    );

            int movementCountAfterAttempt =
                    stockService
                            .getAllStockMovements()
                            .size();

            int productMovementCountAfterAttempt =
                    stockService
                            .getStockMovementsByProductId(
                                    productId
                            )
                            .size();

            assertValue(
                    "Product quantity unchanged",
                    originalQuantity,
                    productAfterAttempt.getQuantity()
            );

            assertValue(
                    "Total movement count unchanged",
                    originalMovementCount,
                    movementCountAfterAttempt
            );

            assertValue(
                    "Product movement count unchanged",
                    originalProductMovementCount,
                    productMovementCountAfterAttempt
            );

            System.out.println();
            System.out.println(
                    "[PASS] Transaction rollback verified."
            );

            System.out.println(
                    "[PASS] No invalid stock movement was saved."
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
        }

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "INSUFFICIENT STOCK TEST FINISHED"
        );

        System.out.println(
                "========================================"
        );
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
