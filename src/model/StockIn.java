package model;

public class StockIn extends StockMovement {

    @Override
    public String getMovementType() {
        return "IN";
    }

    @Override
    public int calculateNewQuantity(int currentQuantity) {
        return currentQuantity + getQuantity();
    }
}