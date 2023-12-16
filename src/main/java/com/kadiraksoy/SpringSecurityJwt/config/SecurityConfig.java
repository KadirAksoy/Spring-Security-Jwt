package com.kadiraksoy.SpringSecurityJwt.config;
import com.kadiraksoy.SpringSecurityJwt.security.JwtAuthenticationFilter;
import com.kadiraksoy.SpringSecurityJwt.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity// metot tabanlı güvenlik konfigürasyonunu etkinleştirir.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    //Spring Security için bir AuthenticationProvider bean'i oluşturur.
    // Bu metod, DaoAuthenticationProvider kullanarak kullanıcı doğrulama işlemlerini yapılandırır.
    // userService üzerinden kullanıcı detaylarını alır ve passwordEncoder'ı kullanarak şifre doğrulamasını yapar.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    //Spring Security'nin AuthenticationManager bean'ini oluşturur.
    // Bu metod, AuthenticationConfiguration üzerinden AuthenticationManager'ı alarak oluşturur.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Cross-Site Request Forgery (CSRF) korumasını devre dışı bırakır.
                .csrf(AbstractHttpConfigurer::disable
                )
                //Oturum yönetimini yapılandırır ve STATELESS olarak belirler.
                // Bu, sunucunun oturumları tutmamasını ve her isteği ayrı olarak işlemesini sağlar.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Http isteklerini yetkilendirir
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/signup", "/api/v1/signin").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/test/**").permitAll()
                        .anyRequest().authenticated()
                )
                //Özel olarak oluşturulan JwtAuthenticationFilter'ı,
                // UsernamePasswordAuthenticationFilter'dan önce çalışacak şekilde konfigüre eder.
                // Bu, gelen isteklerde JWT kimlik doğrulaması için filtre ekler.
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}