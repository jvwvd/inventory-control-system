package model;

public class GeneralProduct extends Product {

    @Override
    public String getProductType() {
        return "GENERAL";
    }

    @Override
    public String getSpecialInformation() {
        return "General product";
    }
}