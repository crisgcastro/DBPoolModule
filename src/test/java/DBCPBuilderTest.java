import dbpool.dbmodule.DBCPBuilder;
import dbpool.dbmodule.DBConnectionPool;
import dbpool.dbmodule.DBPoolConfiguration;
import dbpool.utils.DBPoolException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for DBCPBuilder class .
 * @author cgcastro
 * @version 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DBCPBuilderTest {

    private DBPoolConfiguration dbPoolConfigurationMock;

    @Test
    public void testNullConfiguration() throws SQLException,
        DBPoolException {

        DBPoolConfiguration dbPoolConfiguration = new DBPoolConfiguration();
        DBCPBuilder dbcpBuilder = new DBCPBuilder();
        dbcpBuilder.setConfiguration(dbPoolConfiguration);
        catchException(dbcpBuilder).build();

        assert caughtException() instanceof DBPoolException;
    }

    @Test
    public void testIncompleteConfiguration() throws SQLException,
            DBPoolException {

        DBPoolConfiguration dbPoolConfiguration = new DBPoolConfiguration();
        dbPoolConfiguration.setDbusername("admin");
        dbPoolConfiguration.setDbpassword("admin");
        DBCPBuilder dbcpBuilder = new DBCPBuilder();
        dbcpBuilder.setConfiguration(dbPoolConfiguration);
        catchException(dbcpBuilder).build();

        assert caughtException() instanceof DBPoolException;

    }

    @Test
    public void shouldReturnValidPool() throws SQLException, DBPoolException {

        DBPoolConfiguration dbPoolConfigurationMock = Mockito.mock(
            DBPoolConfiguration.class);

        dbPoolConfigurationMock.setDbusername(anyString());
        dbPoolConfigurationMock.setDbpassword(anyString());
        dbPoolConfigurationMock.setDbDriver(anyString());
        dbPoolConfigurationMock.setDburl(anyString());
        dbPoolConfigurationMock.setMinPoolCache(5);
        dbPoolConfigurationMock.setMaxPoolSize(30);

        DBCPBuilder dbcpBuilderMock = Mockito.mock(DBCPBuilder.class);

        DBConnectionPool dbConnectionPool = Mockito.mock(
            DBConnectionPool.class);

        dbcpBuilderMock.setConfiguration(dbPoolConfigurationMock);
        when(dbcpBuilderMock.build()).thenReturn(dbConnectionPool);

        assertEquals(dbcpBuilderMock.build(), dbConnectionPool);

    }



}
