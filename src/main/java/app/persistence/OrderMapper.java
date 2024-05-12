package app.persistence;

import app.entities.Order;
import app.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderMapper {

    public static void createOrder (Order order, User user, int shippingId, ConnectionPool connectionPool) throws SQLException {
        String sql = "INSERT INTO orders(price, user_id, comment, shipping_id, cp_length, cp_width, shed_length, shed_width, status_id, cp_roof) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, 100.00);
            ps.setInt(2, user.getUserId());
            ps.setString(3, order.getComment());
            ps.setInt(4, shippingId);
            ps.setInt(5, order.getCpLength());
            ps.setInt(6, order.getCpWidth());
            ps.setInt(7, order.getShLength());
            ps.setInt(8, order.getShWidth());
            ps.setInt(9, 1);
            ps.setString(10, order.getCpRoof());
            ps.executeUpdate();
        }
    }
}