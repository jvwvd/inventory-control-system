package model;

public abstract class Alert {

    public abstract boolean shouldTrigger(Product product);

    public abstract String getMessage(Product product);
}