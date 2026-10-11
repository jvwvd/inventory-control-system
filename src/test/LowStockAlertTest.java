package test;

import java.util.List;

import exception.DatabaseException;
import exception.ValidationException;
import model.Alert;
import model.LowStockAlert;
import model.Product;
import service.ProductService;

public class LowStockAlertTest {

    public static void main(String[] args) {

        ProductService productService =
                new ProductService();

        Product selectedProduct = null;

        int originalQuantity = 0;
        int originalReorderLevel = 0;

        boolean originalLowStockStatus = false;
        boolean productChanged = false;

        System.out.println(
                "========================================"
        );

        System.out.println(
                "LOW STOCK ALERT TEST"
        );

        System.out.println(
                "========================================"
        );

        try {
            List<Product> products =
                    productService.getAllProducts();

            if (products.isEmpty()) {
                throw new IllegalStateException(
                        "No products are available for testing."
                );
            }

            selectedProduct =
                    products.get(0);

            originalQuantity =
                    selectedProduct.getQuantity();

            originalReorderLevel =
                    selectedProduct.getReorderLevel();

            originalLowStockStatus =
                    selectedProduct.isLowStock();

            int testReorderLevel;

            if (originalReorderLevel > 0) {
                testReorderLevel =
                        originalReorderLevel;
            } else {
                testReorderLevel = 5;
            }

            int testQuantity =
                    testReorderLevel;

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
                    "Original reorder level: "
                            + originalReorderLevel
            );

            selectedProduct.setQuantity(
                    testQuantity
            );

            selectedProduct.setReorderLevel(
                    testReorderLevel
            );

            productService.updateProduct(
                    selectedProduct
            );

            productChanged = true;

            Product lowStockProduct =
                    productService.getProductById(
                            selectedProduct.getId()
                    );

            check(
                    lowStockProduct != null,
                    "Test product retrieved"
            );

            check(
                    lowStockProduct.getQuantity()
                            == testQuantity,
                    "Test quantity saved as "
                            + testQuantity
            );

            check(
                    lowStockProduct.getReorderLevel()
                            == testReorderLevel,
                    "Test reorder level saved as "
                            + testReorderLevel
            );

            check(
                    lowStockProduct.isLowStock(),
                    "Product.isLowStock() returned true"
            );

            List<Product> lowStockProducts =
                    productService
                            .getLowStockProducts();

            check(
                    containsProduct(
                            lowStockProducts,
                            lowStockProduct.getId()
                    ),
                    "Low-stock query returned test product"
            );

            Alert alert =
                    new LowStockAlert();

            check(
                    alert.shouldTrigger(
                            lowStockProduct
                    ),
                    "LowStockAlert.shouldTrigger() "
                            + "returned true"
            );

            String expectedMessage =
                    "Low stock alert: "
                            + lowStockProduct
                                    .getProductName()
                            + " has only "
                            + lowStockProduct
                                    .getQuantity()
                            + " units remaining. "
                            + "Reorder level: "
                            + lowStockProduct
                                    .getReorderLevel()
                            + ".";

            String actualMessage =
                    alert.getMessage(
                            lowStockProduct
                    );

            check(
                    expectedMessage.equals(
                            actualMessage
                    ),
                    "Low-stock alert message is correct"
            );

            System.out.println();
            System.out.println(
                    "Alert message:"
            );

            System.out.println(
                    actualMessage
            );

            System.out.println();
            System.out.println(
                    "[PASS] Low-stock query and "
                            + "alert test completed."
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
                    "Low-stock verification failed.",
                    exception
            );

        } finally {
            if (selectedProduct != null
                    && productChanged) {

                try {
                    selectedProduct.setQuantity(
                            originalQuantity
                    );

                    selectedProduct.setReorderLevel(
                            originalReorderLevel
                    );

                    productService.updateProduct(
                            selectedProduct
                    );

                    Product restoredProduct =
                            productService
                                    .getProductById(
                                            selectedProduct
                                                    .getId()
                                    );

                    System.out.println();
                    System.out.println(
                            "RESTORE VERIFICATION"
                    );

                    check(
                            restoredProduct.getQuantity()
                                    == originalQuantity,
                            "Original quantity restored: "
                                    + originalQuantity
                    );

                    check(
                            restoredProduct
                                    .getReorderLevel()
                                    == originalReorderLevel,
                            "Original reorder level restored: "
                                    + originalReorderLevel
                    );

                    check(
                            restoredProduct.isLowStock()
                                    == originalLowStockStatus,
                            "Original low-stock status restored"
                    );

                    System.out.println(
                            "[PASS] Product restored to "
                                    + "original state."
                    );

                } catch (ValidationException
                        | DatabaseException
                        | IllegalStateException exception) {

                    printFailure(
                            "Automatic restore failed.",
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
                "LOW STOCK ALERT TEST FINISHED"
        );

        System.out.println(
                "========================================"
        );
    }

    private static boolean containsProduct(
            List<Product> products,
            int productId
    ) {
        for (Product product : products) {
            if (product.getId() == productId) {
                return true;
            }
        }

        return false;
    }

    private static void check(
            boolean condition,
            String testName
    ) {
        if (!condition) {
            throw new IllegalStateException(
                    testName + " failed."
            );
        }

        System.out.println(
                "[PASS] " + testName
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