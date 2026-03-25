package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.UserCreateEditDto;
import com.wotiwan.medonline.dto.UserReadDto;
import com.wotiwan.medonline.mapper.UserCreateEditMapper;
import com.wotiwan.medonline.mapper.UserReadMapper;
import com.wotiwan.medonline.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

// UserService предназначен для регистрации пользователей и их авторизации.
// Содержит в себе User и Patient entities (Решено объединить юзеров и пациентов)

@RequiredArgsConstructor
@ToString
@Service
@Transactional(readOnly = true) // Для чего readOnly? ТАк быстрее работает, но только на чтение
public class UserService implements UserDetailsService {
    private final UserReadMapper userReadMapper;
    private final UserCreateEditMapper userCreateEditMapper;
    private final UserRepository userRepository;

    public Optional<UserReadDto> findById(Long id) {
        return userRepository.findById(id)
                .map(userReadMapper::map);
    }

    @Transactional // Над ними переопределяем аннотацию, без ридонли флага, чтобы работало
    public UserReadDto create(UserCreateEditDto userDto) {
        return Optional.of(userDto)
                .map(userCreateEditMapper::map) // Мапим dto в Entity (там же хеширование пароля)
                .map(userRepository::save) // Сохраняем Entity
                .map(userReadMapper::map) // Мапим результат в dto для чтения
                .orElseThrow();
    }

    @Transactional
    public Optional<UserReadDto> update(Long id, UserCreateEditDto userDto) {
        return userRepository.findById(id)
                .map(e -> userCreateEditMapper.map(userDto, e))
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map);
    }

    // Логин
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singleton(user.getRole())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }


    // TODO: Добавить функционал администратора -- смена ролей + удаление пользователей
    // Потребуется UserEditDto + методы updateRole(), delete()
}
