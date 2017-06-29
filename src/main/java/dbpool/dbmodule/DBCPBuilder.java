package dbpool.dbmodule;

import dbpool.utils.Constants.DBCPMessageException;
import dbpool.utils.DBPoolException;

import java.sql.SQLException;

/**
 * Builder class for creating a new DBConnectionPool object .
 * @author cgcastro
 * @version 1.0
 */
public class DBCPBuilder {

    /**
     * DBPoolConfiguration the configuration pool
     */
    private DBPoolConfiguration dbPoolConfiguration;

    /**
     * Default constructor .
     **/
    public DBCPBuilder() {}

    /**
     * Sets the Pool Configuration object .
     *  @param localPoolConfiguration sets configuration pool .
     * */
    public void setConfiguration(
        final DBPoolConfiguration localPoolConfiguration){
        if (localPoolConfiguration != null)
            dbPoolConfiguration = localPoolConfiguration;
    }

    /**
     *  Build a connection pool object .
     *  @throws SQLException sql exceptions
     *  @throws DBPoolException db pool exception
     *  @return DBConnectionPool object .
     * */
    public DBConnectionPool build() throws SQLException, DBPoolException {

        if (dbPoolConfiguration.isValid()) {
            return new DBConnectionPool(dbPoolConfiguration);
        } else {
            throw new DBPoolException(
                DBCPMessageException.MSG_INVALID_CONFIGURATION);
        }
    }
}
