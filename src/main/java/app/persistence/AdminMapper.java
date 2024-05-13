package app.persistence;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminMapper
{



    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM orders inner join status using(status_id) order by status_id";
        List<Order> orderList = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int userId = rs.getInt("user_id");
                User user = UserMapper.getUserById(userId, connectionPool);
                if (user == null) {
                    throw new DatabaseException("No user found with the provided userId: " + userId);
                }
                int orderId = rs.getInt("order_id");
                int cpLength = rs.getInt("cp_length");
                int cpWidth = rs.getInt("cp_width");
                int shLength = rs.getInt("shed_length");
                int shWidth = rs.getInt("shed_width");
                int statusId = rs.getInt("status_id");
                String cpRoof = rs.getString("cp_roof");
                double price = rs.getDouble("price");
                Order order = new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId);
                orderList.add(order);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl i getAllOrders");
        }
        return orderList;
    }
}
