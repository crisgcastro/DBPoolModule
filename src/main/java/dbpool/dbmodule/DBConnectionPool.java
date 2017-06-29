package dbpool.dbmodule;

import dbpool.utils.Constants.DBCPStatus;
import dbpool.utils.DBConnectionValidator;
import dbpool.utils.Constants.DBCPMessageException;
import dbpool.utils.DBPoolException;

import java.sql.Connection;
import java.sql.Blob;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.DatabaseMetaData;
import java.sql.SQLWarning;
import java.sql.SQLClientInfoException;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Array;
import java.sql.Struct;
import java.sql.SQLXML;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class is for creating and managing wrapped pool connections .
 * @author cgcastro
 * @version 1.0
 */
public class DBConnectionPool  {

    /**
     * BlockingQueue pool queue .
     */
    private  BlockingQueue<DBWrappedConnection> connectionPoolQueue;

    /**
     * CopyOnWriteArrayList connection pool array of borrowed connections
     */
    private CopyOnWriteArrayList<DBWrappedConnection> borrowedConnections;
    /**
    * int maxPoolSize  max size.
     */
    private int maxPoolSize;
    /**
    * int minPoolCache .
    */
    private int minPoolCache;
    /**
    * int poolStatus .
    */
    private int poolStatus;
    /**
     * DPoolConfiguration database pool configuration .
     */
    private DBPoolConfiguration dbPoolConfiguration;
    /**
     * Constructor .
     * @throws DBPoolException dbPoolException .
     * @throws SQLException sql exception .
     * @param dbPoolConfiguration pool configuration .
     * */
     DBConnectionPool(final DBPoolConfiguration dbPoolConfiguration)
        throws DBPoolException, SQLException {

        this.dbPoolConfiguration = dbPoolConfiguration;
        this.maxPoolSize = dbPoolConfiguration.getMaxPoolSize();
        this.minPoolCache = dbPoolConfiguration.getMinPoolCache();
        this.connectionPoolQueue =
                    new LinkedBlockingDeque<DBWrappedConnection>(maxPoolSize);
         this.borrowedConnections =
                    new CopyOnWriteArrayList<DBWrappedConnection>();
         fillDBPool();
    }


    /**
     * Check if DBPool configuration has valid fields .
     * @throws SQLException sql exception .
     * @return wrapped connection .
     * */
    private DBWrappedConnection createConnection()
        throws SQLException {

        Connection connection;

        String username =  dbPoolConfiguration.getDbusername();
        String password = dbPoolConfiguration.getDbpassword();
        String driver = dbPoolConfiguration.getDbDriver();
        String url = dbPoolConfiguration.getDburl();

        // load driver and create new connection
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException exception) {
            throw new SQLException(DBCPMessageException.MSG_INVALID_DRIVER);
        }

        connection = DriverManager.getConnection(url, username, password);
        DBWrappedConnection dbWrappedConnection =
                new DBWrappedConnection(connection);

        return dbWrappedConnection;
    }


    /**
     * Check if DBPool configuration has valid fields .
     * @throws SQLException sql exception .
     * */
    private void fillDBPool()
        throws SQLException {

        int i;

        for (i = 0; i < dbPoolConfiguration.getMinPoolCache(); i++) {

            connectionPoolQueue.add(createConnection());
        }


    }

    /**
     * Return wrapped connection from Queue Connections.
     * @throws SQLException sql exception .
     * @return wrapped connection
     * */
    public synchronized Connection getConnection()
        throws SQLException {

        int localPoolStatus = getPoolStatus();
        DBWrappedConnection connection = null;
        if (localPoolStatus == DBCPStatus.CONNECTIONS_AVAILABLE) {

            boolean isFullCache = getPoolStatus()
                    == DBCPStatus.FULL_CACHE_CONNECTION;
            boolean isWaitingForCache = getPoolStatus()
                    == DBCPStatus.WAITING_FOR_FILLING_CACHE;

            if (isWaitingForCache) {
                return null;
            } else if (isFullCache) {
                fillDBPool();
            }

            connection = connectionPoolQueue.remove();
            borrowedConnections.add(connection);
        }
        return connection;
    }

    /**
     * Get status of the pool according to
     *  the borrowed and available connections.
     * @return borrowed pool status    .
     * */
    private int getPoolStatus() {

        int connectionsSize = borrowedConnections.size();
        int modConnectionsSize = connectionsSize % minPoolCache;

        if (connectionsSize == maxPoolSize) { // max pool size reached
            poolStatus = DBCPStatus.FULL_POOL_CONNECTION;
        } else if (modConnectionsSize == DBCPStatus.CONNECTIONS_AVAILABLE
            && (connectionPoolQueue.isEmpty())) { // check if cache is full
            poolStatus = DBCPStatus.FULL_CACHE_CONNECTION;
        } else if ((maxPoolSize - connectionsSize) < minPoolCache) {
            // waiting for reach min cache size
            poolStatus = DBCPStatus.WAITING_FOR_FILLING_CACHE;
        } else { // connections available
            poolStatus = DBCPStatus.CONNECTIONS_AVAILABLE;
        }

        return poolStatus;
    }

    /**
     *  Return borrowed connection to pool database .
     * @throws SQLException sql exception for new connection .
     * @param  connection the incoming connection .
     * @param  isValidConnection boolean of valid connection .
     * */
    private synchronized void sendConnectionToPool(final DBWrappedConnection connection,
        final boolean isValidConnection) throws SQLException {


        if (borrowedConnections.remove(connection)) {

            if (isValidConnection) {
                connectionPoolQueue.add(connection);
            } else {
                connectionPoolQueue.add(createConnection());
            }
        }

    }

    /**
     * Check if connection pool db is full .
     * @return full connection boolean .
     * */
    private boolean isFullConnectionPool() {

        return borrowedConnections.size() == maxPoolSize;

    }

    /**
     * This class is for Wrapping SQL Connection .
     * @author cgcastro
     * @version 1.0
     */
     class DBWrappedConnection implements Connection {

        /**
         * Connection the SQL Connection .
         */
        private Connection connection;
        /**
         *
         */
        private boolean deadConnection;
        /**
         * Constructor .
         * @param connection SQL Connection .
         * */
        DBWrappedConnection(final Connection connection) {

            this.connection = connection;

        }
        /**
         * close connection .
         * */
        @Override
        public void close() throws SQLException {

            checkIfIsActive();

            DBConnectionValidator connectionValidator =
                new DBConnectionValidator();

            boolean isValid = connectionValidator.isValidConnection(connection);

            sendConnectionToPool(this, isValid);

            deadConnection = true;

        }
        /**
         * check if connection is active .
         * @throws SQLException the sql exception
         * */
        private void checkIfIsActive() throws SQLException {

            if (deadConnection) {
                throw new
                    SQLException(DBCPMessageException.MSG_CONNECTION_NO_LONGER_ACTIVE);
            }

        }
        /**
         * Call for original Connection method.
         * @return statement .
         * */
        @Override
        public Statement createStatement() throws SQLException {

            checkIfIsActive();
            return connection.createStatement();
        }
        /**
         * Call for original Connection method.
         * @return prepared statement .
         * */
        @Override
        public PreparedStatement prepareStatement(final String sql)
            throws SQLException {
            checkIfIsActive();
            return connection.prepareStatement(sql);
        }
        /**
         * Call for original Connection method.
         * @return callable statement .
         * */
        @Override
        public CallableStatement prepareCall(final String sql)
            throws SQLException {

            checkIfIsActive();
            return connection.prepareCall(sql);
        }
        /**
         * Call for original Connection method.
         * @return native SQL .
         * */
        @Override
        public String nativeSQL(final String sql) throws SQLException {
            checkIfIsActive();
            return connection.nativeSQL(sql);
        }
        /**
         * Call for original method.
         * @param autoCommit the autocommit .
         * */
        @Override
        public void setAutoCommit(final boolean autoCommit) throws SQLException {
            checkIfIsActive();
            connection.setAutoCommit(autoCommit);
        }
        /**
         * Call for original Connection method.
         * @return auto commit  .
         * */
        @Override
        public boolean getAutoCommit() throws SQLException {
            checkIfIsActive();
            return connection.getAutoCommit();
        }
        /**
         * Call for original method.
         * */
        @Override
        public void commit() throws SQLException {
            checkIfIsActive();
            connection.commit();
        }
        /**
         * Call for original method.
         * */
        @Override
        public void rollback() throws SQLException {
            checkIfIsActive();
            connection.rollback();
        }
        /**
         * Call for original Connection method.
         * @return value if is closed the connection  .
         * */
        @Override
        public boolean isClosed() throws SQLException {
            checkIfIsActive();
            return connection.isClosed();
        }
        /**
         * Call for original Connection method.
         * @return DatabaseMetada information .
         * */
        @Override
        public DatabaseMetaData getMetaData()
            throws SQLException {
            checkIfIsActive();
            return connection.getMetaData();
        }
        /**
         * Call for original method.
         * @param readOnly readOnly value .
         * */
        @Override
        public void setReadOnly(final boolean readOnly)
            throws SQLException {
            checkIfIsActive();
            connection.setReadOnly(readOnly);
        }
        /**
         * Call for original Connection method.
         * @return boolean of read only  .
         * */
        @Override
        public boolean isReadOnly() throws SQLException {
            checkIfIsActive();
            return connection.isReadOnly();
        }
        /**
         * Call for original method.
         * @param catalog the catalog .
         * */
        @Override
        public void setCatalog(final String catalog)
            throws SQLException {
            checkIfIsActive();
            connection.setCatalog(catalog);
        }
        /**
         * Call for original Connection method.
         * @return catalog string .
         * */
        @Override
        public String getCatalog() throws SQLException {
            checkIfIsActive();
            return connection.getCatalog();
        }
        /**
         * Call for original method.
         * @param level the level .
         * */
        @Override
        public void setTransactionIsolation(final int level)
            throws SQLException {
            checkIfIsActive();
            connection.setTransactionIsolation(level);
        }
        /**
         * Call for original Connection method.
         * @return transaction isolation int .
         * */
        @Override
        public int getTransactionIsolation() throws SQLException {
            checkIfIsActive();
            return connection.getTransactionIsolation();
        }
        /**
         * Call for original Connection method.
         * @return sql warnings  .
         * */
        @Override
        public SQLWarning getWarnings() throws SQLException {
            checkIfIsActive();
            return connection.getWarnings();
        }
        /**
         * Call for original method.
         * */
        @Override
        public void clearWarnings() throws SQLException {
            checkIfIsActive();
            connection.clearWarnings();
        }
        /**
         * Call for original Connection method.
         * @return new statement .
         * */
        @Override
        public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency) throws SQLException {
            checkIfIsActive();
            return connection.createStatement(resultSetType, resultSetConcurrency);
        }
        /**
         * Call for original Connection method.
         * @return new prepared statement .
         * */
        @Override
        public PreparedStatement prepareStatement(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
            checkIfIsActive();
            return connection.prepareStatement(sql, resultSetType,
                resultSetConcurrency);
        }

        /**
         * Call for original Connection method.
         * @return new callable statement .
         * */
        @Override
        public CallableStatement prepareCall(final String sql,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
            checkIfIsActive();
            return connection.prepareCall(sql, resultSetType,
                    resultSetConcurrency);
        }
        /**
         * Call for original Connection method.
         * @return get type map  .
         * */
        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            checkIfIsActive();
            return connection.getTypeMap();
        }
        /**
         * Call for original method.
         * @param map the map .
         * */
        @Override
        public void setTypeMap(final Map<String, Class<?>> map)
            throws SQLException {
            checkIfIsActive();
            connection.setTypeMap(map);
        }
        /**
         * Call for original method.
         * @param holdability the holdability.
         * */
        @Override
        public void setHoldability(final int holdability) throws SQLException {
            checkIfIsActive();
            connection.setHoldability(holdability);
        }
        /**
         * Call for original Connection method.
         * @return holdability int .
         * */
        @Override
        public int getHoldability() throws SQLException {
            checkIfIsActive();
            return connection.getHoldability();
        }
        /**
         * Call for original Connection method.
         * @return save point .
         * */
        @Override
        public Savepoint setSavepoint() throws SQLException {
            checkIfIsActive();
            return connection.setSavepoint();
        }
        /**
         * Call for original Connection method.
         * @return save point .
         * */
        @Override
        public Savepoint setSavepoint(final String name) throws SQLException {
            checkIfIsActive();
            return connection.setSavepoint(name);
        }
        /**
         * Call for original method.
         * @param savepoint the savepoint .
         * */
        @Override
        public void rollback(final Savepoint savepoint) throws SQLException {
            checkIfIsActive();
            connection.rollback(savepoint);
        }
        /**
         * Call for original method.
         * @param savepoint the savepoint .
         * */
        @Override
        public void releaseSavepoint(final Savepoint savepoint)
            throws SQLException {
            checkIfIsActive();
            connection.releaseSavepoint(savepoint);
        }
        /**
         * Call for original Connection method.
         * @return new statement .
         * */
        @Override
        public Statement createStatement(final int resultSetType,
            final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
            checkIfIsActive();
            return connection.createStatement(resultSetType,
                resultSetConcurrency, resultSetHoldability);
        }

        /**
         * Call for original Connection method.
         * @return new prepared statement .
         * */
        @Override
        public PreparedStatement prepareStatement(final String sql,
            final int resultSetType,
            final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
            checkIfIsActive();
            return connection.prepareStatement(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
        }
        /**
         * Call for original Connection method.
         * @return new callable statement  .
         * */
        @Override
        public CallableStatement prepareCall(final String sql,
            final int resultSetType,
            final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
            checkIfIsActive();
            return connection.prepareCall(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
        }

        /**
         * Call for original Connection method.
         * @return prepared statement  .
         * */
        @Override
        public PreparedStatement prepareStatement(final String sql,
            final int autoGeneratedKeys) throws SQLException {
            checkIfIsActive();
            return connection.prepareStatement(sql, autoGeneratedKeys);
        }

        /**
         * Call for original Connection method.
         * @return prepared statement .
         * */
        @Override
        public PreparedStatement prepareStatement(final String sql,
            final int[] columnIndexes) throws SQLException {
            checkIfIsActive();
            return connection.prepareStatement(sql, columnIndexes);
        }

        /**
         * Call for original Connection method.
         * @return prepared statement .
         * */
        @Override
        public PreparedStatement prepareStatement(final String sql,
            final String[] columnNames) throws SQLException {

            checkIfIsActive();
            return connection.prepareStatement(sql, columnNames);
        }

        /**
         * Call for original Connection method.
         * @return the clob .
         * */
        @Override
        public Clob createClob() throws SQLException {

            checkIfIsActive();
            return connection.createClob();
        }

        /**
         * Call for original Connection method.
         * @return the blob .
         * */
        @Override
        public Blob createBlob() throws SQLException {

            checkIfIsActive();
            return connection.createBlob();
        }

        /**
         * Call for original Connection method.
         * @return the nclob .
         * */
        @Override
        public NClob createNClob() throws SQLException {
            checkIfIsActive();
            return connection.createNClob();
        }

        /**
         * Call for original Connection method.
         * @return SQL XML .
         * */
        @Override
        public SQLXML createSQLXML() throws SQLException {
            checkIfIsActive();
            return connection.createSQLXML();
        }

        /**
         * Call for original Connection method.
         * @return boolean of validation .
         * */
        @Override
        public boolean isValid(final int timeout) throws SQLException {
            checkIfIsActive();
            return connection.isValid(timeout);
        }
        /**
         * Call for original method.
         * @param name the name .
         * @param value the value
         * */
        @Override
        public void setClientInfo(final String name, final String value)
            throws SQLClientInfoException {
            connection.setClientInfo(name, value);
        }
        /**
         * Call for original method.
         * @param properties the properties .
         * */
        @Override
        public void setClientInfo(final Properties properties)
            throws SQLClientInfoException {
            connection.setClientInfo(properties);
        }
        /**
         * Call for original Connection method.
         * @return client info .
         * */
        @Override
        public String getClientInfo(final String name)
            throws SQLException {
            checkIfIsActive();
            return connection.getClientInfo(name);
        }

        /**
         * Call for original Connection method.
         * @return client info properties .
         * */
        @Override
        public Properties getClientInfo() throws SQLException {
            checkIfIsActive();
            return connection.getClientInfo();
        }

        /**
         * Call for original Connection method.
         * @return new array .
         * */
        @Override
        public Array createArrayOf(final String typeName, final Object[] elements)
            throws SQLException {
            checkIfIsActive();
            return connection.createArrayOf(typeName, elements);
        }

        /**
         * Call for original Connection method.
         * @return new struct  .
         * */
        @Override
        public Struct createStruct(final String typeName,
            final Object[] attributes) throws SQLException {
            checkIfIsActive();
            return connection.createStruct(typeName, attributes);
        }
        /**
         * Call for original method.
         * @param schema the schema .
         * */
        @Override
        public void setSchema(final String schema) throws SQLException {
            checkIfIsActive();
            connection.setSchema(schema);
        }

        /**
         * Call for original Connection method.
         * @return new schema .
         * */
        @Override
        public String getSchema() throws SQLException {
            checkIfIsActive();
            return connection.getSchema();
        }
        /**
         * Call for original method.
         * @param executor the executor .
         * */
        @Override
        public void abort(final Executor executor) throws SQLException {
            checkIfIsActive();
            connection.abort(executor);
        }
        /**
         * Call for original method.
         * @param executor the executor .
         * @param milliseconds the milliseconds .
         * */
        @Override
        public void setNetworkTimeout(final Executor executor,
            final int milliseconds) throws SQLException {
            checkIfIsActive();
            connection.setNetworkTimeout(executor, milliseconds);
        }

        /**
         * Call for original Connection method.
         * @return new network timeout .
         * */
        @Override
        public int getNetworkTimeout() throws SQLException {
            checkIfIsActive();
            return connection.getNetworkTimeout();
        }

        /**
         * Call for original Connection method.
         * @return T unwrap .
         * */
        @Override
        public <T> T unwrap(final Class<T> iface) throws SQLException {
            checkIfIsActive();
            return connection.unwrap(iface);
        }
        /**
         * Call for original Connection method.
         * @return value if is wrapped .
         * */
        @Override
        public boolean isWrapperFor(final Class<?> iface) throws SQLException {
            checkIfIsActive();
            return connection.isWrapperFor(iface);
        }
    }
}
