package com.wotiwan.medonline.service;

import com.wotiwan.medonline.database.entity.Specialization;
import com.wotiwan.medonline.database.repository.SpecializationRepository;
import com.wotiwan.medonline.dto.SpecializationReadDto;
import com.wotiwan.medonline.mapper.SpecializationReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final SpecializationReadMapper specializationReadMapper;

    public List<SpecializationReadDto> findAll() {
        return specializationRepository.findAll().stream()
                .map(specializationReadMapper::map)
                .toList();
    }

    public Optional<Specialization> findById(Integer specializationId) {
        return specializationRepository.findById(specializationId);
    }
}
