package elice.shoppingmallproject.domain.order.exception;

public class OrderStatusNotModifiableException extends RuntimeException{

    public OrderStatusNotModifiableException(String message) {
        super(message);
    }
}
