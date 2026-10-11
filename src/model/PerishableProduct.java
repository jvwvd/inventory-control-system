package model;

import java.time.LocalDate;

public class PerishableProduct extends Product {

    private LocalDate expiryDate;

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String getProductType() {
        return "PERISHABLE";
    }

    @Override
    public String getSpecialInformation() {
        if (expiryDate == null) {
            return "Expiry date: Not specified";
        }

        return "Expiry date: " + expiryDate;
    }
}