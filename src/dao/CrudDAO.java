package dao;

import java.util.List;

import exception.DatabaseException;

public interface CrudDAO<T> {

    void add(T entity) throws DatabaseException;

    void update(T entity) throws DatabaseException;

    void delete(int id) throws DatabaseException;

    T findById(int id) throws DatabaseException;

    List<T> findAll() throws DatabaseException;
}