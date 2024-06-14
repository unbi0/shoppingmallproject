package elice.shoppingmallproject.domain.order.repository;

import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT o FROM Orders o WHERE o.userId = :userId " +
        "AND (:orderId IS NULL OR o.id = :orderId) " +
        "AND (o.createAt BETWEEN :startDate AND :endDate) " +
        "AND (:orderStatus IS NULL OR o.orderStatus = :orderStatus)")
    List<Orders> searchUserOrders(@Param("userId") Long userId,
        @Param("orderId") Long orderId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("orderStatus") OrderStatus orderStatus);

    @Query("SELECT o FROM Orders o WHERE (:orderId IS NULL OR o.id = :orderId) " +
        "AND (o.createAt BETWEEN :startDate AND :endDate) " +
        "AND (:orderStatus IS NULL OR o.orderStatus = :orderStatus)")
    List<Orders> searchAllOrders(@Param("orderId") Long orderId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("orderStatus") OrderStatus orderStatus);
}
