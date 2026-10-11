package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import exception.DatabaseException;
import model.StockIn;
import model.StockMovement;
import model.StockOut;
import util.DatabaseConnection;

public class JdbcStockMovementDAO
        implements StockMovementDAO {

    private static final String INSERT_SQL =
            "INSERT INTO stock_movements "
                    + "(product_id, user_id, movement_type, "
                    + "quantity, quantity_before, quantity_after, "
                    + "movement_date, notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String FIND_ALL_SQL =
            "SELECT movement_id, product_id, user_id, "
                    + "movement_type, quantity, quantity_before, "
                    + "quantity_after, movement_date, notes "
                    + "FROM stock_movements "
                    + "ORDER BY movement_date DESC, "
                    + "movement_id DESC";

    private static final String FIND_BY_PRODUCT_ID_SQL =
            "SELECT movement_id, product_id, user_id, "
                    + "movement_type, quantity, quantity_before, "
                    + "quantity_after, movement_date, notes "
                    + "FROM stock_movements "
                    + "WHERE product_id = ? "
                    + "ORDER BY movement_date DESC, "
                    + "movement_id DESC";

    @Override
    public void add(StockMovement stockMovement)
            throws DatabaseException {

        LocalDateTime movementDate =
                stockMovement.getMovementDate();

        if (movementDate == null) {
            movementDate =
                    LocalDateTime.now().withNano(0);

            stockMovement.setMovementDate(
                    movementDate
            );
        }

        if (stockMovement.getCreatedAt() == null) {
            stockMovement.setCreatedAt(
                    movementDate
            );
        }

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                INSERT_SQL,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
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
                    movementDate.toString()
                            .replace("T", " ")
            );

            statement.setString(
                    8,
                    stockMovement.getNotes()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Failed to add stock movement."
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

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to add stock movement.",
                    exception
            );
        }
    }

    @Override
    public List<StockMovement> findAll()
            throws DatabaseException {

        List<StockMovement> stockMovements =
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
                stockMovements.add(
                        mapStockMovement(resultSet)
                );
            }

            return stockMovements;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to retrieve stock movements.",
                    exception
            );
        }
    }

    @Override
    public List<StockMovement> findByProductId(
            int productId
    ) throws DatabaseException {

        List<StockMovement> stockMovements =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_PRODUCT_ID_SQL
                        )
        ) {
            statement.setInt(
                    1,
                    productId
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    stockMovements.add(
                            mapStockMovement(resultSet)
                    );
                }
            }

            return stockMovements;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to retrieve product "
                            + "stock movements.",
                    exception
            );
        }
    }

    private StockMovement mapStockMovement(
            ResultSet resultSet
    ) throws SQLException {

        String movementType =
                resultSet.getString(
                        "movement_type"
                );

        StockMovement stockMovement;

        if ("IN".equalsIgnoreCase(movementType)) {
            stockMovement = new StockIn();

        } else if ("OUT".equalsIgnoreCase(
                movementType
        )) {
            stockMovement = new StockOut();

        } else {
            throw new SQLException(
                    "Unsupported movement type: "
                            + movementType
            );
        }

        stockMovement.setId(
                resultSet.getInt(
                        "movement_id"
                )
        );

        stockMovement.setProductId(
                resultSet.getInt(
                        "product_id"
                )
        );

        stockMovement.setUserId(
                resultSet.getInt(
                        "user_id"
                )
        );

        stockMovement.setQuantity(
                resultSet.getInt(
                        "quantity"
                )
        );

        stockMovement.setQuantityBefore(
                resultSet.getInt(
                        "quantity_before"
                )
        );

        stockMovement.setQuantityAfter(
                resultSet.getInt(
                        "quantity_after"
                )
        );

        LocalDateTime movementDate =
                parseDateTime(
                        resultSet.getString(
                                "movement_date"
                        )
                );

        stockMovement.setMovementDate(
                movementDate
        );

        stockMovement.setCreatedAt(
                movementDate
        );

        stockMovement.setNotes(
                resultSet.getString(
                        "notes"
                )
        );

        return stockMovement;
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
                    "Invalid movement_date value: "
                            + dateTimeText,
                    exception
            );
        }
    }
}