package service;

import java.util.List;

import dao.CategoryDAO;
import dao.JdbcCategoryDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.Category;
import util.ValidationUtil;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new JdbcCategoryDAO();
    }

    public CategoryService(CategoryDAO categoryDAO) {
        if (categoryDAO == null) {
            throw new IllegalArgumentException(
                    "CategoryDAO cannot be null."
            );
        }

        this.categoryDAO = categoryDAO;
    }

    public void addCategory(Category category)
            throws ValidationException,
            DatabaseException {

        validateCategory(category);

        String categoryName =
                category.getCategoryName().trim();

        Category existingCategory =
                categoryDAO.findByName(categoryName);

        if (existingCategory != null) {
            throw new ValidationException(
                    "Category name already exists."
            );
        }

        category.setCategoryName(categoryName);
        category.setDescription(
                cleanOptionalText(
                        category.getDescription()
                )
        );

        categoryDAO.add(category);
    }

    public void updateCategory(Category category)
            throws ValidationException,
            DatabaseException {

        validateCategory(category);

        ValidationUtil.requirePositive(
                category.getId(),
                "Category ID"
        );

        String categoryName =
                category.getCategoryName().trim();

        Category existingCategory =
                categoryDAO.findByName(categoryName);

        if (existingCategory != null
                && existingCategory.getId()
                != category.getId()) {

            throw new ValidationException(
                    "Category name already exists."
            );
        }

        category.setCategoryName(categoryName);
        category.setDescription(
                cleanOptionalText(
                        category.getDescription()
                )
        );

        categoryDAO.update(category);
    }

    public void deleteCategory(int categoryId)
            throws ValidationException,
            DatabaseException {

        ValidationUtil.requirePositive(
                categoryId,
                "Category ID"
        );

        categoryDAO.delete(categoryId);
    }

    public Category getCategoryById(int categoryId)
            throws ValidationException,
            DatabaseException {

        ValidationUtil.requirePositive(
                categoryId,
                "Category ID"
        );

        return categoryDAO.findById(categoryId);
    }

    public List<Category> getAllCategories()
            throws DatabaseException {

        return categoryDAO.findAll();
    }

    public List<Category> searchCategories(
            String keyword
    ) throws DatabaseException {

        return categoryDAO.searchByName(keyword);
    }

    private void validateCategory(Category category)
            throws ValidationException {

        ValidationUtil.requireNotNull(
                category,
                "Category"
        );

        ValidationUtil.requireNonBlank(
                category.getCategoryName(),
                "Category name"
        );
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}