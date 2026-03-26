package com.example.StudentManagement.service;

import com.example.StudentManagement.dto.request.GenderRequest;
import com.example.StudentManagement.dto.response.GenderResponse;
import com.example.StudentManagement.entity.Gender;
import com.example.StudentManagement.exception.DuplicateResourceException;
import com.example.StudentManagement.exception.ResourceNotFoundException;
import com.example.StudentManagement.mapper.GenderMapper;
import com.example.StudentManagement.repository.GenderRepository;
import com.example.StudentManagement.websocket.WebSocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenderService {

    private final GenderRepository genderRepository;
    private final GenderMapper genderMapper;
    private final WebSocketController webSocketController;


    public List<GenderResponse> getAllGenders() {
        return genderRepository.findAll()
                .stream()
                .map(genderMapper::toDto)
                .collect(Collectors.toList());
    }


    public GenderResponse getGenderById(Long id) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + id));
        return genderMapper.toDto(gender);
    }


    public GenderResponse createGender(GenderRequest request) {
        String name = request.getName().trim().toLowerCase();
        if(genderRepository.existsByName(name)){
            throw new DuplicateResourceException("Name is already in use");
        }
        Gender gender = genderMapper.toEntity(request);
        gender.setName(name);
        genderRepository.save(gender);

        webSocketController.sendEvent("GENDER_CREATED", genderMapper.toDto(gender));
        return genderMapper.toDto(gender);
    }


    public GenderResponse updateGender(Long id, GenderRequest request) {
        String newName = request.getName().trim().toLowerCase();
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + id));

        if(!newName.equals(gender.getName())){
            if(genderRepository.existsByName(newName)){
                throw new DuplicateResourceException("Name is already in use");
            }
        }

        genderMapper.update(request, gender);
        gender.setName(newName);
        genderRepository.save(gender);

        webSocketController.sendEvent("GENDER_UPDATED", genderMapper.toDto(gender));
        return genderMapper.toDto(gender);
    }


    public void deleteGender(Long id) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + id));
        genderRepository.delete(gender);
        webSocketController.sendEvent("GENDER_DELETED", genderMapper.toDto(gender));
    }
}