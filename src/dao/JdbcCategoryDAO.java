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
import model.Category;
import util.DatabaseConnection;

public class JdbcCategoryDAO implements CategoryDAO {

    private static final String INSERT_SQL =
            "INSERT INTO categories "
                    + "(category_name, description) "
                    + "VALUES (?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE categories "
                    + "SET category_name = ?, description = ? "
                    + "WHERE category_id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM categories "
                    + "WHERE category_id = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT category_id, category_name, "
                    + "description, created_at "
                    + "FROM categories "
                    + "WHERE category_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT category_id, category_name, "
                    + "description, created_at "
                    + "FROM categories "
                    + "ORDER BY category_name";

    private static final String FIND_BY_NAME_SQL =
            "SELECT category_id, category_name, "
                    + "description, created_at "
                    + "FROM categories "
                    + "WHERE LOWER(category_name) = LOWER(?) "
                    + "LIMIT 1";

    private static final String SEARCH_BY_NAME_SQL =
            "SELECT category_id, category_name, "
                    + "description, created_at "
                    + "FROM categories "
                    + "WHERE LOWER(category_name) LIKE LOWER(?) "
                    + "ORDER BY category_name";

    @Override
    public void add(Category category)
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
                    category.getCategoryName()
            );

            statement.setString(
                    2,
                    category.getDescription()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Failed to add category. "
                                + "No database row was inserted."
                );
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    category.setId(
                            generatedKeys.getInt(1)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to add category.",
                    exception
            );
        }
    }

    @Override
    public void update(Category category)
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
                    category.getCategoryName()
            );

            statement.setString(
                    2,
                    category.getDescription()
            );

            statement.setInt(
                    3,
                    category.getId()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException(
                        "Category with ID "
                                + category.getId()
                                + " was not found."
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to update category.",
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
                        "Category with ID "
                                + id
                                + " was not found."
                );
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to delete category.",
                    exception
            );
        }
    }

    @Override
    public Category findById(int id)
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
                    return mapCategory(resultSet);
                }

                return null;
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to find category by ID.",
                    exception
            );
        }
    }

    @Override
    public List<Category> findAll()
            throws DatabaseException {

        List<Category> categories =
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
                categories.add(
                        mapCategory(resultSet)
                );
            }

            return categories;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to retrieve categories.",
                    exception
            );
        }
    }

    @Override
    public Category findByName(String categoryName)
            throws DatabaseException {

        if (categoryName == null
                || categoryName.isBlank()) {
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
                    categoryName.trim()
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapCategory(resultSet);
                }

                return null;
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to find category by name.",
                    exception
            );
        }
    }

    @Override
    public List<Category> searchByName(String keyword)
            throws DatabaseException {

        if (keyword == null
                || keyword.isBlank()) {
            return findAll();
        }

        List<Category> categories =
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
                    categories.add(
                            mapCategory(resultSet)
                    );
                }
            }

            return categories;

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to search categories.",
                    exception
            );
        }
    }

    private Category mapCategory(
            ResultSet resultSet
    ) throws SQLException {

        Category category =
                new Category();

        category.setId(
                resultSet.getInt("category_id")
        );

        category.setCategoryName(
                resultSet.getString(
                        "category_name"
                )
        );

        category.setDescription(
                resultSet.getString(
                        "description"
                )
        );

        category.setCreatedAt(
                parseDateTime(
                        resultSet.getString(
                                "created_at"
                        )
                )
        );

        return category;
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