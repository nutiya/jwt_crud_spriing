package com.example.StudentManagement.controller;

import com.example.StudentManagement.dto.request.StudentRequest;
import com.example.StudentManagement.dto.response.StudentResponse;
import com.example.StudentManagement.entity.Student;
import com.example.StudentManagement.service.StudentService;
import com.example.StudentManagement.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@AllArgsConstructor
public class StudentController extends BaseController{
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll(
            @RequestParam(required = false, defaultValue = "") String sort
    ){
        var students = studentService.getAllStudents(sort);
        return ok(students, "Students fetched successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Long id){
        var student = studentService.getStudentById(id);
        return ok(student, "Student fetched successfully");
    }

    @PostMapping//@Valid use to check is object pass the validat on each field in entity
    public ResponseEntity<ApiResponse<StudentResponse>> create(
            @Valid @RequestBody StudentRequest request,
            UriComponentsBuilder uriBuilder
    ){

        var student = studentService.createStudent(request);
        var uri = uriBuilder.path("/api/students/{id}").buildAndExpand(student.getId()).toUri();

        return created(student, "Student created successfully",uri);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request){
        var student = studentService.updateStudent(id, request);

        return ok(student, "Student updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){
        studentService.deleteStudent(id);
        return ok(null, "Student deleted successfully");
    }
}

