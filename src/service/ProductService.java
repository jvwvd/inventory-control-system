package service;

import java.util.List;

import dao.CategoryDAO;
import dao.JdbcCategoryDAO;
import dao.JdbcProductDAO;
import dao.JdbcSupplierDAO;
import dao.ProductDAO;
import dao.SupplierDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.ElectronicProduct;
import model.GeneralProduct;
import model.PerishableProduct;
import model.Product;
import util.ValidationUtil;

public class ProductService {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final SupplierDAO supplierDAO;

    public ProductService() {
        this.productDAO = new JdbcProductDAO();
        this.categoryDAO = new JdbcCategoryDAO();
        this.supplierDAO = new JdbcSupplierDAO();
    }

    public ProductService(
            ProductDAO productDAO,
            CategoryDAO categoryDAO,
            SupplierDAO supplierDAO
    ) {
        if (productDAO == null
                || categoryDAO == null
                || supplierDAO == null) {

            throw new IllegalArgumentException(
                    "Product, category and supplier "
                            + "DAOs cannot be null."
            );
        }

        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
        this.supplierDAO = supplierDAO;
    }

    public void addProduct(Product product)
            throws ValidationException,
            DatabaseException {

        validateProduct(product);
        validateCategoryAndSupplier(product);

        String sku =
                product.getSku().trim();

        Product existingProduct =
                productDAO.findBySku(sku);

        if (existingProduct != null) {
            throw new ValidationException(
                    "Product SKU already exists."
            );
        }

        cleanProductValues(product);
        productDAO.add(product);
    }

    public void updateProduct(Product product)
            throws ValidationException,
            DatabaseException {

        validateProduct(product);

        ValidationUtil.requirePositive(
                product.getId(),
                "Product ID"
        );

        validateCategoryAndSupplier(product);

        String sku =
                product.getSku().trim();

        Product existingProduct =
                productDAO.findBySku(sku);

        if (existingProduct != null
                && existingProduct.getId()
                != product.getId()) {

            throw new ValidationException(
                    "Product SKU already exists."
            );
        }

        cleanProductValues(product);
        productDAO.update(product);
    }

    public void deleteProduct(int productId)
            throws ValidationException,
            DatabaseException {

        ValidationUtil.requirePositive(
                productId,
                "Product ID"
        );

        productDAO.delete(productId);
    }

    public Product getProductById(int productId)
            throws ValidationException,
            DatabaseException {

        ValidationUtil.requirePositive(
                productId,
                "Product ID"
        );

        return productDAO.findById(productId);
    }

    public List<Product> getAllProducts()
            throws DatabaseException {

        return productDAO.findAll();
    }

    public List<Product> searchProducts(
            String keyword
    ) throws DatabaseException {

        return productDAO.search(keyword);
    }

    public List<Product> getLowStockProducts()
            throws DatabaseException {

        return productDAO.findLowStockProducts();
    }

    private void validateProduct(Product product)
            throws ValidationException {

        ValidationUtil.requireNotNull(
                product,
                "Product"
        );

        ValidationUtil.requireNonBlank(
                product.getSku(),
                "Product SKU"
        );

        ValidationUtil.requireNonBlank(
                product.getProductName(),
                "Product name"
        );

        ValidationUtil.requirePositive(
                product.getCategoryId(),
                "Category ID"
        );

        ValidationUtil.requirePositive(
                product.getSupplierId(),
                "Supplier ID"
        );

        ValidationUtil.requireNonNegative(
                product.getQuantity(),
                "Product quantity"
        );

        ValidationUtil.requireNonNegative(
                product.getReorderLevel(),
                "Reorder level"
        );

        ValidationUtil.requirePositive(
                product.getUnitPrice(),
                "Unit price"
        );

        validateProductType(product);
    }

    private void validateProductType(Product product)
            throws ValidationException {

        if (product instanceof GeneralProduct) {
            return;
        }

        if (product instanceof PerishableProduct) {
            PerishableProduct perishableProduct =
                    (PerishableProduct) product;

            ValidationUtil.requireNotNull(
                    perishableProduct.getExpiryDate(),
                    "Expiry date"
            );

            return;
        }

        if (product instanceof ElectronicProduct) {
            ElectronicProduct electronicProduct =
                    (ElectronicProduct) product;

            ValidationUtil.requirePositive(
                    electronicProduct
                            .getWarrantyMonths(),
                    "Warranty months"
            );

            return;
        }

        throw new ValidationException(
                "Unsupported product type."
        );
    }

    private void validateCategoryAndSupplier(
            Product product
    ) throws ValidationException,
            DatabaseException {

        if (categoryDAO.findById(
                product.getCategoryId()
        ) == null) {

            throw new ValidationException(
                    "Selected category does not exist."
            );
        }

        if (supplierDAO.findById(
                product.getSupplierId()
        ) == null) {

            throw new ValidationException(
                    "Selected supplier does not exist."
            );
        }
    }

    private void cleanProductValues(Product product) {
        product.setSku(
                product.getSku().trim()
        );

        product.setProductName(
                product.getProductName().trim()
        );

        product.setDescription(
                cleanOptionalText(
                        product.getDescription()
                )
        );
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}