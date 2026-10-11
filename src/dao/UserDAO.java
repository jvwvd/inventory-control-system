package dao;

import exception.DatabaseException;
import model.InventoryUser;

public interface UserDAO {

    InventoryUser findByUsername(String username)
            throws DatabaseException;
}