package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Appointment;
import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.database.repository.AppointmentRepository;
import com.wotiwan.medonline.database.repository.UserRepository;
import com.wotiwan.medonline.dto.UserCreateDto;
import com.wotiwan.medonline.dto.UserEditDto;
import com.wotiwan.medonline.dto.UserReadDto;
import com.wotiwan.medonline.mapper.UserCreateMapper;
import com.wotiwan.medonline.mapper.UserEditMapper;
import com.wotiwan.medonline.mapper.UserReadMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// UserService предназначен для регистрации пользователей и их авторизации.
// Содержит в себе User и Patient entities (Решено объединить юзеров и пациентов)

@RequiredArgsConstructor
@ToString
@Service
@Transactional(readOnly = true) // Для чего readOnly? ТАк быстрее работает, но только на чтение
public class UserService implements UserDetailsService {
    private final UserReadMapper userReadMapper;
    private final UserCreateMapper userCreateMapper;
    private final UserEditMapper userEditMapper;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public Optional<UserReadDto> findById(Integer id) {
        return userRepository.findById(id)
                .map(userReadMapper::map);
    }

    @Transactional // Над ними переопределяем аннотацию, без ридонли флага, чтобы работало
    public UserReadDto create(UserCreateDto userDto) {
        return Optional.of(userDto)
                .map(userCreateMapper::map) // Мапим dto в Entity (там же хеширование пароля)
                .map(userRepository::save) // Сохраняем Entity
                .map(userReadMapper::map) // Мапим результат в dto для чтения
                .orElseThrow();
    }

    // Находим юзера по id, затем меняем поля, переданные в UserEditDto, после чего сохраняем
    // Далее мапим обновлённого юзера в дто для отправки на фронт
    @Transactional
    public Optional<UserReadDto> update(Integer id, UserEditDto userDto) {
        return userRepository.findById(id)
                .map(e -> userEditMapper.map(userDto, e))
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map);
    }

    // В Principal хранится только email текущего пользователя, по-этому искать можем так
    public Optional<UserReadDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userReadMapper::map);
    }

    // Нужно Spring Security для логина
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }


    // TODO: Добавить функционал администратора -- смена ролей + удаление пользователей
    // Потребуется UserEditDto + методы updateRole(), delete()

    public List<UserReadDto> findAll() {
        return userRepository.findAll().stream()
                .map(userReadMapper::map)
                .toList();
    }
    public List<UserReadDto> findAllByRole(Role role) {
        return userRepository.findAllByRole(role).stream()
                .map(userReadMapper::map)
                .toList();
    }
    @Transactional
    public boolean updateRole(Integer id, Role role) {
        return userRepository.updateRole(id, role) > 0;
    }
    @Transactional
    public boolean delete(Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    userRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    // TODO: Добавить пагинацию
    // Загрузка всех записей к врачу одного пользователя
    public List<Appointment> findAllUserAppointmentsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return appointmentRepository
                .findAllByPatientIdOrderByTimeSlot_StartTimeDesc(user.getId());
    }

}
