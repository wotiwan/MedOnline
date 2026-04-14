package com.wotiwan.medonline.security.user;

import com.wotiwan.medonline.database.entity.User;
import com.wotiwan.medonline.database.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private UserRepository repository;

    protected User findUserByUsername(String username) {
        return repository.findByEmail(username)
                .orElse(new User());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUser(findUserByUsername(username));
    }
}