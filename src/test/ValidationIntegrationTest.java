package test;

import java.util.List;

import exception.DatabaseException;
import exception.ValidationException;
import model.Category;
import model.ElectronicProduct;
import model.GeneralProduct;
import model.PerishableProduct;
import model.Product;
import model.Supplier;
import service.CategoryService;
import service.ProductService;
import service.SupplierService;

public class ValidationIntegrationTest {

    public static void main(String[] args) {

        CategoryService categoryService =
                new CategoryService();

        SupplierService supplierService =
                new SupplierService();

        ProductService productService =
                new ProductService();

        String marker =
                Long.toString(
                        System.currentTimeMillis()
                );

        String shortMarker =
                marker.substring(
                        marker.length() - 6
                );

        Category duplicateCategory =
                new Category();

        Supplier duplicateSupplier =
                new Supplier();

        Supplier invalidEmailSupplier =
                new Supplier();

        GeneralProduct duplicateSkuProduct =
                new GeneralProduct();

        GeneralProduct negativeQuantityProduct =
                new GeneralProduct();

        PerishableProduct missingExpiryProduct =
                new PerishableProduct();

        ElectronicProduct invalidWarrantyProduct =
                new ElectronicProduct();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "VALIDATION INTEGRATION TEST"
        );

        System.out.println(
                "========================================"
        );

        try {
            List<Category> categories =
                    categoryService
                            .getAllCategories();

            List<Supplier> suppliers =
                    supplierService
                            .getAllSuppliers();

            List<Product> products =
                    productService
                            .getAllProducts();

            if (categories.isEmpty()
                    || suppliers.isEmpty()
                    || products.isEmpty()) {

                throw new IllegalStateException(
                        "Sample category, supplier and "
                                + "product records are required."
                );
            }

            int originalCategoryCount =
                    categories.size();

            int originalSupplierCount =
                    suppliers.size();

            int originalProductCount =
                    products.size();

            Category existingCategory =
                    categories.get(0);

            Supplier existingSupplier =
                    suppliers.get(0);

            Product existingProduct =
                    products.get(0);

            testDuplicateCategory(
                    categoryService,
                    duplicateCategory,
                    existingCategory
            );

            testDuplicateSupplier(
                    supplierService,
                    duplicateSupplier,
                    existingSupplier
            );

            testInvalidEmail(
                    supplierService,
                    invalidEmailSupplier,
                    marker
            );

            testDuplicateSku(
                    productService,
                    duplicateSkuProduct,
                    existingProduct,
                    marker
            );

            testNegativeQuantity(
                    productService,
                    negativeQuantityProduct,
                    existingProduct,
                    shortMarker
            );

            testMissingExpiryDate(
                    productService,
                    missingExpiryProduct,
                    existingProduct,
                    shortMarker
            );

            testInvalidWarranty(
                    productService,
                    invalidWarrantyProduct,
                    existingProduct,
                    shortMarker
            );

            assertCount(
                    "Category count unchanged",
                    originalCategoryCount,
                    categoryService
                            .getAllCategories()
                            .size()
            );

            assertCount(
                    "Supplier count unchanged",
                    originalSupplierCount,
                    supplierService
                            .getAllSuppliers()
                            .size()
            );

            assertCount(
                    "Product count unchanged",
                    originalProductCount,
                    productService
                            .getAllProducts()
                            .size()
            );

            System.out.println();
            System.out.println(
                    "[PASS] No invalid records were saved."
            );

        } catch (DatabaseException exception) {
            printFailure(
                    "Database operation failed.",
                    exception
            );

        } catch (IllegalStateException exception) {
            printFailure(
                    "Validation verification failed.",
                    exception
            );

        } finally {
            cleanupUnexpectedProducts(
                    productService,
                    duplicateSkuProduct,
                    negativeQuantityProduct,
                    missingExpiryProduct,
                    invalidWarrantyProduct
            );

            cleanupUnexpectedSuppliers(
                    supplierService,
                    duplicateSupplier,
                    invalidEmailSupplier
            );

            cleanupUnexpectedCategories(
                    categoryService,
                    duplicateCategory
            );
        }

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "VALIDATION INTEGRATION TEST FINISHED"
        );

        System.out.println(
                "========================================"
        );
    }

    private static void testDuplicateCategory(
            CategoryService categoryService,
            Category duplicateCategory,
            Category existingCategory
    ) throws DatabaseException {

        System.out.println();
        System.out.println(
                "1. DUPLICATE CATEGORY TEST"
        );

        duplicateCategory.setCategoryName(
                existingCategory
                        .getCategoryName()
                        .toLowerCase()
        );

        duplicateCategory.setDescription(
                "Duplicate category test"
        );

        try {
            categoryService.addCategory(
                    duplicateCategory
            );

            throw new IllegalStateException(
                    "Duplicate category was accepted."
            );

        } catch (ValidationException exception) {
            assertMessage(
                    "Duplicate category rejected",
                    "Category name already exists.",
                    exception
            );
        }
    }

    private static void testDuplicateSupplier(
            SupplierService supplierService,
            Supplier duplicateSupplier,
            Supplier existingSupplier
    ) throws DatabaseException {

        System.out.println();
        System.out.println(
                "2. DUPLICATE SUPPLIER TEST"
        );

        duplicateSupplier.setSupplierName(
                existingSupplier
                        .getSupplierName()
                        .toUpperCase()
        );

        try {
            supplierService.addSupplier(
                    duplicateSupplier
            );

            throw new IllegalStateException(
                    "Duplicate supplier was accepted."
            );

        } catch (ValidationException exception) {
            assertMessage(
                    "Duplicate supplier rejected",
                    "Supplier name already exists.",
                    exception
            );
        }
    }

    private static void testInvalidEmail(
            SupplierService supplierService,
            Supplier invalidEmailSupplier,
            String marker
    ) throws DatabaseException {

        System.out.println();
        System.out.println(
                "3. INVALID EMAIL TEST"
        );

        invalidEmailSupplier.setSupplierName(
                "Invalid Email Supplier " + marker
        );

        invalidEmailSupplier.setEmail(
                "invalid-email-address"
        );

        try {
            supplierService.addSupplier(
                    invalidEmailSupplier
            );

            throw new IllegalStateException(
                    "Invalid supplier email was accepted."
            );

        } catch (ValidationException exception) {
            assertMessage(
                    "Invalid email rejected",
                    "Email format is invalid.",
                    exception
            );
        }
    }

    private static void testDuplicateSku(
            ProductService productService,
            GeneralProduct duplicateSkuProduct,
            Product existingProduct,
            String marker
    ) throws DatabaseException {

        System.out.println();
        System.out.println(
                "4. DUPLICATE SKU TEST"
        );

        setValidProductValues(
                duplicateSkuProduct,
                existingProduct,
                existingProduct
                        .getSku()
                        .toLowerCase(),
                "Duplicate SKU Product " + marker
        );

        try {
            productService.addProduct(
                    duplicateSkuProduct
            );

            throw new IllegalStateException(
                    "Duplicate product SKU was accepted."
            );

        } catch (ValidationException exception) {
            assertMessage(
                    "Duplicate SKU rejected",
                    "Product SKU already exists.",
                    exception
            );
        }
    }

    private static void testNegativeQuantity(
            ProductService productService,
            GeneralProduct negativeQuantityProduct,
            Product existingProduct,
            String shortMarker
    ) throws DatabaseException {

        System.out.println();
        System.out.println(
                "5. NEGATIVE QUANTITY TEST"
        );

        setValidProductValues(
                negativeQuantityProduct,
                existingProduct,
                "NEG-" + shortMarker,
                "Negative Quantity Test Product"
        );

        negativeQuantityProduct.setQuantity(-1);

        try {
            productService.addProduct(
                    negativeQuantityProduct
            );

            throw new IllegalStateException(
                    "Negative product quantity was accepted."
            );

        } catch (ValidationException exception) {
            assertMessage(
                    "Negative quantity rejected",
                    "Product quantity cannot be negative.",
                    exception
            );
        }
    }

    private static void testMissingExpiryDate(
            ProductService productService,
            PerishableProduct missingExpiryProduct,
            Product existingProduct,
            String shortMarker
    ) throws DatabaseException {

        System.out.println();
        System.out.println(
                "6. MISSING EXPIRY DATE TEST"
        );

        setValidProductValues(
                missingExpiryProduct,
                existingProduct,
                "EXP-" + shortMarker,
                "Missing Expiry Test Product"
        );

        missingExpiryProduct.setExpiryDate(null);

        try {
            productService.addProduct(
                    missingExpiryProduct
            );

            throw new IllegalStateException(
                    "Missing expiry date was accepted."
            );

        } catch (ValidationException exception) {
            assertMessage(
                    "Missing expiry date rejected",
                    "Expiry date is required.",
                    exception
            );
        }
    }

    private static void testInvalidWarranty(
            ProductService productService,
            ElectronicProduct invalidWarrantyProduct,
            Product existingProduct,
            String shortMarker
    ) throws DatabaseException {

        System.out.println();
        System.out.println(
                "7. INVALID WARRANTY TEST"
        );

        setValidProductValues(
                invalidWarrantyProduct,
                existingProduct,
                "WAR-" + shortMarker,
                "Invalid Warranty Test Product"
        );

        invalidWarrantyProduct.setWarrantyMonths(0);

        try {
            productService.addProduct(
                    invalidWarrantyProduct
            );

            throw new IllegalStateException(
                    "Invalid warranty months were accepted."
            );

        } catch (ValidationException exception) {
            assertMessage(
                    "Invalid warranty rejected",
                    "Warranty months must be greater than zero.",
                    exception
            );
        }
    }

    private static void setValidProductValues(
            Product product,
            Product existingProduct,
            String sku,
            String productName
    ) {
        product.setSku(sku);
        product.setProductName(productName);

        product.setCategoryId(
                existingProduct.getCategoryId()
        );

        product.setSupplierId(
                existingProduct.getSupplierId()
        );

        product.setQuantity(5);
        product.setReorderLevel(2);
        product.setUnitPrice(10.00);

        product.setDescription(
                "Temporary validation test product"
        );
    }

    private static void assertMessage(
            String testName,
            String expectedMessage,
            ValidationException exception
    ) {
        if (!expectedMessage.equals(
                exception.getMessage()
        )) {
            throw new IllegalStateException(
                    testName
                            + " returned an unexpected message. "
                            + "Expected: "
                            + expectedMessage
                            + " Actual: "
                            + exception.getMessage()
            );
        }

        System.out.println(
                "[PASS] " + testName
        );

        System.out.println(
                "Message: "
                        + exception.getMessage()
        );
    }

    private static void assertCount(
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

    private static void cleanupUnexpectedProducts(
            ProductService productService,
            Product... products
    ) {
        for (Product product : products) {
            if (product.getId() > 0) {
                try {
                    productService.deleteProduct(
                            product.getId()
                    );

                    System.out.println(
                            "[CLEANUP] Unexpected product removed."
                    );

                } catch (ValidationException
                        | DatabaseException exception) {

                    System.out.println(
                            "[FAILED] Product cleanup: "
                                    + exception.getMessage()
                    );
                }
            }
        }
    }

    private static void cleanupUnexpectedSuppliers(
            SupplierService supplierService,
            Supplier... suppliers
    ) {
        for (Supplier supplier : suppliers) {
            if (supplier.getId() > 0) {
                try {
                    supplierService.deleteSupplier(
                            supplier.getId()
                    );

                    System.out.println(
                            "[CLEANUP] Unexpected supplier removed."
                    );

                } catch (ValidationException
                        | DatabaseException exception) {

                    System.out.println(
                            "[FAILED] Supplier cleanup: "
                                    + exception.getMessage()
                    );
                }
            }
        }
    }

    private static void cleanupUnexpectedCategories(
            CategoryService categoryService,
            Category... categories
    ) {
        for (Category category : categories) {
            if (category.getId() > 0) {
                try {
                    categoryService.deleteCategory(
                            category.getId()
                    );

                    System.out.println(
                            "[CLEANUP] Unexpected category removed."
                    );

                } catch (ValidationException
                        | DatabaseException exception) {

                    System.out.println(
                            "[FAILED] Category cleanup: "
                                    + exception.getMessage()
                    );
                }
            }
        }
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