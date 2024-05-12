package app.persistence;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {

    public static void createOrder(Order order, User user, int shippingId, ConnectionPool connectionPool) throws SQLException {
        String sql = "INSERT INTO orders(price, user_id, comment, shipping_id, cp_length, cp_width, shed_length, shed_width, status_id, cp_roof) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

    public static int getLastOrder(ConnectionPool connectionPool) throws DatabaseException {
        int orderId = 0;
        String sql = "SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            if (rs.next()) {
                orderId = rs.getInt("order_id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving the latest order ID", e.getMessage());
        }
        return orderId;
    }

    public static int getOrderStatusByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        int statusId = 0;
        String sql = "SELECT status_id FROM orders WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    statusId = rs.getInt("status_id");
                } else {
                    throw new DatabaseException("Vi kunne desværre ikke finde en ordre med følgende ordrenummer: " + orderId + ". Prøv igen og tjek evt din e-mail, hvor du kan se dit ordrenummer.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order status by ID", e.getMessage());
        }
        return statusId;
    }

    public static void updateOrderStatusById(int orderId, int statusId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status_id = ? WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating order status", e.getMessage());
        }
    }

    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    User user = UserMapper.getUserById(userId, connectionPool);
                    if (user == null) {
                        throw new DatabaseException("No user found with the provided userId: " + userId);
                    }
                    int cpLength = rs.getInt("cp_length");
                    int cpWidth = rs.getInt("cp_width");
                    int shLength = rs.getInt("shed_length");
                    int shWidth = rs.getInt("shed_width");
                    int statusId = rs.getInt("status_id");
                    String cpRoof = rs.getString("cp_roof");
                    double price = rs.getDouble("price");
                    order = new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order by ID", e.getMessage());
        }
        return order;
    }
}