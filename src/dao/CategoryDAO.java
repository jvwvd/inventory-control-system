package dao;

import java.util.List;

import exception.DatabaseException;
import model.Category;

public interface CategoryDAO extends CrudDAO<Category> {

    Category findByName(String categoryName)
            throws DatabaseException;

    List<Category> searchByName(String keyword)
            throws DatabaseException;
}