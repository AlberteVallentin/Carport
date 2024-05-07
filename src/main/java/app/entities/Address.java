package app.entities;

public class Address {
    private int addressId;
    private String streetName;
    private String houseNumber;
    private String floorAndDoor;
    private String city;
    private int postalCode;


    public Address(int addressId, String streetName, String houseNumber, String floorAndDoor, int postalCode, String city) {
        this.addressId = addressId;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.floorAndDoor = floorAndDoor;
        this.postalCode = postalCode;
        this.city = city;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFloorAndDoor() {
        return floorAndDoor;
    }

    public void setFloorAndDoor(String floorAndDoor) {
        this.floorAndDoor = floorAndDoor;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", streetName='" + streetName + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", floorAndDoor='" + floorAndDoor + '\'' +
                ", city='" + city + '\'' +
                ", postalCode=" + postalCode +
                '}';
    }
}










