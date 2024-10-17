package com.wg.dabms.user2;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.enums.UserDepartment;
import com.wg.dabms.enums.UserGender;
import com.wg.dabms.enums.UserRole;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;
import com.wg.dabms.newexceptions.UnauthorizedAccessException;
import com.wg.dabms.newexceptions.UserAlreadyExistsException2;
import com.wg.dabms.newexceptions.UserNotFoundException2;

@Service
public class User2Service {

	@Autowired
	private User2Repository user2Repository;

	// Get all users based on search criteria
	public Page<User2DTO> getAllUsers(String userName, String userRealName, String userEmail, UserGender userGender,
			UserRole userRole, UserDepartment userDepartment, String userPhoneNo, LocalDate userDOB,
			Pageable pageable) {

		User2DTO currentUser = getCurrentUser();
		checkAdminOrReceptionistAccess(currentUser);

		return user2Repository.searchUsers(userName, userRealName, userEmail, userGender, userRole, userDepartment,
				userPhoneNo, userDOB, pageable);
	}

	// Create a new user
	public ResponseEnvelopeWithPagination2 createUser(CreateUser2DTO createUser2DTO) {
		checkForDuplicateFields(createUser2DTO);

		String newUserUuid = generateUniqueUserUuid();

		// Create the new user
		User2DTO newUser = buildNewUser(createUser2DTO, newUserUuid);
		user2Repository.save(newUser);

		return buildSuccessResponse("User created successfully.", newUser);
	}

	// Update an existing user
	public ResponseEnvelopeWithPagination2 updateUser(String userId, UpdateUser2DTO updateUser2DTO) {
		User2DTO existingUser = user2Repository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException2("User not found with ID: " + userId));

		User2DTO currentUser = getCurrentUser();
		checkUpdateAuthorization(currentUser, existingUser);

		checkForDuplicateFields(updateUser2DTO, existingUser);
		updateUserDetails(existingUser, updateUser2DTO);

		user2Repository.save(existingUser);

		return buildSuccessResponse("User updated successfully.", existingUser);
	}

	// Delete a user by ID
	public void deleteUserById(String userId) {
		User2DTO userToDelete = user2Repository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException2("User not found with ID: " + userId));

		User2DTO currentUser = getCurrentUser();
		checkDeleteAuthorization(currentUser, userToDelete);

		user2Repository.delete(userToDelete);
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	 														Helper methods

	// Role check methods
	public boolean isAdmin(User2DTO user) {
	    return user.getUserRole() == UserRole.ADMIN;
	}

	public boolean isReceptionist(User2DTO user) {
	    return user.getUserRole() == UserRole.RECEPTIONIST;
	}

	public boolean isCurrentUser(User2DTO currentUser, User2DTO user) {
	    return user.getUserUuid().equals(currentUser.getUserUuid());
	}

	public boolean isDoctor(User2DTO user) {
	    return user.getUserRole() == UserRole.DOCTOR; // Assuming DOCTOR is a role in UserRole enum
	}

	public boolean isPatient(User2DTO user) {
	    return user.getUserRole() == UserRole.PATIENT; // Assuming PATIENT is a role in UserRole enum
	}

	// Modified authorization checks
	public void checkAdminOrReceptionistAccess(User2DTO currentUser) {
	    if (!isAdmin(currentUser) && !isReceptionist(currentUser)) {
	        throw new UnauthorizedAccessException("You are not authorized to access this endpoint.");
	    }
	}

	public void checkUpdateAuthorization(User2DTO currentUser, User2DTO existingUser) {
	    if (!isAdmin(currentUser) && !isCurrentUser(currentUser, existingUser)) {
	        throw new UnauthorizedAccessException("You are not authorized to update this user.");
	    }
	}

	public void checkDeleteAuthorization(User2DTO currentUser, User2DTO userToDelete) {
	    if (!isCurrentUser(currentUser, userToDelete) && !isAdmin(currentUser)) {
	        throw new UnauthorizedAccessException("You do not have permission to delete this user.");
	    }
	}

	// Get the current authenticated user
	public User2DTO getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			return user2Repository.findByUserName(username)
					.orElseThrow(() -> new UserNotFoundException2("User not found: " + username));
		}
		throw new UnauthorizedAccessException("User is not authenticated.");
	}

	// Build a success response
	private ResponseEnvelopeWithPagination2 buildSuccessResponse(String message, User2DTO data) {
		return ResponseEnvelopeWithPagination2.builder().status(ApiResponseStatus.SUCCESS).message(message).data(data)
				.timeStamp(LocalDateTime.now()).build();
	}

	// Build a new user from CreateUser2DTO
	private User2DTO buildNewUser(CreateUser2DTO createUser2DTO, String userUuid) {
		return User2DTO.builder().userUuid(userUuid).userName(createUser2DTO.getUserName())
				.userRealName(createUser2DTO.getUserRealName())
				.userPassword(hashPassword(createUser2DTO.getUserPassword())).userEmail(createUser2DTO.getUserEmail())
				.userGender(createUser2DTO.getUserGender()).userRole(createUser2DTO.getUserRole())
				.userDepartment(createUser2DTO.getUserDepartment()).userPhoneNo(createUser2DTO.getUserPhoneNo())
				.userAddress(createUser2DTO.getUserAddress()).userExperience(createUser2DTO.getUserExperience())
				.userDOB(createUser2DTO.getUserDOB()).userCreatedAt(LocalDateTime.now())
				.userUpdatedAt(LocalDateTime.now()).build();
	}

	// Update user details
	private void updateUserDetails(User2DTO existingUser, UpdateUser2DTO updateUser2DTO) {
		if (updateUser2DTO.getUserName() != null) {
			existingUser.setUserName(updateUser2DTO.getUserName());
		}
		if (updateUser2DTO.getUserRealName() != null) {
			existingUser.setUserRealName(updateUser2DTO.getUserRealName());
		}
		if (updateUser2DTO.getUserPassword() != null) {
			existingUser.setUserPassword(hashPassword(updateUser2DTO.getUserPassword()));
		}
		if (updateUser2DTO.getUserEmail() != null) {
			existingUser.setUserEmail(updateUser2DTO.getUserEmail());
		}
		if (updateUser2DTO.getUserGender() != null) {
			existingUser.setUserGender(updateUser2DTO.getUserGender());
		}
		if (updateUser2DTO.getUserPhoneNo() != null) {
			existingUser.setUserPhoneNo(updateUser2DTO.getUserPhoneNo());
		}
		if (updateUser2DTO.getUserAddress() != null) {
			existingUser.setUserAddress(updateUser2DTO.getUserAddress());
		}
		if (updateUser2DTO.getUserExperience() != null) {
			existingUser.setUserExperience(updateUser2DTO.getUserExperience());
		}
		if (updateUser2DTO.getUserDOB() != null) {
			existingUser.setUserDOB(updateUser2DTO.getUserDOB());
		}
	}

	// Check for duplicate fields when creating a user
	private void checkForDuplicateFields(CreateUser2DTO createUser2DTO) {
		if (user2Repository.existsByUserName(createUser2DTO.getUserName())) {
			throw new UserAlreadyExistsException2("Username already exists.");
		}
		if (user2Repository.existsByUserEmail(createUser2DTO.getUserEmail())) {
			throw new UserAlreadyExistsException2("Email already exists.");
		}
		if (user2Repository.existsByUserPhoneNo(createUser2DTO.getUserPhoneNo())) {
			throw new UserAlreadyExistsException2("Phone number already exists.");
		}
	}

	// Check for duplicate fields when updating a user
	private void checkForDuplicateFields(UpdateUser2DTO updateUser2DTO, User2DTO existingUser) {
		List<String> duplicateFields = new ArrayList<>();

		if (updateUser2DTO.getUserName() != null && !updateUser2DTO.getUserName().equals(existingUser.getUserName())
				&& user2Repository.existsByUserName(updateUser2DTO.getUserName())) {
			duplicateFields.add("Username");
		}
		if (updateUser2DTO.getUserEmail() != null && !updateUser2DTO.getUserEmail().equals(existingUser.getUserEmail())
				&& user2Repository.existsByUserEmail(updateUser2DTO.getUserEmail())) {
			duplicateFields.add("Email");
		}
		if (updateUser2DTO.getUserPhoneNo() != null
				&& !updateUser2DTO.getUserPhoneNo().equals(existingUser.getUserPhoneNo())
				&& user2Repository.existsByUserPhoneNo(updateUser2DTO.getUserPhoneNo())) {
			duplicateFields.add("Phone Number");
		}

		if (!duplicateFields.isEmpty()) {
			String errorMessage = "The following fields already exist in the database: "
					+ String.join(", ", duplicateFields);
			throw new UserAlreadyExistsException2(errorMessage);
		}
	}

	// Password handling
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public String hashPassword(String plainPassword) {
		return passwordEncoder.encode(plainPassword);
	}

	public boolean checkPassword(String plainPassword, String hashedPassword) {
		return passwordEncoder.matches(plainPassword, hashedPassword);
	}

	// Generate a random string of given length
	private static final SecureRandom random = new SecureRandom();

	public static String generateRandomString(int length) {
		byte[] randomBytes = new byte[length];
		random.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
	}

	// Generate a unique UUID
	private String generateUniqueUserUuid() {
		String newUserUuid;
		do {
			newUserUuid = generateRandomString(16);
		} while (user2Repository.existsById(newUserUuid));
		return newUserUuid;
	}
}
