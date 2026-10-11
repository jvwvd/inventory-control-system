package test;

import java.util.List;

import exception.DatabaseException;
import exception.ValidationException;
import model.Category;
import model.GeneralProduct;
import model.Product;
import model.Supplier;
import service.CategoryService;
import service.ProductService;
import service.SupplierService;

public class CrudIntegrationTest {

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

        Category category =
                new Category();

        Supplier supplier =
                new Supplier();

        GeneralProduct product =
                new GeneralProduct();

        boolean testCompleted = false;

        System.out.println(
                "========================================"
        );

        System.out.println(
                "CRUD INTEGRATION TEST"
        );

        System.out.println(
                "========================================"
        );

        try {
            testCategoryCrud(
                    categoryService,
                    category,
                    marker,
                    shortMarker
            );

            testSupplierCrud(
                    supplierService,
                    supplier,
                    marker,
                    shortMarker
            );

            testProductCrud(
                    productService,
                    product,
                    category,
                    supplier,
                    marker,
                    shortMarker
            );

            deleteTemporaryRecords(
                    productService,
                    supplierService,
                    categoryService,
                    product,
                    supplier,
                    category
            );

            testCompleted = true;

            System.out.println();
            System.out.println(
                    "[PASS] All CRUD operations completed."
            );

            System.out.println(
                    "[PASS] Temporary records deleted."
            );

        } catch (ValidationException exception) {
            printFailure(
                    "Validation test failed.",
                    exception
            );

        } catch (DatabaseException exception) {
            printFailure(
                    "Database operation failed.",
                    exception
            );

        } catch (IllegalStateException exception) {
            printFailure(
                    "CRUD verification failed.",
                    exception
            );

        } finally {
            if (!testCompleted) {
                cleanupAfterFailure(
                        productService,
                        supplierService,
                        categoryService,
                        product,
                        supplier,
                        category
                );
            }
        }

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "CRUD INTEGRATION TEST FINISHED"
        );

        System.out.println(
                "========================================"
        );
    }

    private static void testCategoryCrud(
            CategoryService categoryService,
            Category category,
            String marker,
            String shortMarker
    ) throws ValidationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "1. CATEGORY CRUD TEST"
        );

        category.setCategoryName(
                "Test Category " + marker
        );

        category.setDescription(
                "Temporary category for CRUD testing"
        );

        categoryService.addCategory(category);

        check(
                category.getId() > 0,
                "Category added with ID "
                        + category.getId()
        );

        Category savedCategory =
                categoryService.getCategoryById(
                        category.getId()
                );

        check(
                savedCategory != null,
                "Category retrieved by ID"
        );

        List<Category> searchResults =
                categoryService.searchCategories(
                        shortMarker
                );

        check(
                containsCategory(
                        searchResults,
                        category.getId()
                ),
                "Category search returned test record"
        );

        String updatedCategoryName =
                "Updated Test Category " + marker;

        category.setCategoryName(
                updatedCategoryName
        );

        category.setDescription(
                "Updated category description"
        );

        categoryService.updateCategory(category);

        Category updatedCategory =
                categoryService.getCategoryById(
                        category.getId()
                );

        check(
                updatedCategory != null
                        && updatedCategoryName.equals(
                        updatedCategory
                                .getCategoryName()
                ),
                "Category updated successfully"
        );
    }

    private static void testSupplierCrud(
            SupplierService supplierService,
            Supplier supplier,
            String marker,
            String shortMarker
    ) throws ValidationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "2. SUPPLIER CRUD TEST"
        );

        supplier.setSupplierName(
                "Test Supplier " + marker
        );

        supplier.setContactPerson(
                "Test Contact"
        );

        supplier.setPhone(
                "+44 20 7946 0999"
        );

        supplier.setEmail(
                "test."
                        + shortMarker
                        + "@example.com"
        );

        supplier.setAddress(
                "Temporary test address"
        );

        supplierService.addSupplier(supplier);

        check(
                supplier.getId() > 0,
                "Supplier added with ID "
                        + supplier.getId()
        );

        Supplier savedSupplier =
                supplierService.getSupplierById(
                        supplier.getId()
                );

        check(
                savedSupplier != null,
                "Supplier retrieved by ID"
        );

        List<Supplier> searchResults =
                supplierService.searchSuppliers(
                        shortMarker
                );

        check(
                containsSupplier(
                        searchResults,
                        supplier.getId()
                ),
                "Supplier search returned test record"
        );

        String updatedSupplierName =
                "Updated Test Supplier " + marker;

        supplier.setSupplierName(
                updatedSupplierName
        );

        supplier.setContactPerson(
                "Updated Test Contact"
        );

        supplierService.updateSupplier(supplier);

        Supplier updatedSupplier =
                supplierService.getSupplierById(
                        supplier.getId()
                );

        check(
                updatedSupplier != null
                        && updatedSupplierName.equals(
                        updatedSupplier
                                .getSupplierName()
                ),
                "Supplier updated successfully"
        );
    }

    private static void testProductCrud(
            ProductService productService,
            GeneralProduct product,
            Category category,
            Supplier supplier,
            String marker,
            String shortMarker
    ) throws ValidationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "3. PRODUCT CRUD TEST"
        );

        product.setSku(
                "TST-" + shortMarker
        );

        product.setProductName(
                "Test Product " + marker
        );

        product.setCategoryId(
                category.getId()
        );

        product.setSupplierId(
                supplier.getId()
        );

        product.setQuantity(10);
        product.setReorderLevel(3);
        product.setUnitPrice(12.50);

        product.setDescription(
                "Temporary general product"
        );

        productService.addProduct(product);

        check(
                product.getId() > 0,
                "Product added with ID "
                        + product.getId()
        );

        Product savedProduct =
                productService.getProductById(
                        product.getId()
                );

        check(
                savedProduct != null,
                "Product retrieved by ID"
        );

        check(
                savedProduct
                        instanceof GeneralProduct,
                "Product mapped as GeneralProduct"
        );

        check(
                "GENERAL".equals(
                        savedProduct.getProductType()
                ),
                "Product type is GENERAL"
        );

        List<Product> searchResults =
                productService.searchProducts(
                        shortMarker
                );

        check(
                containsProduct(
                        searchResults,
                        product.getId()
                ),
                "Product search returned test record"
        );

        String updatedProductName =
                "Updated Test Product " + marker;

        product.setProductName(
                updatedProductName
        );

        product.setQuantity(12);
        product.setReorderLevel(4);
        product.setUnitPrice(15.75);

        product.setDescription(
                "Updated temporary product"
        );

        productService.updateProduct(product);

        Product updatedProduct =
                productService.getProductById(
                        product.getId()
                );

        check(
                updatedProduct != null
                        && updatedProductName.equals(
                        updatedProduct
                                .getProductName()
                ),
                "Product name updated successfully"
        );

        check(
                updatedProduct.getQuantity() == 12,
                "Product quantity updated to 12"
        );

        check(
                Math.abs(
                        updatedProduct.getUnitPrice()
                                - 15.75
                ) < 0.001,
                "Product unit price updated to £15.75"
        );
    }

    private static void deleteTemporaryRecords(
            ProductService productService,
            SupplierService supplierService,
            CategoryService categoryService,
            Product product,
            Supplier supplier,
            Category category
    ) throws ValidationException,
            DatabaseException {

        System.out.println();
        System.out.println(
                "4. DELETE TEST"
        );

        int productId =
                product.getId();

        int supplierId =
                supplier.getId();

        int categoryId =
                category.getId();

        productService.deleteProduct(
                productId
        );

        check(
                productService.getProductById(
                        productId
                ) == null,
                "Product deleted successfully"
        );

        product.setId(0);

        supplierService.deleteSupplier(
                supplierId
        );

        check(
                supplierService.getSupplierById(
                        supplierId
                ) == null,
                "Supplier deleted successfully"
        );

        supplier.setId(0);

        categoryService.deleteCategory(
                categoryId
        );

        check(
                categoryService.getCategoryById(
                        categoryId
                ) == null,
                "Category deleted successfully"
        );

        category.setId(0);
    }

    private static boolean containsCategory(
            List<Category> categories,
            int categoryId
    ) {
        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return true;
            }
        }

        return false;
    }

    private static boolean containsSupplier(
            List<Supplier> suppliers,
            int supplierId
    ) {
        for (Supplier supplier : suppliers) {
            if (supplier.getId() == supplierId) {
                return true;
            }
        }

        return false;
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

    private static void cleanupAfterFailure(
            ProductService productService,
            SupplierService supplierService,
            CategoryService categoryService,
            Product product,
            Supplier supplier,
            Category category
    ) {
        System.out.println();
        System.out.println(
                "AUTOMATIC CLEANUP"
        );

        if (product.getId() > 0) {
            try {
                Product existingProduct =
                        productService.getProductById(
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

        if (supplier.getId() > 0) {
            try {
                Supplier existingSupplier =
                        supplierService.getSupplierById(
                                supplier.getId()
                        );

                if (existingSupplier != null) {
                    supplierService.deleteSupplier(
                            supplier.getId()
                    );

                    System.out.println(
                            "[PASS] Temporary supplier removed."
                    );
                }

            } catch (ValidationException
                    | DatabaseException exception) {

                System.out.println(
                        "[FAILED] Supplier cleanup: "
                                + exception.getMessage()
                );
            }
        }

        if (category.getId() > 0) {
            try {
                Category existingCategory =
                        categoryService.getCategoryById(
                                category.getId()
                        );

                if (existingCategory != null) {
                    categoryService.deleteCategory(
                            category.getId()
                    );

                    System.out.println(
                            "[PASS] Temporary category removed."
                    );
                }

            } catch (ValidationException
                    | DatabaseException exception) {

                System.out.println(
                        "[FAILED] Category cleanup: "
                                + exception.getMessage()
                );
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