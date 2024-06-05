package elice.shoppingmallproject.domain.auth.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import lombok.Getter;

@Getter
@Entity
public class Refresh {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String token;
    private Date expiration;

    public static Refresh createRefresh(String email, String token, Long expiration) {
        Date expiredMs = new Date(System.currentTimeMillis() + expiration);

        Refresh refresh = new Refresh();
        refresh.email = email;
        refresh.token = token;
        refresh.expiration = expiredMs;

        return refresh;
    }
    public static String generateToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}

