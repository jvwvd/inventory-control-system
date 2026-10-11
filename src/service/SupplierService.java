package service;

import java.util.List;

import dao.JdbcSupplierDAO;
import dao.SupplierDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.Supplier;
import util.ValidationUtil;

public class SupplierService {

    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new JdbcSupplierDAO();
    }

    public SupplierService(SupplierDAO supplierDAO) {
        if (supplierDAO == null) {
            throw new IllegalArgumentException(
                    "SupplierDAO cannot be null."
            );
        }

        this.supplierDAO = supplierDAO;
    }

    public void addSupplier(Supplier supplier)
            throws ValidationException,
            DatabaseException {

        validateSupplier(supplier);

        String supplierName =
                supplier.getSupplierName().trim();

        Supplier existingSupplier =
                supplierDAO.findByName(supplierName);

        if (existingSupplier != null) {
            throw new ValidationException(
                    "Supplier name already exists."
            );
        }

        cleanSupplierValues(supplier);
        supplierDAO.add(supplier);
    }

    public void updateSupplier(Supplier supplier)
            throws ValidationException,
            DatabaseException {

        validateSupplier(supplier);

        ValidationUtil.requirePositive(
                supplier.getId(),
                "Supplier ID"
        );

        String supplierName =
                supplier.getSupplierName().trim();

        Supplier existingSupplier =
                supplierDAO.findByName(supplierName);

        if (existingSupplier != null
                && existingSupplier.getId()
                != supplier.getId()) {

            throw new ValidationException(
                    "Supplier name already exists."
            );
        }

        cleanSupplierValues(supplier);
        supplierDAO.update(supplier);
    }

    public void deleteSupplier(int supplierId)
            throws ValidationException,
            DatabaseException {

        ValidationUtil.requirePositive(
                supplierId,
                "Supplier ID"
        );

        supplierDAO.delete(supplierId);
    }

    public Supplier getSupplierById(int supplierId)
            throws ValidationException,
            DatabaseException {

        ValidationUtil.requirePositive(
                supplierId,
                "Supplier ID"
        );

        return supplierDAO.findById(supplierId);
    }

    public List<Supplier> getAllSuppliers()
            throws DatabaseException {

        return supplierDAO.findAll();
    }

    public List<Supplier> searchSuppliers(
            String keyword
    ) throws DatabaseException {

        return supplierDAO.searchByName(keyword);
    }

    private void validateSupplier(Supplier supplier)
            throws ValidationException {

        ValidationUtil.requireNotNull(
                supplier,
                "Supplier"
        );

        ValidationUtil.requireNonBlank(
                supplier.getSupplierName(),
                "Supplier name"
        );

        ValidationUtil.validateOptionalEmail(
                supplier.getEmail()
        );

        ValidationUtil.validateOptionalPhone(
                supplier.getPhone()
        );
    }

    private void cleanSupplierValues(
            Supplier supplier
    ) {
        supplier.setSupplierName(
                supplier.getSupplierName().trim()
        );

        supplier.setContactPerson(
                cleanOptionalText(
                        supplier.getContactPerson()
                )
        );

        supplier.setPhone(
                cleanOptionalText(
                        supplier.getPhone()
                )
        );

        supplier.setEmail(
                cleanOptionalText(
                        supplier.getEmail()
                )
        );

        supplier.setAddress(
                cleanOptionalText(
                        supplier.getAddress()
                )
        );
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}