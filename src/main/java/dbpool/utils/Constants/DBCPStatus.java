package dbpool.utils.Constants;

/**
 * This class contains the possible status of the pool database .
 * @author cgcastro
 * @version 1.0
 */
public class DBCPStatus {
    /*
 * int for full pool connection status .
 */
    public static final int FULL_POOL_CONNECTION = 1;
    /*
     * int reach min cache
     */
    public static final int FULL_CACHE_CONNECTION = 2;
    /*
     * int fill cache
     */
    public static final int WAITING_FOR_FILLING_CACHE = 3;

    /*
     * int connections available
     */
    public static final int CONNECTIONS_AVAILABLE = 0;
}
