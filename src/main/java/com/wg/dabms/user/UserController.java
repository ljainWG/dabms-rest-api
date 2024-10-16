package com.wg.dabms.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wg.dabms.api_response.ApiResponseEnvelopeHandler;
import com.wg.dabms.api_response.ApiResponseStatus;

import jakarta.validation.Valid;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
	//	System.out.println("flow reached");
		List<User> users = userService.findAll();
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable String userId) {
		System.out.println("flow reached ctrl");
		User user = userService.findById(userId);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/users")
	public ResponseEntity<Object> createNewUser(@Valid @RequestBody User user) {
		User userCreated = userService.createUser(user);
		return ApiResponseEnvelopeHandler.buildResponse(ApiResponseStatus.SUCCESS, HttpStatus.CREATED,
				"USER CREATED SUCCESSFULLY", userCreated);
	}
	
	@PutMapping("/user/{userId}")
	public ResponseEntity<Object> updateUserById(@PathVariable String userId, @Valid @RequestBody User user){
		User userToBeUpdated = userService.updateUserById(userId, user);
		return ApiResponseEnvelopeHandler.buildResponse(ApiResponseStatus.SUCCESS, HttpStatus.OK, "USER UPDATED SUCCESSFULLY", userToBeUpdated);
	}
	
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<Object> deleteUserById(@PathVariable String userId){
		User userToBeDeleted = userService.deleteUserById(userId);
		return ApiResponseEnvelopeHandler.buildResponse(ApiResponseStatus.SUCCESS, HttpStatus.OK, "USER DELETED SUCCESSFULLY", userToBeDeleted);
	}
	
}
