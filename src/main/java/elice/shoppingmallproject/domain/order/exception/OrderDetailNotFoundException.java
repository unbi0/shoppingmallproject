package elice.shoppingmallproject.domain.order.exception;

public class OrderDetailNotFoundException extends RuntimeException{

    public OrderDetailNotFoundException(String message) {
        super(message);
    }
}
