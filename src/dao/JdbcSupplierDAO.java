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
import model.Supplier;
import util.DatabaseConnection;

public class JdbcSupplierDAO implements SupplierDAO {

    private static final String INSERT_SQL =
            "INSERT INTO suppliers "
                    + "(supplier_name, contact_person, phone, "
                    + "email, address) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE suppliers "
                    + "SET supplier_name = ?, "
                    + "contact_person = ?, "
                    + "phone = ?, "
                    + "email = ?, "
                    + "address = ? "
                    + "WHERE supplier_id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM suppliers "
                    + "WHERE supplier_id = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT supplier_id, supplier_name, "
                    + "contact_person, phone, email, "
                    + "address, created_at "
                    + "FROM suppliers "
                    + "WHERE supplier_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT supplier_id, supplier_name, "
                    + "contact_person, phone, email, "
                    + "address, created_at "
                    + "FROM suppliers "
                    + "ORDER BY supplier_name";

    private static final String FIND_BY_NAME_SQL =
            "SELECT supplier_id, supplier_name, "
                    + "contact_person, phone, email, "
                    + "address, created_at "
                    + "FROM suppliers "
                    + "WHERE LOWER(supplier_name) = LOWER(?) "
                    + "LIMIT 1";

    private static final String SEARCH_BY_NAME_SQL =
            "SELECT supplier_id, supplier_name, "
                    + "contact_person, phone, email, "
                    + "address, created_at "
                    + "FROM suppliers "
                    + "WHERE LOWER(supplier_name) LIKE LOWER(?) "
                    + "ORDER BY supplier_name";

    @Override
    public void add(Supplier supplier)
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
            statement.setString(
                    1,
                    supplier.getSupplierName()
            );

            statement.setString(
                    2,
                    supplier.getContactPerson()
            );

            statement.setString(
                    3,
                    supplier.getPhone()
            );

            statement.setString(
                    4,
                    supplier.getEmail()
            );

            statement.setString(
                    5,
                    supplier.getAddress()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Failed to add supplier. "
                                + "No database row was inserted."
                );
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    supplier.setId(
                            generatedKeys.getInt(1)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to add supplier.",
                    exception
            );
        }
    }

    @Override
    public void update(Supplier supplier)
            throws DatabaseException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                UPDATE_SQL
                        )
        ) {
            statement.setString(
                    1,
                    supplier.getSupplierName()
            );

            statement.setString(
                    2,
                    supplier.getContactPerson()
            );

            statement.setString(
                    3,
                    supplier.getPhone()
            );

            statement.setString(
                    4,
                    supplier.getEmail()
            );

            statement.setString(
                    5,
                    supplier.getAddress()
            );

            statement.setInt(
                    6,
                    supplier.getId()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Supplier with ID "
                                + supplier.getId()
                                + " was not found."
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to update supplier.",
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
                        "Supplier with ID "
                                + id
                                + " was not found."
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to delete supplier.",
                    exception
            );
        }
    }

    @Override
    public Supplier findById(int id)
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
                    return mapSupplier(resultSet);
                }

                return null;
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to find supplier by ID.",
                    exception
            );
        }
    }

    @Override
    public List<Supplier> findAll()
            throws DatabaseException {

        List<Supplier> suppliers =
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
                suppliers.add(
                        mapSupplier(resultSet)
                );
            }

            return suppliers;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to retrieve suppliers.",
                    exception
            );
        }
    }

    @Override
    public Supplier findByName(String supplierName)
            throws DatabaseException {

        if (supplierName == null
                || supplierName.isBlank()) {
            return null;
        }

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_NAME_SQL
                        )
        ) {
            statement.setString(
                    1,
                    supplierName.trim()
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSupplier(resultSet);
                }

                return null;
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to find supplier by name.",
                    exception
            );
        }
    }

    @Override
    public List<Supplier> searchByName(String keyword)
            throws DatabaseException {

        if (keyword == null
                || keyword.isBlank()) {
            return findAll();
        }

        List<Supplier> suppliers =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                SEARCH_BY_NAME_SQL
                        )
        ) {
            statement.setString(
                    1,
                    "%" + keyword.trim() + "%"
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {
                    suppliers.add(
                            mapSupplier(resultSet)
                    );
                }
            }

            return suppliers;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to search suppliers.",
                    exception
            );
        }
    }

    private Supplier mapSupplier(
            ResultSet resultSet
    ) throws SQLException {

        Supplier supplier =
                new Supplier();

        supplier.setId(
                resultSet.getInt("supplier_id")
        );

        supplier.setSupplierName(
                resultSet.getString(
                        "supplier_name"
                )
        );

        supplier.setContactPerson(
                resultSet.getString(
                        "contact_person"
                )
        );

        supplier.setPhone(
                resultSet.getString("phone")
        );

        supplier.setEmail(
                resultSet.getString("email")
        );

        supplier.setAddress(
                resultSet.getString("address")
        );

        supplier.setCreatedAt(
                parseDateTime(
                        resultSet.getString(
                                "created_at"
                        )
                )
        );

        return supplier;
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