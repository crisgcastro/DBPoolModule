package dbpool.utils.Constants;

/**
 * Class with all the constants in the DBPool .
 * @author cgcastro
 * @version 1.0
 */
public class DBCPMessageException {
    /*
     * public String user of database .
     */
    public static final String MGS_UNDEFINED_USERNAME =
        "There is no database user defined yet";
    /*
     * public String password of database .
     */
    public static final String MSG_UNDEFINED_PASSWORD =
        "There is no database password defined yet";
    /*
     * public String url of database .
     */
    public static final String MSG_UNDEFINED_URL =
            "There is no URL Database defined yet";
    /*
     * public String driver of database .
     */
    public static final String MSG_UNDEFINED_DRIVER =
            "There is no Database Driver defined yet";
    /*
     * public static String max size .
     */
    public static final String MSG_ZERO_MAX_SIZE =
            "The maximium pool size should be greater than 0";
    /*
     * public static String min pool cache size .
     */
    public static final String MSG_ZERO_MIN_CACHE_SIZE =
            "The minimium cache size should be greater than 0 and "
            +  "less than the maximium pool size";
    /*
     * String message for connection no longer active .
     */
    public static final String MSG_CONNECTION_NO_LONGER_ACTIVE =
            "Connection no longer active";
    /*
     * String message for invalid driver .
     */
    public static final String MSG_INVALID_DRIVER =
            "Invalid database Driver";

    /*
     * String message for invalid driver .
     */
    public static final String MSG_INVALID_CONFIGURATION =
            "Invalid Configuration for creating new pool";
    /*
     * String message for empty queries .
     */
    public static final String MSG_EMPTY_QUERY =
            "Query must not be empty";

    /*
     * String message for empty queries .
     */
    public static final String MSG_NULL_CONFIGURATION =
            "Configuration cannot be Null";

}
