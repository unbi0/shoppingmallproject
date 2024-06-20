package elice.shoppingmallproject;

import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.domain.user.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDB {
    private final InitService initService;
    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final BCryptPasswordEncoder bCryptPasswordEncoder;

        public void dbInit() {
            // 기존 ADMIN 계정을 확인합니다.
            if (!adminUserExists()) {
                User user = User.createUser("관리자", "admin@elice.com", "admin135!", null, Role.ADMIN);
                user.passwordEncode(bCryptPasswordEncoder);
                em.persist(user);
            }
        }

        private boolean adminUserExists() {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", "admin@elice.com");
            Long count = query.getSingleResult();
            return count > 0;
        }
    }
}



