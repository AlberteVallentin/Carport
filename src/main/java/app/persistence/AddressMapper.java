package app.persistence;


import app.entities.Address;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressMapper {

    public static void createAddress(String streetName, String houseNumber, String floorAndDoor, String postalCode, String city, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "INSERT INTO address (street_name, house_number, floor_and_door, postal_code, city) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, streetName);
            ps.setString(2, houseNumber);
            ps.setString(3, floorAndDoor);
            ps.setString(4, postalCode);
            ps.setString(5, city);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af adresse");
            }
        } catch (SQLException | DatabaseException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static List<Address> getAllAddresses(ConnectionPool connectionPool) {
        String sql = "SELECT * FROM address";
        List<Address> addressList = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int addressId = rs.getInt("id");
                String streetName = rs.getString("street_name");
                String houseNumber = rs.getString("house_number");
                String floorAndDoor = rs.getString("floor_and_door");
                int postalCode = rs.getInt("postal_code");
                String city = rs.getString("city");
                Address address = new Address(addressId, streetName, houseNumber, floorAndDoor, postalCode, city);
                addressList.add(address);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return addressList;
    }
}