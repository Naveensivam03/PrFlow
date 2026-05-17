package prflow.spring_backend.exception;

public abstract class PrflowException extends RuntimeException {

    protected PrflowException(String message) {
        super(message);
    }
}
