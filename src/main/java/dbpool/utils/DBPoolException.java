package dbpool.utils;


/**
 * This class contains a list of possible dbpool exceptions.
 * @author cgcastro
 * @version 1.0
 */
public class DBPoolException extends Exception {

    /**
     * Constructor .
     * @param message the exception message .
     * */
    public DBPoolException(final String message){
        super(message);
    }

}
