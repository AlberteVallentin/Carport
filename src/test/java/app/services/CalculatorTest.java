package app.services;

import app.persistence.ConnectionPool;
import app.utility.Calculator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {


    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String URL = System.getenv("JDBC_CONNECTION_STRING");
    private static final String DB = System.getenv("JDBC_DB");
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    @BeforeAll
    static void setup(){

    }
    @Test
    void calcPostQuantity() {
        Calculator calculator = new Calculator(420,420, connectionPool);

        assertEquals(4, calculator.calcPostQuantity());
    }
}