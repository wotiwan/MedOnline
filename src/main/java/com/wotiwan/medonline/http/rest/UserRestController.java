package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.dto.*;
import com.wotiwan.medonline.security.user.SecurityUser;
import com.wotiwan.medonline.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserReadDto findById(@PathVariable("id") Integer id) {
        return userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserReadDto create(@Validated @RequestBody UserCreateDto user) { // Зач реквест боди?
        return userService.create(user);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserReadDto update(@PathVariable("id") Integer id,
                              @Validated @RequestBody UserEditDto user) { // Зач реквест боди?
        return userService.update(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable("id") Long id) {
//        if (!userService.delete(id)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//    }



    @GetMapping("/profile")
    public ProfileResponse profilePage(@AuthenticationPrincipal SecurityUser securityUser) {

        UserReadDto user = userService.findByEmail(securityUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<AppointmentReadDto> appointments =
                userService.findAllUserAppointmentsByUserId(securityUser.getUser().getId());

        return new ProfileResponse(user, appointments);
    }

}
