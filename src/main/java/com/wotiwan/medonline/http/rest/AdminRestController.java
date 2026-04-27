package com.wotiwan.medonline.http.rest;

import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.dto.*;
import com.wotiwan.medonline.security.user.SecurityUser;
import com.wotiwan.medonline.service.DoctorService;
import com.wotiwan.medonline.service.SpecializationService;
import com.wotiwan.medonline.service.UserDoctorService;
import com.wotiwan.medonline.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final UserDoctorService userDoctorService;
    private final SpecializationService specializationService;

    @GetMapping("/users")
    public List<UserReadDto> getUsers(@RequestParam(required = false) String role,
                                      @AuthenticationPrincipal SecurityUser securityUser) {
        if (role == null) {
            return userService.findAll();
        }
        else if (role.isBlank() || role.equals("ALL") || !Role.roleExists(role) ) {
            return userService.findAll();
        } else {
            return userService.findAllByRole(Role.valueOf(role));
        }
    }

    @PutMapping("/users/{id}/role/{role}")
    public ResponseEntity<Map<String, String>> updateUserRole(@PathVariable Integer id,
                                               @PathVariable Role role) {

        userService.updateRole(id, role);
        return ResponseEntity.ok(
                Map.of("message", "Роль успешно обновлена")
        );
    }

    @PostMapping("/doctors/create")
    public ResponseEntity<Map<String, String>> createDoctorFromUser(
            @Validated @RequestBody DoctorCreateDto doctorCreateDto) {

        try {
            doctorService.create(doctorCreateDto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(
                Map.of("message", "Врач успешно зарегистрирован!")
        );
    }

    @PostMapping("/doctors/full/create")
    public ResponseEntity<Map<String, String>> createDoctor(
            @Validated @RequestBody UserDoctorCreateDto userDoctorCreateDto) {

        userDoctorService.create(userDoctorCreateDto);

        return ResponseEntity.ok(
                Map.of("message", "Врач успешно зарегистрирован!")
        );
    }

}
