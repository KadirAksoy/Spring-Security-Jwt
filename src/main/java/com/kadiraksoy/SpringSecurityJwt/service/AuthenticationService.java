package com.kadiraksoy.SpringSecurityJwt.service;

import com.kadiraksoy.SpringSecurityJwt.dto.JwtAuthenticationResponse;
import com.kadiraksoy.SpringSecurityJwt.dto.SignInRequest;
import com.kadiraksoy.SpringSecurityJwt.dto.SignUpRequest;
import com.kadiraksoy.SpringSecurityJwt.model.Role;
import com.kadiraksoy.SpringSecurityJwt.model.User;
import com.kadiraksoy.SpringSecurityJwt.repository.UserRepository;
import com.kadiraksoy.SpringSecurityJwt.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    //Yeni bir kullanıcının kaydını oluşturur.
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // Kayıt olan birinin rolünü User olarak yapıyoruz.
                .role(Role.ROLE_USER)
                .build();

        user = userService.save(user);
        // Ardından, bu kullanıcı için bir JWT oluşturur
        var jwt = jwtService.generateToken(user);
        //oluşturulan token ile birlikte JwtAuthenticationResponse nesnesini döndürür.
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


    //Kullanıcı girişini işler.
    public JwtAuthenticationResponse signin(SignInRequest request) {
        //Kullanıcı adı ve şifre doğrulaması için authenticationManager.authenticate() metodu kullanılır.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        // Eğer doğrulama başarılıysa, ilgili kullanıcıyı bulur ve bu kullanıcı için bir JWT oluşturur
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        //oluşturulan token ile birlikte JwtAuthenticationResponse nesnesini döndürür.
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

}