package test;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import exception.AuthenticationException;
import exception.DatabaseException;
import model.Category;
import model.InventoryReportRow;
import model.InventoryUser;
import model.Product;
import model.StockMovement;
import model.Supplier;
import service.AuthenticationService;
import service.CategoryService;
import service.ProductService;
import service.ReportService;
import service.StockService;
import service.SupplierService;

public class BackendSmokeTest {

    public static void main(String[] args) {

        System.out.println(
                "========================================"
        );

        System.out.println(
                "INVENTORY MANAGEMENT SYSTEM"
        );

        System.out.println(
                "BACKEND SMOKE TEST"
        );

        System.out.println(
                "========================================"
        );

        try {
            testAuthentication();
            testDatabaseReading();
            testStockMovements();
            testInventoryReport();

            System.out.println();
            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "ALL BACKEND SMOKE TESTS COMPLETED"
            );

            System.out.println(
                    "========================================"
            );

        } catch (AuthenticationException exception) {
            System.out.println();
            System.out.println(
                    "[FAILED] Authentication test"
            );

            System.out.println(
                    exception.getMessage()
            );

        } catch (DatabaseException exception) {
            System.out.println();
            System.out.println(
                    "[FAILED] Database test"
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

    private static void testAuthentication()
            throws AuthenticationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "1. AUTHENTICATION TEST"
        );

        AuthenticationService authenticationService =
                new AuthenticationService();

        InventoryUser loggedInUser =
                authenticationService.login(
                        "admin",
                        "admin123"
                );

        System.out.println(
                "[PASS] Valid login"
        );

        System.out.println(
                "Username: "
                        + loggedInUser.getUsername()
        );

        System.out.println(
                "Full name: "
                        + loggedInUser.getFullName()
        );

        System.out.println(
                "Active: "
                        + loggedInUser.isActive()
        );

        try {
            authenticationService.login(
                    "admin",
                    "incorrect-password"
            );

            System.out.println(
                    "[FAILED] Invalid password was accepted"
            );

        } catch (AuthenticationException exception) {
            System.out.println(
                    "[PASS] Invalid password rejected"
            );

            System.out.println(
                    "Message: "
                            + exception.getMessage()
            );
        }
    }

    private static void testDatabaseReading()
            throws DatabaseException {

        System.out.println();
        System.out.println(
                "2. DATABASE READING TEST"
        );

        CategoryService categoryService =
                new CategoryService();

        SupplierService supplierService =
                new SupplierService();

        ProductService productService =
                new ProductService();

        List<Category> categories =
                categoryService.getAllCategories();

        List<Supplier> suppliers =
                supplierService.getAllSuppliers();

        List<Product> products =
                productService.getAllProducts();

        List<Product> lowStockProducts =
                productService.getLowStockProducts();

        System.out.println(
                "[PASS] Categories retrieved: "
                        + categories.size()
        );

        System.out.println(
                "[PASS] Suppliers retrieved: "
                        + suppliers.size()
        );

        System.out.println(
                "[PASS] Products retrieved: "
                        + products.size()
        );

        System.out.println(
                "[PASS] Low-stock products: "
                        + lowStockProducts.size()
        );

        System.out.println();
        System.out.println(
                "Product inheritance mapping:"
        );

        for (Product product : products) {
            System.out.println(
                    "- "
                            + product.getSku()
                            + " | "
                            + product.getProductName()
                            + " | "
                            + product.getProductType()
                            + " | "
                            + product.getSpecialInformation()
            );
        }
    }

    private static void testStockMovements()
            throws DatabaseException {

        System.out.println();
        System.out.println(
                "3. STOCK MOVEMENT TEST"
        );

        StockService stockService =
                new StockService();

        List<StockMovement> stockMovements =
                stockService.getAllStockMovements();

        System.out.println(
                "[PASS] Stock movements retrieved: "
                        + stockMovements.size()
        );

        if (!stockMovements.isEmpty()) {
            StockMovement movement =
                    stockMovements.get(0);

            System.out.println(
                    "Latest movement:"
            );

            System.out.println(
                    "Type: "
                            + movement.getMovementType()
            );

            System.out.println(
                    "Product ID: "
                            + movement.getProductId()
            );

            System.out.println(
                    "Quantity moved: "
                            + movement.getQuantity()
            );

            System.out.println(
                    "Quantity before: "
                            + movement.getQuantityBefore()
            );

            System.out.println(
                    "Quantity after: "
                            + movement.getQuantityAfter()
            );
        }
    }

    private static void testInventoryReport()
            throws DatabaseException {

        System.out.println();
        System.out.println(
                "4. INVENTORY REPORT TEST"
        );

        ReportService reportService =
                new ReportService();

        List<InventoryReportRow> reportRows =
                reportService.generateInventoryReport();

        double totalInventoryValue =
                reportService
                        .calculateTotalInventoryValue(
                                reportRows
                        );

        NumberFormat currencyFormat =
                NumberFormat.getCurrencyInstance(
                        Locale.UK
                );

        System.out.println(
                "[PASS] Report rows generated: "
                        + reportRows.size()
        );

        System.out.println(
                "[PASS] Total inventory value: "
                        + currencyFormat.format(
                                totalInventoryValue
                        )
        );

        if (!reportRows.isEmpty()) {
            InventoryReportRow firstRow =
                    reportRows.get(0);

            System.out.println(
                    "First report row:"
            );

            System.out.println(
                    firstRow.getSku()
                            + " | "
                            + firstRow.getProductName()
                            + " | "
                            + firstRow.getCategoryName()
                            + " | "
                            + firstRow.getSupplierName()
                            + " | "
                            + firstRow.getStockStatus()
            );
        }
    }
}