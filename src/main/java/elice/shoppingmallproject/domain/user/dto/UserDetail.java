package elice.shoppingmallproject.domain.user.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class UserDetail {
    private LocalDateTime createAt;
    private String email;
    private String username;
    private String role;

    public UserDetail(LocalDateTime createAt, String email, String username, String role) {
        this.createAt = createAt;
        this.email = email;
        this.username = username;
        this.role = role;
    }
}
