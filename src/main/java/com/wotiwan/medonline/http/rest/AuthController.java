package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.dto.UserCreateDto;
import com.wotiwan.medonline.dto.auth.AuthRequest;
import com.wotiwan.medonline.dto.auth.AuthResponse;
import com.wotiwan.medonline.dto.auth.RegisterRequest;
import com.wotiwan.medonline.security.jwt.JwtUtils;
import com.wotiwan.medonline.security.user.SecurityUser;
import com.wotiwan.medonline.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails);

            return new ResponseEntity<>(new AuthResponse("Вы успешно вошли в систему", jwt), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Неверные учетные данные", HttpStatus.UNAUTHORIZED);
        }
    }

    // TODO: подумать над тем какой ответ лучше отдавать
    @PostMapping("/register")
    public String register(@Valid @RequestBody UserCreateDto userCreateDto) {
        // TODO: обработку ошибок добавить
        userService.create(userCreateDto);
        return "Регистрация успешна";
    }

    // Метод для удобного получения юзера
    @GetMapping("/current-user")
    public User currentUser(@AuthenticationPrincipal SecurityUser securityUser) {
        return securityUser.getUser();
    }

}