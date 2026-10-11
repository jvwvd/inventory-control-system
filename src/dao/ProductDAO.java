package dao;

import java.util.List;

import exception.DatabaseException;
import model.Product;

public interface ProductDAO extends CrudDAO<Product> {

    Product findBySku(String sku)
            throws DatabaseException;

    List<Product> search(String keyword)
            throws DatabaseException;

    List<Product> findLowStockProducts()
            throws DatabaseException;
}