package elice.shoppingmallproject.domain.auth.repository;

import elice.shoppingmallproject.domain.auth.entity.Refresh;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
    Boolean existsByToken(String token);

    Refresh findByToken(String token);

    @Transactional
    void deleteByToken(String token);
}
