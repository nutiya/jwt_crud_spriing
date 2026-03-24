package com.example.StudentManagement.controller;

import com.example.StudentManagement.service.ThirdPartyService;
import com.example.StudentManagement.dto.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ThirdPartyController extends BaseController {

    private final ThirdPartyService thirdPartyService;

    // Dynamic number of users
    @GetMapping("/api/external/random-users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRandomUsers(
            @RequestParam(defaultValue = "10") int quantity) {
        List<Map<String, Object>> result = thirdPartyService.getRandomUsers(quantity);
        return ok(result, "Third-party API call successful");
    }

}