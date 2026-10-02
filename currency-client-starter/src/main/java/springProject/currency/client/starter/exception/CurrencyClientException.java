package springProject.currency.client.starter.exception;

public class CurrencyClientException extends RuntimeException {
    private final int statusCode;

    public CurrencyClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
