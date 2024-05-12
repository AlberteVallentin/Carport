package app.persistence;

import app.entities.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderMapper {

    public static void createOrder (Order order, ConnectionPool connectionPool) throws SQLException {
        String sql = "INSERT INTO orders(price, user_id, comment, shipping_id, cp_length, cp_width, shed_length, shed_width, status_id, cp_roof) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, order.getPrice());
            ps.setInt(2, order.getUserId());
            ps.setString(3, order.getComment());
            ps.setInt(4, order.getShipping().getShippingId());
            ps.setInt(5, order.getCpLength());
            ps.setInt(6, order.getCpWidth());
            ps.setInt(7, order.getShLength());
            ps.setInt(8, order.getShWidth());
            ps.setInt(9, order.getStatus().getStatusId());
            ps.setString(10, order.getCpRoof());

            ps.executeUpdate();
        }
    }
}