package elice.shoppingmallproject.domain.auth.repository;

import elice.shoppingmallproject.domain.auth.entity.Auth;
import elice.shoppingmallproject.domain.auth.entity.Provider;
import elice.shoppingmallproject.domain.user.entity.User;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByUserAndProvider(User user, Provider provider);

    @Transactional
    void deleteByUserId(Long userId);
}
