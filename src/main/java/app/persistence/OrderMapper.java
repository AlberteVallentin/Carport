package app.persistence;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.utility.Calculator;
import app.utility.ShippingCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderMapper {

    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException{
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders inner join users using(user_id)";
        try(
                Connection connection = connectionPool.getConnection();
                var prepareStatement = connection.prepareStatement(sql);
                var resultSet = prepareStatement.executeQuery();
                )
            {
            while(resultSet.next()){

                //Retreiving user attributes from DB
                int userId = resultSet.getInt("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phoneNumber = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                boolean isAdmin = resultSet.getBoolean("admin");
                int addressId = resultSet.getInt("address_id");

                //Retreiving order attributes from DB
                int orderId = resultSet.getInt("order_id");
                double price = resultSet.getDouble("price");
                String comment = resultSet.getString("comment");
                int shippingId = resultSet.getInt("shipping_id");
                int cpLength = resultSet.getInt("cp_length");
                int cpWidth = resultSet.getInt("cp_width");
                String cpRoof = resultSet.getString("cp_roof");
                int shedLength = resultSet.getInt("shed_length");
                int shedWidth = resultSet.getInt("shed_width");
                int statusId = resultSet.getInt("status_id");



                User user = new User(userId, firstName, lastName, phoneNumber, email, password, isAdmin, addressId);
                Shipping shipping = ShippingMapper.getShippingById(shippingId, connectionPool);
                Order order = new Order(orderId, price, user, comment, shipping, cpLength, cpWidth, cpRoof, shedLength, shedWidth, statusId);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderList;
    }

    public static List<BillOfMaterialLine> getOrderByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException
    {
        List<BillOfMaterialLine> BillOfMaterialLineList = new ArrayList<>();
        String sql = "SELECT * FROM bill_of_materials_view where order_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(sql);
        )
        {
            prepareStatement.setInt(1, orderId);
            var rs = prepareStatement.executeQuery();
            while (rs.next())
            {
                // Order
                int orderId2 = rs.getInt("order_id");
                Order order = getOrderById(orderId2, connectionPool);

                //Material
                int materialId = rs.getInt("material_id");
                String type = rs.getString("type");
                String unit = rs.getString("unit");
                int materialPrice = rs.getInt("material_price");

                Material material = new Material(materialId, null, null, type, materialPrice, unit, null);

                // material variant
                int materialVariantId = rs.getInt("material_variant_id");
                int length = rs.getInt("length");

                MaterialVariant materialVariant = new MaterialVariant(materialVariantId, length, material);

                // BillOfMaterialLine
                int BillOfMaterialLineId = rs.getInt("bom_line_id");
                int functionalDescriptionId = rs.getInt("functional_description_id");
                String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(functionalDescriptionId, connectionPool);
                int quantity = rs.getInt("quantity");
                BillOfMaterialLine BillOfMaterialLine = new BillOfMaterialLine(BillOfMaterialLineId, order, materialVariant, quantity, functionalDescription);
                BillOfMaterialLineList.add(BillOfMaterialLine);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not get users from the database", e.getMessage());
        }
        return BillOfMaterialLineList;
    }

    public static Order insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO orders (cp_width, cp_length, status_id, user_id, price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
            {
                ps.setDouble(2, order.getPrice());
                ps.setInt(3, order.getUser().getUserId());
                ps.setString(4, order.getComment());
                ps.setInt(5, order.getShippingId());
                ps.setInt(6, order.getCpLength());
                ps.setInt(7, order.getCpWidth());
                ps.setInt(8, order.getShLength());
                ps.setInt(9,order.getShWidth());
                ps.setInt(10, order.getStatusId());
                ps.setString(11, order.getCpRoof());

                ps.executeUpdate();
                ResultSet keySet = ps.getGeneratedKeys();
                if (keySet.next())
                {
                    Shipping shipping = ShippingMapper.getShippingById(order.getShipping().getShippingId(), connectionPool);
                    Order newOrder = new Order(keySet.getInt(1), order.getPrice(), order.getUser(), order.getComment(), shipping, order.getCpLength(), order.getCpWidth(), order.getCpRoof(), order.getShLength(), order.getShWidth(), order.getStatusId());
                    return newOrder;
                } else
                    return null;
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not create order in the database", e.getMessage());
        }

    }

    public static void createBomLine(List<BillOfMaterialLine> billOfMaterialLines, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO bill_of_material_line (order_id, material_variant_id, quanity ,functional_description_id) " +
                "VALUES ( ?, ?, ?,?)";
        try (Connection connection = connectionPool.getConnection())
        {
            for (BillOfMaterialLine billOfMaterialLine : billOfMaterialLines)
            {
                try (PreparedStatement ps = connection.prepareStatement(sql))
                {
                    ps.setInt(1, billOfMaterialLine.getOrder().getOrderId());
                    ps.setInt(2, billOfMaterialLine.getMaterialVariant().getMaterialVariantId());
                    ps.setInt(3, billOfMaterialLine.getQuantity());
                    ps.setInt(4, billOfMaterialLine.getFunctionalDescriptionId());
                    ps.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not create orderitem in the database", e.getMessage());
        }
    }


    public static void createOrder(Order order, User user, int shippingId, ConnectionPool connectionPool) throws SQLException {
        String sql = "INSERT INTO orders (price, user_id, comment, shipping_id, cp_length, cp_width, shed_length, shed_width, status_id, cp_roof) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            Calculator calculator = new Calculator(order.getCpWidth(),order.getCpLength(),connectionPool);
            calculator.calcCarport(order);
            ps.setDouble(1, calculator.getTotalMaterialPrice());
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

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int orderId = rs.getInt(1);

            order.setOrderId(orderId);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
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

    public static Order getOrderByIdAndUserId(int orderId, int userId, ConnectionPool connectionPool) throws DatabaseException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ? AND user_id = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

    public static void deleteOrder (int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting order", e.getMessage());
        }
    }

    public static double updatePriceByOrderId(int orderId, double price, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET price = ? WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, price);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating price by order ID", e.getMessage());
        }
        return price;
    }

    public static void deleteBillOfMaterialLinesByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM bill_of_material_line WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting bill of material lines", e.getMessage());
        }
    }


}