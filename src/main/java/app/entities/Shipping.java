package app.entities;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Shipping{" +
                "shippingId=" + shippingId +
                ", adressId=" + adressId +
                ", shippingRate=" + shippingRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shipping shipping)) return false;
        return shippingId == shipping.shippingId && adressId == shipping.adressId && Double.compare(shippingRate, shipping.shippingRate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shippingId, adressId, shippingRate);
    }
}
