package dbpool.utils;

import dbpool.utils.Constants.DBCPMessageException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains the methods for validating a Connection .
 * @author cgcastro
 * @version 1.0
 */
public class DBConnectionValidator {

    /**
    * String query .
    */
    private String query;

    /**
     * Constructor which sets query to test .
     * @param query the query .
     * */
    public DBConnectionValidator(final String query)  {

        if (!query.isEmpty()){
            this.query = query;
        }

    }

    /**
     * Constructor which sets default query;
     * */
    public DBConnectionValidator()  {

        this.query = "SELECT 1";

    }


    /**
     * Check if connection is still valid .
     * @throws SQLException if is not valid connection .
     * @param connection the connection for validation.
     * @return the configuration status.
     * */
    public boolean isValidConnection(final Connection connection)
        throws SQLException {

        boolean resultSetValue;

        // if query is null or empty throw an exception
        if ((query == null) || (query.isEmpty()))
            throw new SQLException(DBCPMessageException.MSG_EMPTY_QUERY);

        // try with resources connection
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (!resultSet.next()){
                resultSetValue = false;
            } else {
                resultSetValue = true;
            }

            return resultSetValue;
        }
    }

}
