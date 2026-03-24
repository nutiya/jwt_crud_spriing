package com.example.StudentManagement.service;

import com.example.StudentManagement.dto.request.StudentRequest;
import com.example.StudentManagement.dto.response.StudentResponse;
import com.example.StudentManagement.entity.Student;
import com.example.StudentManagement.exception.DuplicateResourceException;
import com.example.StudentManagement.exception.ResourceNotFoundException;
import com.example.StudentManagement.mapper.StudentMapper;
import com.example.StudentManagement.repository.GenderRepository;
import com.example.StudentManagement.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final GenderRepository genderRepository;


    public List<StudentResponse> getAllStudents(String sort){
        if(!Set.of("name", "email", "id").contains(sort.toLowerCase())) sort = "id";
        return studentRepository.findAll(Sort.by(sort))
                .stream()
                .map(studentMapper::toDto)
                .toList();
    }

    public StudentResponse getStudentById(Long id){

        var student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        return studentMapper.toDto(student);
    }

    public StudentResponse createStudent(StudentRequest request){
        String name = request.getName().trim();
        String email = request.getEmail().trim().toLowerCase();
        String address = request.getAddress().trim();

        if(studentRepository.existsByEmail(email)){
            throw new DuplicateResourceException("Email is already in use");
        }

        var gender = genderRepository.findById(request.getGenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        var student = studentMapper.toEntity(request);
        student.setName(name);
        student.setEmail(email);
        student.setAddress(address);
        student.setGender(gender);
        studentRepository.save(student);

        return studentMapper.toDto(student);
    }

    public StudentResponse updateStudent(Long id,StudentRequest request){
        String newName = request.getName().trim();
        String newEmail = request.getEmail().trim().toLowerCase();
        String newAddress = request.getAddress().trim();
        var student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        var gender = genderRepository.findById(request.getGenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if(!newEmail.equals(student.getEmail())){
            if(studentRepository.existsByEmail(newEmail)){
                throw new DuplicateResourceException("Email is already in use");
            }
        }

        studentMapper.update(request, student);
        student.setName(newName);
        student.setEmail(newEmail);
        student.setAddress(newAddress);
        student.setGender(gender);
        studentRepository.save(student);


        return studentMapper.toDto(student);
    }



    public void deleteStudent(Long id){
        var student = studentRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
        studentRepository.delete(student);
    }


}
