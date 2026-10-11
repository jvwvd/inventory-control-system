package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import exception.DatabaseException;
import model.InventoryUser;
import util.DatabaseConnection;

public class JdbcUserDAO implements UserDAO {

    private static final String FIND_BY_USERNAME_SQL =
            "SELECT user_id, username, password_hash, "
                    + "full_name, is_active, created_at "
                    + "FROM users "
                    + "WHERE username = ? "
                    + "LIMIT 1";

    @Override
    public InventoryUser findByUsername(String username)
            throws DatabaseException {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_USERNAME_SQL
                        )
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }

                return null;
            }

        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Failed to find user by username.",
                    exception
            );
        }
    }

    private InventoryUser mapUser(
            ResultSet resultSet
    ) throws SQLException {

        InventoryUser user =
                new InventoryUser();

        user.setId(
                resultSet.getInt("user_id")
        );

        user.setUsername(
                resultSet.getString("username")
        );

        user.setPasswordHash(
                resultSet.getString("password_hash")
        );

        user.setFullName(
                resultSet.getString("full_name")
        );

        user.setActive(
                resultSet.getInt("is_active") == 1
        );

        String createdAtText =
                resultSet.getString("created_at");

        if (createdAtText != null
                && !createdAtText.isBlank()) {

            user.setCreatedAt(
                    LocalDateTime.parse(
                            createdAtText.replace(
                                    " ",
                                    "T"
                            )
                    )
            );
        }

        return user;
    }
}