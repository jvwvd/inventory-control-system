package service;

import dao.JdbcUserDAO;
import dao.UserDAO;
import exception.AuthenticationException;
import exception.DatabaseException;
import model.InventoryUser;
import util.PasswordUtil;

public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new JdbcUserDAO();
    }

    public AuthenticationService(UserDAO userDAO) {
        if (userDAO == null) {
            throw new IllegalArgumentException(
                    "UserDAO cannot be null."
            );
        }

        this.userDAO = userDAO;
    }

    public InventoryUser login(
            String username,
            String password
    ) throws AuthenticationException,
            DatabaseException {

        if (username == null
                || username.isBlank()
                || password == null
                || password.isBlank()) {

            throw new AuthenticationException(
                    "Username and password are required."
            );
        }

        InventoryUser user =
                userDAO.findByUsername(
                        username.trim()
                );

        if (user == null) {
            throw new AuthenticationException(
                    "Invalid username or password."
            );
        }

        boolean passwordCorrect =
                PasswordUtil.verifyPassword(
                        password,
                        user.getPasswordHash()
                );

        if (!passwordCorrect) {
            throw new AuthenticationException(
                    "Invalid username or password."
            );
        }

        if (!user.isActive()) {
            throw new AuthenticationException(
                    "This user account is inactive."
            );
        }

        return user;
    }
}