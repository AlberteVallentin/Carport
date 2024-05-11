package app.persistence;

import app.entities.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderMapper {

    public static void createOrder (Order order, ConnectionPool connectionPool) throws SQLException {
        String sql = "INSERT INTO orders (user_id, order_details) VALUES (?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getUserId());


            ps.executeUpdate();
        }
    }
}