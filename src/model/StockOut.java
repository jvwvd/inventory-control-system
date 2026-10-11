package model;

public class StockOut extends StockMovement {

    @Override
    public String getMovementType() {
        return "OUT";
    }

    @Override
    public int calculateNewQuantity(int currentQuantity) {
        return currentQuantity - getQuantity();
    }
}