package elice.shoppingmallproject.domain.auth.repository;

import elice.shoppingmallproject.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}
