package com.wg.dabms.user2;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.enums.UserDepartment;
import com.wg.dabms.enums.UserGender;
import com.wg.dabms.enums.UserRole;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;

@RestController
public class User2Controller {

    @Autowired
    private User2Service user2Service;

    @GetMapping("/v2/users")
    public ResponseEnvelopeWithPagination2 getAllUsers(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userRealName,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) UserGender userGender,
            @RequestParam(required = false) UserRole userRole,
            @RequestParam(required = false) UserDepartment userDepartment,
            @RequestParam(required = false) String userPhoneNo,
            @RequestParam(required = false) LocalDate userDOB,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User2DTO> usersPage = user2Service.getAllUsers(userName, userRealName, userEmail,
                userGender, userRole, userDepartment, userPhoneNo, userDOB, pageable);
        
        return ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("Users retrieved successfully")
                .data(usersPage.getContent())
                .currentPageNo(usersPage.getNumber())
                .totalNoOfRecords((int) usersPage.getTotalElements())
                .totalNoOfPages(usersPage.getTotalPages())
                .recordsPerPage(usersPage.getSize())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/api/auth/v2/users")
    public ResponseEntity<ResponseEnvelopeWithPagination2> createUser(@RequestBody CreateUser2DTO createUser2DTO) {
        
    	ResponseEnvelopeWithPagination2 response = user2Service.createUser(createUser2DTO); 

//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ResponseEnvelopeWithPagination2.builder()
//                        .status(ApiResponseStatus.SUCCESS)
//                        .message("User created successfully.")
//                        .data(createUser2DTO) 
//                        .timeStamp(LocalDateTime.now())
//                        .build());
    	return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PatchMapping("/v2/user/{userId}")
    public ResponseEntity<ResponseEnvelopeWithPagination2> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUser2DTO updateUser2DTO) {

        ResponseEnvelopeWithPagination2 response = user2Service.updateUser(userId, updateUser2DTO);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @DeleteMapping("/v2/user/{userId}")
    public ResponseEntity<ResponseEnvelopeWithPagination2> deleteUser(@PathVariable String userId) {
        
        user2Service.deleteUserById(userId);

        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("User deleted successfully.")
                .timeStamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response); 
    }
}
