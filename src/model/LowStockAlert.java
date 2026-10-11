package model;

public class LowStockAlert extends Alert {

    @Override
    public boolean shouldTrigger(Product product) {
        return product != null && product.isLowStock();
    }

    @Override
    public String getMessage(Product product) {
        if (product == null) {
            return "Low stock alert: Product information is not available.";
        }

        return "Low stock alert: "
                + product.getProductName()
                + " has only "
                + product.getQuantity()
                + " units remaining. Reorder level: "
                + product.getReorderLevel()
                + ".";
    }
}