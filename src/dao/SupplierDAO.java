package dao;

import java.util.List;

import exception.DatabaseException;
import model.Supplier;

public interface SupplierDAO extends CrudDAO<Supplier> {

    Supplier findByName(String supplierName)
            throws DatabaseException;

    List<Supplier> searchByName(String keyword)
            throws DatabaseException;
}