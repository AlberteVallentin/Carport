package app.persistence;

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
}
