package com.example.StudentManagement.controller;

import com.example.StudentManagement.dto.request.GenderRequest;
import com.example.StudentManagement.dto.response.ApiResponse;
import com.example.StudentManagement.dto.response.GenderResponse;
import com.example.StudentManagement.service.GenderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/genders")
@RequiredArgsConstructor
public class GenderController extends BaseController {

    private final GenderService genderService;

    // GET all

    @GetMapping
    public ResponseEntity<?> getAllGenders() {
        List<GenderResponse> genders = genderService.getAllGenders();
        return ok(genders, "All genders fetched successfully");
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getGenderById(@PathVariable Long id) {
        GenderResponse gender = genderService.getGenderById(id);
        return ok(gender, "Gender fetched successfully");
    }

    // POST create
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<GenderResponse>> createGender(
            @Valid @RequestBody GenderRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        GenderResponse created = genderService.createGender(request);

        // Build the URI dynamically
        URI location = uriBuilder
                .path("/api/genders/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return created(created, "Gender created successfully", location);
    }

    // PUT update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGender(@PathVariable Long id, @Valid @RequestBody GenderRequest request) {
        GenderResponse updated = genderService.updateGender(id, request);
        return ok(updated, "Gender updated successfully");
    }

    // DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGender(@PathVariable Long id) {
        genderService.deleteGender(id);
        return ok((Void)null, "Gender deleted successfully");
    }
}