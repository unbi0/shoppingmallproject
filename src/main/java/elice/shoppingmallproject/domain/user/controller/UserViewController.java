package elice.shoppingmallproject.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/loginForm")
    public String loginView() {
        return "login/login";
    }

    @GetMapping("/registerForm")
    public String registerView() {
        return "register/register";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin/admin";
    }

    @GetMapping("/admin/users")
    public String adminUsersPage() {
        return "admin-users/admin-users";
    }
}
