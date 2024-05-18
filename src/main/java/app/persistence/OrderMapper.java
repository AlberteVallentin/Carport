package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    /**
     * Retrieves all orders from the database.
     *
     * @param connectionPool The connection pool for database connections.
     * @return A list of all orders.
     * @throws DatabaseException If a database error occurs.
     */
    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders INNER JOIN users USING(user_id)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Retrieving user attributes from DB
                int userId = resultSet.getInt("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phoneNumber = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                boolean isAdmin = resultSet.getBoolean("admin");
                int addressId = resultSet.getInt("address_id");

                // Retrieving order attributes from DB
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

    /**
     * Retrieves a list of BillOfMaterialLine objects by order ID.
     *
     * @param orderId        The ID of the order.
     * @param connectionPool The connection pool for database connections.
     * @return A list of BillOfMaterialLine objects.
     * @throws DatabaseException If a database error occurs.
     */
    public static List<BillOfMaterialLine> getOrderByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        List<BillOfMaterialLine> BillOfMaterialLineList = new ArrayList<>();
        String sql = "SELECT * FROM bill_of_materials_view WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {

            prepareStatement.setInt(1, orderId);
            ResultSet rs = prepareStatement.executeQuery();

            while (rs.next()) {
                // Order
                int orderId2 = rs.getInt("order_id");
                Order order = getOrderById(orderId2, connectionPool);

                // Material
                int materialId = rs.getInt("material_id");
                String type = rs.getString("type");
                String unit = rs.getString("unit");
                int materialPrice = rs.getInt("material_price");

                Material material = new Material(materialId, null, null, type, materialPrice, unit, null);

                // Material variant
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
        } catch (SQLException e) {
            throw new DatabaseException("Could not get users from the database", e.getMessage());
        }
        return BillOfMaterialLineList;
    }

    /**
     * Inserts a new order into the database.
     *
     * @param order          The order to insert.
     * @param connectionPool The connection pool for database connections.
     * @return The newly inserted order with the generated ID.
     * @throws DatabaseException If a database error occurs.
     */
    public static Order insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (cp_width, cp_length, status_id, user_id, price) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getCpWidth());
            ps.setInt(2, order.getCpLength());
            ps.setInt(3, order.getStatusId());
            ps.setInt(4, order.getUser().getUserId());
            ps.setDouble(5, order.getPrice());

            ps.executeUpdate();
            ResultSet keySet = ps.getGeneratedKeys();

            if (keySet.next()) {
                int generatedId = keySet.getInt(1);
                order.setOrderId(generatedId);
                return order;
            } else {
                throw new DatabaseException("Order creation failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not create order in the database", e.getMessage());
        }
    }

    /**
     * Creates BillOfMaterialLine records in the database for a given list of BillOfMaterialLine objects.
     *
     * @param billOfMaterialLines The list of BillOfMaterialLine objects to insert.
     * @param connectionPool      The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    public static void createBomLine(List<BillOfMaterialLine> billOfMaterialLines, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO bill_of_material_line (order_id, material_variant_id, quanity, functional_description_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection()) {
            for (BillOfMaterialLine billOfMaterialLine : billOfMaterialLines) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, billOfMaterialLine.getOrder().getOrderId());
                    ps.setInt(2, billOfMaterialLine.getMaterialVariant().getMaterialVariantId());
                    ps.setInt(3, billOfMaterialLine.getQuantity());
                    ps.setInt(4, billOfMaterialLine.getFunctionalDescriptionId());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not create bill of material lines in the database", e.getMessage());
        }
    }

    /**
     * Creates a new order in the database.
     *
     * @param order          The order to create.
     * @param user           The user who created the order.
     * @param shippingId     The shipping ID associated with the order.
     * @param totalPrice     The total price of the order.
     * @param connectionPool The connection pool for database connections.
     * @throws SQLException If a SQL error occurs.
     */
    public static void createOrder(Order order, User user, int shippingId, double totalPrice, ConnectionPool connectionPool) throws SQLException {
        String sql = "INSERT INTO orders (price, user_id, comment, shipping_id, cp_length, cp_width, shed_length, shed_width, status_id, cp_roof) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, totalPrice);
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
            if (rs.next()) {
                int orderId = rs.getInt(1);
                order.setOrderId(orderId);
            }
        }
    }

    /**
     * Retrieves the last inserted order ID from the database.
     *
     * @param connectionPool The connection pool for database connections.
     * @return The last inserted order ID.
     * @throws DatabaseException If a database error occurs.
     */
    public static int getLastOrder(ConnectionPool connectionPool) throws DatabaseException {
        int orderId = 0;
        String sql = "SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                orderId = rs.getInt("order_id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving the latest order ID", e.getMessage());
        }
        return orderId;
    }

    /**
     * Retrieves the status ID of an order by its order ID.
     *
     * @param orderId        The ID of the order.
     * @param connectionPool The connection pool for database connections.
     * @return The status ID of the order.
     * @throws DatabaseException If a database error occurs.
     */
    public static int getOrderStatusByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        int statusId = 0;
        String sql = "SELECT status_id FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                statusId = rs.getInt("status_id");
            } else {
                throw new DatabaseException("Vi kunne desværre ikke finde en ordre med følgende ordrenummer: " + orderId + ". Prøv igen og tjek evt din e-mail, hvor du kan se dit ordrenummer.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order status by ID", e.getMessage());
        }
        return statusId;
    }

    /**
     * Updates the status ID of an order by its order ID.
     *
     * @param orderId        The ID of the order to update.
     * @param statusId       The new status ID of the order.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
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

    /**
     * Retrieves an order by its ID from the database.
     *
     * @param orderId        The ID of the order to retrieve.
     * @param connectionPool The connection pool for database connections.
     * @return The Order object corresponding to the given ID.
     * @throws DatabaseException If a database error occurs.
     */
    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

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

                // Create and return Order object
                order = new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order by ID", e.getMessage());
        }
        return order;
    }

    /**
     * Retrieves an order by its ID and user ID from the database.
     *
     * @param orderId        The ID of the order to retrieve.
     * @param userId         The ID of the user associated with the order.
     * @param connectionPool The connection pool for database connections.
     * @return The Order object corresponding to the given IDs.
     * @throws DatabaseException If a database error occurs.
     */
    public static Order getOrderByIdAndUserId(int orderId, int userId, ConnectionPool connectionPool) throws DatabaseException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ? AND user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

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

                // Create and return Order object
                order = new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order by ID", e.getMessage());
        }
        return order;
    }

    /**
     * Deletes an order by its ID from the database.
     *
     * @param orderId        The ID of the order to delete.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    public static void deleteOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting order", e.getMessage());
        }
    }

    /**
     * Updates the price of an order by its order ID.
     *
     * @param orderId        The ID of the order to update.
     * @param price          The new price of the order.
     * @param connectionPool The connection pool for database connections.
     * @return The updated price.
     * @throws DatabaseException If a database error occurs.
     */
    public static double updatePriceByOrderId(int orderId, double price, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET price = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating price by order ID", e.getMessage());
        }
        return price;
    }

    /**
     * Deletes all bill of material lines associated with an order ID from the database.
     *
     * @param orderId        The ID of the order.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    public static void deleteBillOfMaterialLinesByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM bill_of_material_line WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting bill of material lines", e.getMessage());
        }
    }

    /**
     * Retrieves all bill of material lines associated with an order ID from the database.
     *
     * @param orderId        The ID of the order.
     * @param connectionPool The connection pool for database connections.
     * @return A list of BillOfMaterialLine objects.
     * @throws DatabaseException If a database error occurs.
     */
    public static List<BillOfMaterialLine> getBomLinesByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        List<BillOfMaterialLine> bomLines = new ArrayList<>();
        String sql = "SELECT * FROM bill_of_material_line WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bomLineId = rs.getInt("bom_line_id");
                int materialVariantId = rs.getInt("material_variant_id");
                int quantity = rs.getInt("quantity");
                int functionalDescriptionId = rs.getInt("functional_description_id");

                // Retrieve Order and MaterialVariant objects based on their IDs
                Order order = OrderMapper.getOrderById(orderId, connectionPool);
                MaterialVariant materialVariant = MaterialVariantMapper.getMaterialVariantById(materialVariantId, connectionPool);

                // Retrieve the functional description based on its ID
                String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(functionalDescriptionId, connectionPool);

                // Create a new BillOfMaterialLine object and add it to the list
                BillOfMaterialLine bomLine = new BillOfMaterialLine(order, materialVariant, quantity, functionalDescription);
                bomLines.add(bomLine);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving bill of material lines by order ID", e.getMessage());
        }
        return bomLines;
    }
}
