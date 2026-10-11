package test;

import java.time.LocalDate;
import java.util.List;

import exception.DatabaseException;
import exception.ValidationException;
import model.Category;
import model.ElectronicProduct;
import model.PerishableProduct;
import model.Product;
import model.Supplier;
import service.CategoryService;
import service.ProductService;
import service.SupplierService;

public class ProductSubclassIntegrationTest {

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

        PerishableProduct perishableProduct =
                new PerishableProduct();

        ElectronicProduct electronicProduct =
                new ElectronicProduct();

        boolean testCompleted = false;

        System.out.println(
                "========================================"
        );

        System.out.println(
                "PRODUCT SUBCLASS INTEGRATION TEST"
        );

        System.out.println(
                "========================================"
        );

        try {
            List<Category> categories =
                    categoryService.getAllCategories();

            List<Supplier> suppliers =
                    supplierService.getAllSuppliers();

            if (categories.isEmpty()
                    || suppliers.isEmpty()) {

                throw new IllegalStateException(
                        "At least one category and supplier "
                                + "are required for this test."
                );
            }

            Category category =
                    categories.get(0);

            Supplier supplier =
                    suppliers.get(0);

            testPerishableProduct(
                    productService,
                    perishableProduct,
                    category,
                    supplier,
                    marker,
                    shortMarker
            );

            testElectronicProduct(
                    productService,
                    electronicProduct,
                    category,
                    supplier,
                    marker,
                    shortMarker
            );

            deleteTemporaryProducts(
                    productService,
                    perishableProduct,
                    electronicProduct
            );

            testCompleted = true;

            System.out.println();
            System.out.println(
                    "[PASS] Product subclass CRUD "
                            + "operations completed."
            );

            System.out.println(
                    "[PASS] Temporary products deleted."
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
                    "Subclass verification failed.",
                    exception
            );

        } finally {
            if (!testCompleted) {
                cleanupAfterFailure(
                        productService,
                        perishableProduct,
                        electronicProduct
                );
            }
        }

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "PRODUCT SUBCLASS TEST FINISHED"
        );

        System.out.println(
                "========================================"
        );
    }

    private static void testPerishableProduct(
            ProductService productService,
            PerishableProduct product,
            Category category,
            Supplier supplier,
            String marker,
            String shortMarker
    ) throws ValidationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "1. PERISHABLE PRODUCT TEST"
        );

        LocalDate originalExpiryDate =
                LocalDate.of(
                        2027,
                        12,
                        31
                );

        product.setSku(
                "PST-" + shortMarker
        );

        product.setProductName(
                "Test Perishable Product " + marker
        );

        product.setCategoryId(
                category.getId()
        );

        product.setSupplierId(
                supplier.getId()
        );

        product.setQuantity(15);
        product.setReorderLevel(5);
        product.setUnitPrice(4.50);

        product.setDescription(
                "Temporary perishable test product"
        );

        product.setExpiryDate(
                originalExpiryDate
        );

        productService.addProduct(product);

        check(
                product.getId() > 0,
                "Perishable product added with ID "
                        + product.getId()
        );

        Product savedProduct =
                productService.getProductById(
                        product.getId()
                );

        check(
                savedProduct
                        instanceof PerishableProduct,
                "Product mapped as PerishableProduct"
        );

        check(
                "PERISHABLE".equals(
                        savedProduct.getProductType()
                ),
                "Product type is PERISHABLE"
        );

        PerishableProduct savedPerishableProduct =
                (PerishableProduct) savedProduct;

        check(
                originalExpiryDate.equals(
                        savedPerishableProduct
                                .getExpiryDate()
                ),
                "Expiry date retrieved correctly"
        );

        LocalDate updatedExpiryDate =
                LocalDate.of(
                        2028,
                        6,
                        30
                );

        product.setProductName(
                "Updated Perishable Product "
                        + marker
        );

        product.setExpiryDate(
                updatedExpiryDate
        );

        product.setUnitPrice(5.25);

        productService.updateProduct(product);

        Product updatedProduct =
                productService.getProductById(
                        product.getId()
                );

        check(
                updatedProduct
                        instanceof PerishableProduct,
                "Updated product remains PerishableProduct"
        );

        PerishableProduct updatedPerishableProduct =
                (PerishableProduct) updatedProduct;

        check(
                updatedExpiryDate.equals(
                        updatedPerishableProduct
                                .getExpiryDate()
                ),
                "Expiry date updated successfully"
        );

        check(
                Math.abs(
                        updatedPerishableProduct
                                .getUnitPrice()
                                - 5.25
                ) < 0.001,
                "Perishable unit price updated to £5.25"
        );

        System.out.println(
                "Special information: "
                        + updatedPerishableProduct
                                .getSpecialInformation()
        );
    }

    private static void testElectronicProduct(
            ProductService productService,
            ElectronicProduct product,
            Category category,
            Supplier supplier,
            String marker,
            String shortMarker
    ) throws ValidationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "2. ELECTRONIC PRODUCT TEST"
        );

        product.setSku(
                "EST-" + shortMarker
        );

        product.setProductName(
                "Test Electronic Product " + marker
        );

        product.setCategoryId(
                category.getId()
        );

        product.setSupplierId(
                supplier.getId()
        );

        product.setQuantity(8);
        product.setReorderLevel(2);
        product.setUnitPrice(75.00);

        product.setDescription(
                "Temporary electronic test product"
        );

        product.setWarrantyMonths(12);

        productService.addProduct(product);

        check(
                product.getId() > 0,
                "Electronic product added with ID "
                        + product.getId()
        );

        Product savedProduct =
                productService.getProductById(
                        product.getId()
                );

        check(
                savedProduct
                        instanceof ElectronicProduct,
                "Product mapped as ElectronicProduct"
        );

        check(
                "ELECTRONIC".equals(
                        savedProduct.getProductType()
                ),
                "Product type is ELECTRONIC"
        );

        ElectronicProduct savedElectronicProduct =
                (ElectronicProduct) savedProduct;

        check(
                savedElectronicProduct
                        .getWarrantyMonths() == 12,
                "Warranty retrieved as 12 months"
        );

        product.setProductName(
                "Updated Electronic Product "
                        + marker
        );

        product.setWarrantyMonths(24);
        product.setUnitPrice(89.99);

        productService.updateProduct(product);

        Product updatedProduct =
                productService.getProductById(
                        product.getId()
                );

        check(
                updatedProduct
                        instanceof ElectronicProduct,
                "Updated product remains ElectronicProduct"
        );

        ElectronicProduct updatedElectronicProduct =
                (ElectronicProduct) updatedProduct;

        check(
                updatedElectronicProduct
                        .getWarrantyMonths() == 24,
                "Warranty updated to 24 months"
        );

        check(
                Math.abs(
                        updatedElectronicProduct
                                .getUnitPrice()
                                - 89.99
                ) < 0.001,
                "Electronic unit price updated to £89.99"
        );

        System.out.println(
                "Special information: "
                        + updatedElectronicProduct
                                .getSpecialInformation()
        );
    }

    private static void deleteTemporaryProducts(
            ProductService productService,
            PerishableProduct perishableProduct,
            ElectronicProduct electronicProduct
    ) throws ValidationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "3. DELETE TEST"
        );

        int perishableProductId =
                perishableProduct.getId();

        int electronicProductId =
                electronicProduct.getId();

        productService.deleteProduct(
                perishableProductId
        );

        check(
                productService.getProductById(
                        perishableProductId
                ) == null,
                "Perishable product deleted successfully"
        );

        perishableProduct.setId(0);

        productService.deleteProduct(
                electronicProductId
        );

        check(
                productService.getProductById(
                        electronicProductId
                ) == null,
                "Electronic product deleted successfully"
        );

        electronicProduct.setId(0);
    }

    private static void cleanupAfterFailure(
            ProductService productService,
            Product... products
    ) {
        System.out.println();
        System.out.println(
                "AUTOMATIC CLEANUP"
        );

        for (Product product : products) {
            if (product.getId() > 0) {
                try {
                    Product existingProduct =
                            productService
                                    .getProductById(
                                            product.getId()
                                    );

                    if (existingProduct != null) {
                        productService.deleteProduct(
                                product.getId()
                        );

                        System.out.println(
                                "[PASS] Temporary product removed."
                        );
                    }

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
