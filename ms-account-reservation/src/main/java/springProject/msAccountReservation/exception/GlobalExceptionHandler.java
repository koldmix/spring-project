package springProject.msAccountReservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springProject.msAccountReservation.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ClientConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("CLIENT_ALREADY_EXISTS");
        errorResponse.setErrorDescription(ex.getMessage());
        errorResponse.setStatusCode(HttpStatus.CONFLICT.value());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ClientNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("CLIENT_NOT_FOUND");
        errorResponse.setErrorDescription(ex.getMessage());
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("INTERNAL_SERVER_ERROR");
        errorResponse.setErrorDescription("Внутренняя ошибка сервера: " + ex.getMessage());
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException
            (HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("BAD_REQUEST");
        errorResponse.setErrorDescription("Ошибка чтения запроса: неверный формат данных " +
                "или нарушена структура JSON.");
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException
    (MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("BAD_REQUEST");

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "Поле '" + error.getField() + "': "
                        + error.getDefaultMessage())
                .findFirst()
                .orElse("Невалидные данные в запросе");

        errorResponse.setErrorDescription(errorMessage);
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}