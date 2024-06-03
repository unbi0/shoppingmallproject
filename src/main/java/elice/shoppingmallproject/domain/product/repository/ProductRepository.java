package elice.shoppingmallproject.domain.product.repository;


import elice.shoppingmallproject.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {


}
