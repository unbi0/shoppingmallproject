package elice.shoppingmallproject.domain.order.exception;

public class InvalidOrderException extends RuntimeException{

    public InvalidOrderException(String message) {
        super(message);
    }
}
