package model;

public class ElectronicProduct extends Product {

    private int warrantyMonths;

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getProductType() {
        return "ELECTRONIC";
    }

    @Override
    public String getSpecialInformation() {
        if (warrantyMonths <= 0) {
            return "Warranty: Not specified";
        }

        if (warrantyMonths == 1) {
            return "Warranty: 1 month";
        }

        return "Warranty: " + warrantyMonths + " months";
    }
}