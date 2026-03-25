package com.wotiwan.medonline.database.repository;

import com.wotiwan.medonline.database.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {

    // Для проверки на существование такой специальности при создании/редактировании профиля врача
    Optional<Specialization> findByName(String name);

}
