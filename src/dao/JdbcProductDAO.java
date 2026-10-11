package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import exception.DatabaseException;
import model.ElectronicProduct;
import model.GeneralProduct;
import model.PerishableProduct;
import model.Product;
import util.DatabaseConnection;

public class JdbcProductDAO implements ProductDAO {

    private static final String INSERT_SQL =
            "INSERT INTO products "
                    + "(sku, product_name, product_type, "
                    + "category_id, supplier_id, quantity, "
                    + "reorder_level, unit_price, description, "
                    + "expiry_date, warranty_months) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE products "
                    + "SET sku = ?, "
                    + "product_name = ?, "
                    + "product_type = ?, "
                    + "category_id = ?, "
                    + "supplier_id = ?, "
                    + "quantity = ?, "
                    + "reorder_level = ?, "
                    + "unit_price = ?, "
                    + "description = ?, "
                    + "expiry_date = ?, "
                    + "warranty_months = ? "
                    + "WHERE product_id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM products "
                    + "WHERE product_id = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT product_id, sku, product_name, "
                    + "product_type, category_id, supplier_id, "
                    + "quantity, reorder_level, unit_price, "
                    + "description, expiry_date, warranty_months, "
                    + "created_at "
                    + "FROM products "
                    + "WHERE product_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT product_id, sku, product_name, "
                    + "product_type, category_id, supplier_id, "
                    + "quantity, reorder_level, unit_price, "
                    + "description, expiry_date, warranty_months, "
                    + "created_at "
                    + "FROM products "
                    + "ORDER BY product_name";

    private static final String FIND_BY_SKU_SQL =
            "SELECT product_id, sku, product_name, "
                    + "product_type, category_id, supplier_id, "
                    + "quantity, reorder_level, unit_price, "
                    + "description, expiry_date, warranty_months, "
                    + "created_at "
                    + "FROM products "
                    + "WHERE LOWER(sku) = LOWER(?) "
                    + "LIMIT 1";

    private static final String SEARCH_SQL =
            "SELECT product_id, sku, product_name, "
                    + "product_type, category_id, supplier_id, "
                    + "quantity, reorder_level, unit_price, "
                    + "description, expiry_date, warranty_months, "
                    + "created_at "
                    + "FROM products "
                    + "WHERE LOWER(sku) LIKE LOWER(?) "
                    + "OR LOWER(product_name) LIKE LOWER(?) "
                    + "OR LOWER(product_type) LIKE LOWER(?) "
                    + "ORDER BY product_name";

    private static final String FIND_LOW_STOCK_SQL =
            "SELECT product_id, sku, product_name, "
                    + "product_type, category_id, supplier_id, "
                    + "quantity, reorder_level, unit_price, "
                    + "description, expiry_date, warranty_months, "
                    + "created_at "
                    + "FROM products "
                    + "WHERE quantity <= reorder_level "
                    + "ORDER BY quantity, product_name";

    @Override
    public void add(Product product)
            throws DatabaseException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                INSERT_SQL,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            setProductParameters(
                    statement,
                    product
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Failed to add product. "
                                + "No database row was inserted."
                );
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    product.setId(
                            generatedKeys.getInt(1)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to add product.",
                    exception
            );
        }
    }

    @Override
    public void update(Product product)
            throws DatabaseException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                UPDATE_SQL
                        )
        ) {
            setProductParameters(
                    statement,
                    product
            );

            statement.setInt(
                    12,
                    product.getId()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Product with ID "
                                + product.getId()
                                + " was not found."
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to update product.",
                    exception
            );
        }
    }

    @Override
    public void delete(int id)
            throws DatabaseException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                DELETE_SQL
                        )
        ) {
            statement.setInt(1, id);

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Product with ID "
                                + id
                                + " was not found."
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to delete product.",
                    exception
            );
        }
    }

    @Override
    public Product findById(int id)
            throws DatabaseException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_ID_SQL
                        )
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }

                return null;
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to find product by ID.",
                    exception
            );
        }
    }

    @Override
    public List<Product> findAll()
            throws DatabaseException {

        List<Product> products =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_ALL_SQL
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                products.add(
                        mapProduct(resultSet)
                );
            }

            return products;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to retrieve products.",
                    exception
            );
        }
    }

    @Override
    public Product findBySku(String sku)
            throws DatabaseException {

        if (sku == null || sku.isBlank()) {
            return null;
        }

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_SKU_SQL
                        )
        ) {
            statement.setString(
                    1,
                    sku.trim()
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }

                return null;
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to find product by SKU.",
                    exception
            );
        }
    }

    @Override
    public List<Product> search(String keyword)
            throws DatabaseException {

        if (keyword == null
                || keyword.isBlank()) {
            return findAll();
        }

        List<Product> products =
                new ArrayList<>();

        String searchPattern =
                "%" + keyword.trim() + "%";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                SEARCH_SQL
                        )
        ) {
            statement.setString(
                    1,
                    searchPattern
            );

            statement.setString(
                    2,
                    searchPattern
            );

            statement.setString(
                    3,
                    searchPattern
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(
                            mapProduct(resultSet)
                    );
                }
            }

            return products;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to search products.",
                    exception
            );
        }
    }

    @Override
    public List<Product> findLowStockProducts()
            throws DatabaseException {

        List<Product> products =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_LOW_STOCK_SQL
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                products.add(
                        mapProduct(resultSet)
                );
            }

            return products;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to retrieve low-stock products.",
                    exception
            );
        }
    }

    private void setProductParameters(
            PreparedStatement statement,
            Product product
    ) throws SQLException {

        statement.setString(
                1,
                product.getSku()
        );

        statement.setString(
                2,
                product.getProductName()
        );

        statement.setString(
                3,
                product.getProductType()
        );

        statement.setInt(
                4,
                product.getCategoryId()
        );

        statement.setInt(
                5,
                product.getSupplierId()
        );

        statement.setInt(
                6,
                product.getQuantity()
        );

        statement.setInt(
                7,
                product.getReorderLevel()
        );

        statement.setDouble(
                8,
                product.getUnitPrice()
        );

        statement.setString(
                9,
                product.getDescription()
        );

        setSpecialProductParameters(
                statement,
                product,
                10,
                11
        );
    }

    private void setSpecialProductParameters(
            PreparedStatement statement,
            Product product,
            int expiryDateIndex,
            int warrantyMonthsIndex
    ) throws SQLException {

        String productType =
                product.getProductType();

        if ("GENERAL".equals(productType)) {
            statement.setNull(
                    expiryDateIndex,
                    Types.VARCHAR
            );

            statement.setNull(
                    warrantyMonthsIndex,
                    Types.INTEGER
            );

            return;
        }

        if ("PERISHABLE".equals(productType)) {
            if (!(product
                    instanceof PerishableProduct)) {

                throw new SQLException(
                        "PERISHABLE product type does not "
                                + "match the Java product class."
                );
            }

            PerishableProduct perishableProduct =
                    (PerishableProduct) product;

            LocalDate expiryDate =
                    perishableProduct.getExpiryDate();

            if (expiryDate == null) {
                statement.setNull(
                        expiryDateIndex,
                        Types.VARCHAR
                );
            } else {
                statement.setString(
                        expiryDateIndex,
                        expiryDate.toString()
                );
            }

            statement.setNull(
                    warrantyMonthsIndex,
                    Types.INTEGER
            );

            return;
        }

        if ("ELECTRONIC".equals(productType)) {
            if (!(product
                    instanceof ElectronicProduct)) {

                throw new SQLException(
                        "ELECTRONIC product type does not "
                                + "match the Java product class."
                );
            }

            ElectronicProduct electronicProduct =
                    (ElectronicProduct) product;

            statement.setNull(
                    expiryDateIndex,
                    Types.VARCHAR
            );

            statement.setInt(
                    warrantyMonthsIndex,
                    electronicProduct
                            .getWarrantyMonths()
            );

            return;
        }

        throw new SQLException(
                "Unsupported product type: "
                        + productType
        );
    }

    private Product mapProduct(
            ResultSet resultSet
    ) throws SQLException {

        String productType =
                resultSet.getString(
                        "product_type"
                );

        Product product =
                createProduct(
                        productType,
                        resultSet
                );

        product.setId(
                resultSet.getInt("product_id")
        );

        product.setSku(
                resultSet.getString("sku")
        );

        product.setProductName(
                resultSet.getString(
                        "product_name"
                )
        );

        product.setCategoryId(
                resultSet.getInt(
                        "category_id"
                )
        );

        product.setSupplierId(
                resultSet.getInt(
                        "supplier_id"
                )
        );

        product.setQuantity(
                resultSet.getInt("quantity")
        );

        product.setReorderLevel(
                resultSet.getInt(
                        "reorder_level"
                )
        );

        product.setUnitPrice(
                resultSet.getDouble(
                        "unit_price"
                )
        );

        product.setDescription(
                resultSet.getString(
                        "description"
                )
        );

        product.setCreatedAt(
                parseDateTime(
                        resultSet.getString(
                                "created_at"
                        )
                )
        );

        return product;
    }

    private Product createProduct(
            String productType,
            ResultSet resultSet
    ) throws SQLException {

        if ("GENERAL".equals(productType)) {
            return new GeneralProduct();
        }

        if ("PERISHABLE".equals(productType)) {
            PerishableProduct product =
                    new PerishableProduct();

            String expiryDateText =
                    resultSet.getString(
                            "expiry_date"
                    );

            product.setExpiryDate(
                    parseDate(
                            expiryDateText
                    )
            );

            return product;
        }

        if ("ELECTRONIC".equals(productType)) {
            ElectronicProduct product =
                    new ElectronicProduct();

            int warrantyMonths =
                    resultSet.getInt(
                            "warranty_months"
                    );

            if (!resultSet.wasNull()) {
                product.setWarrantyMonths(
                        warrantyMonths
                );
            }

            return product;
        }

        throw new SQLException(
                "Unsupported product type in database: "
                        + productType
        );
    }

    private LocalDate parseDate(
            String dateText
    ) throws SQLException {

        if (dateText == null
                || dateText.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(
                    dateText
            );

        } catch (DateTimeParseException exception) {
            throw new SQLException(
                    "Invalid expiry_date value: "
                            + dateText,
                    exception
            );
        }
    }

    private LocalDateTime parseDateTime(
            String dateTimeText
    ) throws SQLException {

        if (dateTimeText == null
                || dateTimeText.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(
                    dateTimeText.replace(
                            " ",
                            "T"
                    )
            );

        } catch (DateTimeParseException exception) {
            throw new SQLException(
                    "Invalid created_at value: "
                            + dateTimeText,
                    exception
            );
        }
    }
}