package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import dao.JdbcStockMovementDAO;
import dao.StockMovementDAO;
import exception.DatabaseException;
import exception.InsufficientStockException;
import exception.ValidationException;
import model.StockIn;
import model.StockMovement;
import model.StockOut;
import util.DatabaseConnection;
import util.ValidationUtil;

public class StockService {

    private static final String FIND_QUANTITY_SQL =
            "SELECT quantity "
                    + "FROM products "
                    + "WHERE product_id = ?";

    private static final String UPDATE_QUANTITY_SQL =
            "UPDATE products "
                    + "SET quantity = ? "
                    + "WHERE product_id = ?";

    private static final String INSERT_MOVEMENT_SQL =
            "INSERT INTO stock_movements "
                    + "(product_id, user_id, movement_type, "
                    + "quantity, quantity_before, quantity_after, "
                    + "movement_date, notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private final StockMovementDAO stockMovementDAO;

    public StockService() {
        this.stockMovementDAO =
                new JdbcStockMovementDAO();
    }

    public StockService(
            StockMovementDAO stockMovementDAO
    ) {
        if (stockMovementDAO == null) {
            throw new IllegalArgumentException(
                    "StockMovementDAO cannot be null."
            );
        }

        this.stockMovementDAO = stockMovementDAO;
    }

    public StockMovement recordStockIn(
            int productId,
            int userId,
            int quantity,
            String notes
    ) throws ValidationException,
            DatabaseException {

        validateMovementInput(
                productId,
                userId,
                quantity
        );

        StockIn stockIn = new StockIn();

        stockIn.setProductId(productId);
        stockIn.setUserId(userId);
        stockIn.setQuantity(quantity);
        stockIn.setNotes(
                cleanOptionalText(notes)
        );

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            try {
                connection.setAutoCommit(false);

                int quantityBefore =
                        findCurrentQuantity(
                                connection,
                                productId
                        );

                int quantityAfter =
                        stockIn.calculateNewQuantity(
                                quantityBefore
                        );

                LocalDateTime movementDate =
                        LocalDateTime.now()
                                .withNano(0);

                stockIn.setQuantityBefore(
                        quantityBefore
                );

                stockIn.setQuantityAfter(
                        quantityAfter
                );

                stockIn.setMovementDate(
                        movementDate
                );

                stockIn.setCreatedAt(
                        movementDate
                );

                updateProductQuantity(
                        connection,
                        productId,
                        quantityAfter
                );

                insertStockMovement(
                        connection,
                        stockIn
                );

                connection.commit();

                return stockIn;

            } catch (ValidationException exception) {
                rollbackQuietly(connection);
                throw exception;

            } catch (SQLException exception) {
                rollbackQuietly(connection);

                throw new DatabaseException(
                        "Failed to record stock-in.",
                        exception
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to complete stock-in operation.",
                    exception
            );
        }
    }

    public StockMovement recordStockOut(
            int productId,
            int userId,
            int quantity,
            String notes
    ) throws ValidationException,
            InsufficientStockException,
            DatabaseException {

        validateMovementInput(
                productId,
                userId,
                quantity
        );

        StockOut stockOut = new StockOut();

        stockOut.setProductId(productId);
        stockOut.setUserId(userId);
        stockOut.setQuantity(quantity);
        stockOut.setNotes(
                cleanOptionalText(notes)
        );

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            try {
                connection.setAutoCommit(false);

                int quantityBefore =
                        findCurrentQuantity(
                                connection,
                                productId
                        );

                if (quantity > quantityBefore) {
                    throw new InsufficientStockException(
                            "Insufficient stock. Available quantity: "
                                    + quantityBefore
                                    + "."
                    );
                }

                int quantityAfter =
                        stockOut.calculateNewQuantity(
                                quantityBefore
                        );

                LocalDateTime movementDate =
                        LocalDateTime.now()
                                .withNano(0);

                stockOut.setQuantityBefore(
                        quantityBefore
                );

                stockOut.setQuantityAfter(
                        quantityAfter
                );

                stockOut.setMovementDate(
                        movementDate
                );

                stockOut.setCreatedAt(
                        movementDate
                );

                updateProductQuantity(
                        connection,
                        productId,
                        quantityAfter
                );

                insertStockMovement(
                        connection,
                        stockOut
                );

                connection.commit();

                return stockOut;

            } catch (ValidationException exception) {
                rollbackQuietly(connection);
                throw exception;

            } catch (InsufficientStockException exception) {
                rollbackQuietly(connection);
                throw exception;

            } catch (SQLException exception) {
                rollbackQuietly(connection);

                throw new DatabaseException(
                        "Failed to record stock-out.",
                        exception
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to complete stock-out operation.",
                    exception
            );
        }
    }

    public List<StockMovement> getAllStockMovements()
            throws DatabaseException {

        return stockMovementDAO.findAll();
    }

    public List<StockMovement>
            getStockMovementsByProductId(
                    int productId
            ) throws ValidationException,
                    DatabaseException {

        ValidationUtil.requirePositive(
                productId,
                "Product ID"
        );

        return stockMovementDAO.findByProductId(
                productId
        );
    }

    private void validateMovementInput(
            int productId,
            int userId,
            int quantity
    ) throws ValidationException {

        ValidationUtil.requirePositive(
                productId,
                "Product ID"
        );

        ValidationUtil.requirePositive(
                userId,
                "User ID"
        );

        ValidationUtil.requirePositive(
                quantity,
                "Stock quantity"
        );
    }

    private int findCurrentQuantity(
            Connection connection,
            int productId
    ) throws SQLException,
            ValidationException {

        try (PreparedStatement statement =
                connection.prepareStatement(
                        FIND_QUANTITY_SQL
                )) {

            statement.setInt(
                    1,
                    productId
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(
                            "quantity"
                    );
                }
            }
        }

        throw new ValidationException(
                "Selected product does not exist."
        );
    }

    private void updateProductQuantity(
            Connection connection,
            int productId,
            int newQuantity
    ) throws SQLException {

        try (PreparedStatement statement =
                connection.prepareStatement(
                        UPDATE_QUANTITY_SQL
                )) {

            statement.setInt(
                    1,
                    newQuantity
            );

            statement.setInt(
                    2,
                    productId
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Product quantity was not updated."
                );
            }
        }
    }

    private void insertStockMovement(
            Connection connection,
            StockMovement stockMovement
    ) throws SQLException {

        try (PreparedStatement statement =
                connection.prepareStatement(
                        INSERT_MOVEMENT_SQL,
                        Statement.RETURN_GENERATED_KEYS
                )) {

            statement.setInt(
                    1,
                    stockMovement.getProductId()
            );

            statement.setInt(
                    2,
                    stockMovement.getUserId()
            );

            statement.setString(
                    3,
                    stockMovement.getMovementType()
            );

            statement.setInt(
                    4,
                    stockMovement.getQuantity()
            );

            statement.setInt(
                    5,
                    stockMovement.getQuantityBefore()
            );

            statement.setInt(
                    6,
                    stockMovement.getQuantityAfter()
            );

            statement.setString(
                    7,
                    stockMovement.getMovementDate()
                            .toString()
                            .replace("T", " ")
            );

            statement.setString(
                    8,
                    stockMovement.getNotes()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Stock movement was not inserted."
                );
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    stockMovement.setId(
                            generatedKeys.getInt(1)
                    );
                }
            }
        }
    }

    private void rollbackQuietly(
            Connection connection
    ) {
        try {
            connection.rollback();

        } catch (SQLException ignored) {
            // Original error will be reported.
        }
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}