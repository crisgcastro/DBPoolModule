import dbpool.dbmodule.DBCPBuilder;
import dbpool.dbmodule.DBConnectionPool;
import dbpool.dbmodule.DBPoolConfiguration;
import dbpool.utils.DBPoolException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit test for DBConnectionPool class .
 * @author cgcastro
 * @version 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DBConnectionPoolTest {


    @Test
    public void returnRealConnection() throws SQLException {

        DBConnectionPool dbConnectionPoolMock = Mockito.mock(
            DBConnectionPool.class);
        Connection connection = Mockito.mock(Connection.class);
        when(dbConnectionPoolMock.getConnection()).thenReturn(connection);

        assertEquals(connection, dbConnectionPoolMock.getConnection());

    }


    @Test
    public void throwExceptionAfterReferencingClosedConnection() throws
            SQLException, DBPoolException {

        DBPoolConfiguration dbPoolConfigurationMock =
            Mockito.mock(DBPoolConfiguration.class);

        dbPoolConfigurationMock.setDbusername("admin");
        dbPoolConfigurationMock.setDbpassword("admin");
        dbPoolConfigurationMock.setDbDriver("//mysql");
        dbPoolConfigurationMock.setDburl("//mysql:305");
        dbPoolConfigurationMock.setMinPoolCache(5);
        dbPoolConfigurationMock.setMaxPoolSize(30);

        DBCPBuilder dbcpBuilderMock = Mockito.mock(DBCPBuilder.class);
        dbcpBuilderMock.setConfiguration(dbPoolConfigurationMock);

        DBConnectionPool dbConnectionPool = dbcpBuilderMock.build();

        Connection connection = dbConnectionPool.getConnection();
        connection.close();

        // after closing connection should throw an exception if there are
        // more references to connection object

        when(connection.createStatement()).thenThrow(
            new SQLException());

        try {
            connection.createStatement();
            fail("Connection is still live");
        } catch (SQLException exception) {
            // success
        }

    }





}
