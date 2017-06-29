package dbpool.dbmodule;

import dbpool.utils.Constants.DBCPMessageException;
import dbpool.utils.DBPoolException;

/**
 * Class for managing Connection Pool  and setting database resources.
 * @author cgcastro
 * @version 1.0
 */
public class DBPoolConfiguration {

    /**
     * String Database username .
     */
    private String dbusername;
    /**
     * String Database password .
     */
    private String dbpassword;
    /**
     * String Database URL .
     */
    private String dburl;
    /**
     * int min pool cache .
     */
    private int minPoolCache;
    /**
     * int max pool size .
     */
    private int maxPoolSize;
    /**
     * String driver class name .
     */
    private String dbDriver;
    /**
     * DPoolConfiguration database pool configuration .
     */
    private DBPoolConfiguration dbPoolConfiguration;
    /**
     * default pool size .
     */
    private static final int DEFAULT_POOL_SIZE = 50;
    /**
     * default cache size .
     */
    private static final int DEFAULT_MIN_CACHE = 10;

    /**
     * Default constructor .
     **/
    public DBPoolConfiguration() {

        this.maxPoolSize = DEFAULT_POOL_SIZE;
        this.minPoolCache = DEFAULT_MIN_CACHE;
    }

    /**
     * Method for getting database driver.
     * @return String the database driver .
     * */
    public String getDbDriver() {
        return dbDriver;
    }

    /**
     * Method for setting database driver.
     * @param dbDriver the database driver .
     * */
    public void setDbDriver(final String dbDriver) {
        this.dbDriver = dbDriver;
    }

    /**
     * Method for getting database username.
     * @return String the database username .
     * */
    public String getDbusername() {
        return this.dbusername;
    }
    /**
     * Method for setting database username.
     * @param dbusername the username .
     * */
    public void setDbusername(final String dbusername) {
        this.dbusername = dbusername;
    }

    /**
     * Method for getting database password.
     * @return database password .
     * */
    public String getDbpassword() {
        return dbpassword;
    }

    /**
     * Method for setting database username.
     * @param dbpassword the username .
     * */
    public void setDbpassword(final String dbpassword) {
        this.dbpassword = dbpassword;
    }
    /**
     * Method for getting database url.
     * @return database url .
     * */
    public String getDburl() {
        return dburl;
    }
    /**
     * Method for setting database url.
     * @param dburl the db url .
     * */
    public void setDburl(final String dburl) {
        this.dburl = dburl;
    }
    /**
     * Method for getting min pool cache configuration.
     * @return min pool cache configuration .
     * */
    public int getMinPoolCache() {
        return minPoolCache;
    }

    /**
     * Method for setting min pool cache configuration .
     * @param minPoolCache min pool cache configuration .
     * */
    public void setMinPoolCache(final int minPoolCache) {
        this.minPoolCache = minPoolCache;
    }

    /**
     * Method for getting maximium pool size.
     * @return max pool size .
     * */
    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    /**
     * Method for setting max pool size.
     * @param maxPoolSize the max pool size .
     * */
    public void setMaxPoolSize(final int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Check if DBPool configuration has valid fields .
     * @throws DBPoolException if has empty fields
     * @return true if is valid configuration
     * */
    public boolean isValid()
            throws DBPoolException {

        try {
            if (getDbusername().isEmpty()) {
                throw new DBPoolException(DBCPMessageException.MGS_UNDEFINED_USERNAME);

            }
            if (getDbpassword().isEmpty()) {
                throw new DBPoolException(DBCPMessageException.MSG_UNDEFINED_PASSWORD);

            }
            if (getDburl().isEmpty()) {
                throw new DBPoolException(DBCPMessageException.MSG_UNDEFINED_URL);

            }
            if (getDbDriver().isEmpty()) {
                throw new DBPoolException(DBCPMessageException.MSG_UNDEFINED_DRIVER);

            }
            if (!(getMaxPoolSize() >= 0)) {
                throw new DBPoolException(DBCPMessageException.MSG_ZERO_MAX_SIZE);

            }
            if (!(getMinPoolCache() >= 0
                    && (getMinPoolCache()
                    <= getMaxPoolSize()))) {
                throw new DBPoolException(DBCPMessageException.MSG_ZERO_MIN_CACHE_SIZE);
            }

        } catch (NullPointerException nullException) {
            throw new DBPoolException(DBCPMessageException.MSG_NULL_CONFIGURATION);
        }

       return true;

    }
}

