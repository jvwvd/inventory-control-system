package dao;

import java.util.List;

import exception.DatabaseException;
import model.StockMovement;

public interface StockMovementDAO {

    void add(StockMovement stockMovement)
            throws DatabaseException;

    List<StockMovement> findAll()
            throws DatabaseException;

    List<StockMovement> findByProductId(int productId)
            throws DatabaseException;
}