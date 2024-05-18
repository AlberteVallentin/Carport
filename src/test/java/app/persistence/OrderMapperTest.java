package app.persistence;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {
    // Integrationstest for OrderMapper

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * Sets up the database schema and test data before all tests run.
     */
    @BeforeAll
    static void setupClass() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Drop existing test tables and sequences
                stmt.execute("DROP TABLE IF EXISTS test.users");
                stmt.execute("DROP TABLE IF EXISTS test.orders");
                stmt.execute("DROP TABLE IF EXISTS test.shipping");
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_user_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.shipping_shipping_id_seq CASCADE;");

                // Create test tables as a copy of the original schema without data
                stmt.execute("CREATE TABLE test.users AS (SELECT * from public.users) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                stmt.execute("CREATE TABLE test.shipping AS (SELECT * from public.shipping) WITH NO DATA");

                // Create sequences for auto-generating IDs
                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");
                stmt.execute("CREATE SEQUENCE test.shipping_shipping_id_seq");
                stmt.execute("ALTER TABLE test.shipping ALTER COLUMN shipping_id SET DEFAULT nextval('test.shipping_shipping_id_seq');");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    /**
     * Sets up the test data before each test.
     */
    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Clear all rows from the test tables
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.users");
                stmt.execute("DELETE FROM test.shipping");

                // Insert test data
                stmt.execute("INSERT INTO test.users (user_id, first_name, last_name, email, admin, address_id, password, phone_number) " +
                        "VALUES (1, 'John', 'Doe', 'john@doe.com', true, 1, '12', '123456789'), (2, 'Jane', 'Doe', 'jD8XrP@example.com', true, 2, 'password', '123456789')");

                stmt.execute("INSERT INTO test.orders (order_id, price, user_id, comment, shipping_id, cp_length, cp_width , shed_length, shed_width, status_id, cp_roof) " +
                        "VALUES (1, 1000, 1, 'En god carport', 1, 480, 480, 200, 210, 1, 'Uden tag'), (2, 2000, 2, 'Test', 2, 600, 780, 0, 0, 2, 'Med plasttrapeztag')");

                stmt.execute("INSERT INTO test.shipping (shipping_id, address_id, shipping_rate) VALUES (1, 1, 100), (2, 2, 200)");

                // Set sequence to continue from the largest ID
                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id) + 1 FROM test.orders), 1), false)");
                stmt.execute("SELECT setval('test.users_user_id_seq', COALESCE((SELECT MAX(user_id) + 1 FROM test.users), 1), false)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    /**
     * Tests the getAllOrders method.
     */
    @Test
    void getAllOrders() {
        try {
            int expected = 2;
            List<Order> actualOrders = OrderMapper.getAllOrders(connectionPool);
            assertEquals(expected, actualOrders.size());
        } catch (DatabaseException e) {
            fail("Database error: " + e.getMessage());
        }
    }

    /**
     * Tests the getOrderById method.
     */
    @Test
    void getOrderById() {
        try {
            User user = new User(1, "John", "Doe", "123456789", "john@doe.com", null, true, 1);
            Order expected = new Order(1, 1000, user, 480, 480, "Uden tag", 200, 210, 1);
            expected.setComment(null);
            Order actualOrder = OrderMapper.getOrderById(1, connectionPool);
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actualOrder);
            assertEquals(expected, actualOrder);
        } catch (DatabaseException e) {
            fail("Database error: " + e.getMessage());
        }
    }

    /**
     * Tests the createOrder method.
     */
    @Test
    void createOrder() {
        try {
            // Create a User object
            User user = new User(1, "John", "Doe", "123456789", "john@doe.com", null, true, 1);

            // Create an Order object
            Order expected = new Order(1000.00, user, null, 1, 480, 480, 200, 210, 1, "Uden tag");

            // Insert the order
            OrderMapper.createOrder(expected, user, 1, 1000.00, connectionPool);

            // Retrieve the inserted order from the database
            Order actual = OrderMapper.getOrderById(expected.getOrderId(), connectionPool);

            // Compare the expected and actual Order objects
            assertEquals(expected, actual);
        } catch (DatabaseException | SQLException e) {
            fail("Database error: " + e.getMessage());
        }
    }
}
