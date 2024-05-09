package app.persistence;

import app.entities.Address;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper {

    public static int createAddress(String streetName, String houseNumber, String floorAndDoorDescription, int postalCode, String city, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "INSERT INTO address (street_name, house_number, floor_and_door_description, postal_code, city) VALUES (?, ?, ?, ?, ?) RETURNING address_id";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, streetName);
            ps.setString(2, houseNumber);
            ps.setString(3, floorAndDoorDescription);
            ps.setInt(4, postalCode);
            ps.setString(5, city);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Returnerer den genererede adresse ID
            } else {
                throw new DatabaseException("Fejl ved oprettelse af adresse - ingen ID returneret");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Der er sket en fejl ved indsættelse af adresse", e.getMessage());
        }
    }

    public static Address getAddressById(int addressId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "SELECT * FROM address WHERE address_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String streetName = rs.getString("street_name");
                String houseNumber = rs.getString("house_number");
                String floorAndDoorDescription = rs.getString("floor_and_door_description");
                int postalCode = rs.getInt("postal_code");
                String city = rs.getString("city");
                return new Address(addressId, streetName, houseNumber, floorAndDoorDescription, postalCode, city);
            } else {
                throw new DatabaseException("Ingen adresse fundet med ID: " + addressId);
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Der er sket en fejl ved hentning af adresse", e.getMessage());
        }
    }
}
