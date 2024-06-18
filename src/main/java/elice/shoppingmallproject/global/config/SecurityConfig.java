package elice.shoppingmallproject.global.config;

import elice.shoppingmallproject.domain.auth.repository.RefreshRepository;
import elice.shoppingmallproject.global.jwt.CustomLogoutFilter;
import elice.shoppingmallproject.global.jwt.JwtAccessDeniedHandler;
import elice.shoppingmallproject.global.jwt.JwtAuthenticationEntryPoint;
import elice.shoppingmallproject.global.jwt.JwtFilter;
import elice.shoppingmallproject.global.jwt.JwtUtil;
import elice.shoppingmallproject.global.jwt.LoginFilter;
import elice.shoppingmallproject.global.oauth2.handler.CustomSuccessHandler;
import elice.shoppingmallproject.global.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] WHITER_LIST = {
            "/error/**", "/css/**", "/js/**", "/",
            "/loginForm", "/registerForm", "account", "my-page", //USER VIEW
            "/login", "/sign-up", // USER API
            "api/product", "api/product/category/**", "/api/product/search", // PRODUCT API

    };

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiY2F0ZWdvcnkiOiJhY2Nlc3MiLCJlbWFpbCI6ImFkbWluQGVsaWNlLmNvbSIsInJvbGUiOiJST0xFX0FETUlOIiwiaWF0IjoxNzE4NjE4OTQ0LCJleHAiOjE3MTg2MTkwNDR9.GXYx5JvV5waivBXAFf4oYJ_dot_DQk-aFhJ4lzLXVZI
//UuF8ewLobPOnkGoubRSFCFB4woY2FJi2
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http.csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);

        http.logout(AbstractHttpConfigurer::disable);

        //http basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler));


        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(WHITER_LIST).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/reissue").permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .anyRequest().permitAll());

        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(refreshRepository), LogoutFilter.class);

        //세션 설정
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

