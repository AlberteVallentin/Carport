package app.persistence;

import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserMapperTest {

    private ConnectionPool connectionPool;
    private Connection connection;
    private PreparedStatement preparedStatement;

    @BeforeEach
    public void setUp() throws SQLException {
        // Arrange
        // Mocking the ConnectionPool, Connection and PreparedStatement
        connectionPool = Mockito.mock(ConnectionPool.class);
        connection = Mockito.mock(Connection.class);
        preparedStatement = Mockito.mock(PreparedStatement.class);

        // Setting up the mock objects to return specific values when their methods are called
        when(connectionPool.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    public void testCreateUserSuccess() throws SQLException, DatabaseException {
        // Arrange
        // Setting up the mock PreparedStatement to return 1 when executeUpdate() is called
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        // Calling the method to be tested
        UserMapper.createUser("John", "Doe", "john.doe@example.com", "1234567890", "password", 1, connectionPool);

        // Assert
        // Verifying that executeUpdate() was called once on the mock PreparedStatement
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testCreateUserDuplicateEmail() throws SQLException {
        // Arrange
        // Setting up the mock PreparedStatement to throw an exception when executeUpdate() is called
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("ERROR: duplicate key value violates unique constraint"));

        // Act and Assert
        // Asserting that a DatabaseException is thrown when createUser() is called
        // and verifying that the exception message is as expected
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            UserMapper.createUser("John", "Doe", "john.doe@example.com", "1234567890", "password", 1, connectionPool);
        });

        assertEquals("E-mailen findes allerede. Vælg en anden e-mail eller log ind", exception.getMessage());
    }

    @Test
    public void testCreateUserDatabaseError() throws SQLException {
        // Arrange
        // Setting up the mock PreparedStatement to throw an exception when executeUpdate() is called
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Some other SQL exception"));

        // Act and Assert
        // Asserting that a DatabaseException is thrown when createUser() is called
        // and verifying that the exception message is as expected
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            UserMapper.createUser("John", "Doe", "john.doe@example.com", "1234567890", "password", 1, connectionPool);
        });

        assertEquals("Der er sket en fejl. Prøv igen", exception.getMessage());
    }
}