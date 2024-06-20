package elice.shoppingmallproject.domain.order.repository;

import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Query("SELECT o FROM Orders o WHERE o.userId = :userId " +
        "AND (:orderId IS NULL OR CAST(o.id AS string) LIKE CONCAT('%', :orderId, '%')) " +
        "AND (:startDate IS NULL OR o.createAt >= :startDate)" +
        "AND (:endDate IS NULL OR o.createAt <= :endDate)" +
        "ORDER BY o.createAt DESC")
    Page<Orders> searchUserOrders(@Param("userId") Long userId,
        @Param("orderId") Long orderId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable);

    @Query("SELECT o FROM Orders o WHERE (:orderId IS NULL OR CAST(o.id AS string) LIKE CONCAT('%', :orderId, '%')) " +
        "AND (:startDate IS NULL OR o.createAt >= :startDate)" +
        "AND (:endDate IS NULL OR o.createAt <= :endDate)" +
        "AND (:orderStatus IS NULL OR o.orderStatus = :orderStatus)" +
        "ORDER BY o.createAt DESC")
    Page<Orders> searchAllOrders(@Param("orderId") Long orderId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("orderStatus") OrderStatus orderStatus,
        Pageable pageable);
}
