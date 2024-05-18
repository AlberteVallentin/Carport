package app.persistence;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {
    // Integrationstest for OrderMapper

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @BeforeAll
    static void setupClass() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.users");
                stmt.execute("DROP TABLE IF EXISTS test.orders");
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_user_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test.users AS (SELECT * from public.users) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Remove all rows from all tables (first orders, then users to avoid foreign key constraint violation)
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.users");

                // Insert test data (first users, then orders to avoid foreign key constraint violation)
                stmt.execute("INSERT INTO test.users (user_id, first_name, last_name, email, admin, address_id, password, phone_number) " +
                        "VALUES (1, 'John', 'Doe', 'D8XrP@example.com', true, 1, 'password', '123456789'), (2, 'Jane', 'Doe', 'jD8XrP@example.com', true, 2, 'password', '123456789')");

                stmt.execute("INSERT INTO test.orders (order_id, price, user_id, comment, shipping_id, cp_length, cp_width , shed_length, shed_width, status_id, cp_roof) " +
                        "VALUES (1, 1000, 1, 'En god carport', 1, 480, 480, 200, 210, 1, 'Uden tag'), (2, 2000, 2, 'Test', 2, 600, 780, 0, 0, 2, 'Med plasttrapeztag')");
                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id) + 1 FROM test.orders), 1), false)");
                stmt.execute("SELECT setval('test.users_user_id_seq', COALESCE((SELECT MAX(user_id) + 1 FROM test.users), 1), false)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @Test
    void getAllOrders() {
    }

    @Test
    void getOrderByOrderId() {
    }

    @Test
    void insertOrder() {
    }
}