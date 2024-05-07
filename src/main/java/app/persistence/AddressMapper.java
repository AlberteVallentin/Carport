package app.persistence;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper {

    public static int createAddress(String streetName, String houseNumber, String floorAndDoor, String postalCode, String city, ConnectionPool connectionPool) throws SQLException {
        String sql = "INSERT INTO addresses (street_name, house_number, floor_and_door, postal_code, city) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, streetName);
            ps.setString(2, houseNumber);
            ps.setString(3, floorAndDoor);
            ps.setString(4, postalCode);
            ps.setString(5, city);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Failed to create address");
                }
            }
        }
    }
}