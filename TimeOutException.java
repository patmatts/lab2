
public class TimeOutException extends Exception {

    public TimeOutException(String message) {
        super(message);
    }

    public TimeOutException(String message, Throwable throwable) {
        super(message, throwable);
    }

}