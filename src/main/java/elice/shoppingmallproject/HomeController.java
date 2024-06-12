package elice.shoppingmallproject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {

        return "redirect:index.html";
    }

    @GetMapping("/login-form")
    public String login() {
        return "login";
    }

    @GetMapping("/register-form")
    public String register() {
        return "register";
    }
}
