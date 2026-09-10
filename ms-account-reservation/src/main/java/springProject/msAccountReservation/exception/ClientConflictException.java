package springProject.msAccountReservation.exception;

public class ClientConflictException extends RuntimeException {
    public ClientConflictException(String message) {
        super(message);
    }
}
