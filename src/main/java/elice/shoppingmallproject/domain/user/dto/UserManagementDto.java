package elice.shoppingmallproject.domain.user.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserManagementDto {

    private int totalUserCount;
    private int adminCount;
    private List<UserDetail> users;
}
