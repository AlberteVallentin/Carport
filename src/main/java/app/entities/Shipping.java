package app.entities;

public class Shipping {
    private int shippingId;
    private int adressId;
    private double shippingRate;

    public Shipping(int shippingId, int adressId, double shippingRate) {
        this.shippingId = shippingId;
        this.adressId = adressId;
        this.shippingRate = shippingRate;
    }

    public void setShippingRate(double shippingRate) {
        this.shippingRate = shippingRate;
    }

    public int getShippingId() {
        return shippingId;
    }

    public int getAdressId() {
        return adressId;
    }

    public double getShippingRate() {
        return shippingRate;
    }
}
