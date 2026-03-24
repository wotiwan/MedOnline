package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Role;
import com.wotiwan.medonline.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying(clearAutomatically = true)
    @Query(
            "update User u set u.role = :role where u.id = :id"
    )
    int updateRole(Long id, Role role);

}
