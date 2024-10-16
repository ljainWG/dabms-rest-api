package com.wg.dabms.user;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wg.dabms.exceptions.UserNotFoundException;

import jakarta.validation.Valid;

@Service
public class UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);


	@Autowired
	private UserRepository userRepository;

	public List<User> findAll() {
		List<User> users =  userRepository.findAll();
//		if(users == null || users.isEmpty())
		return users;
			
	}

	public User findById(String id) {
		User user = null;
		System.out.println("flow reached her");
		user = userRepository.findById(id).orElse(null);
		if(user == null) {
			System.out.println("flow reached");
			throw new UserNotFoundException(String.format("User with id %d is not found", id));
		}
		return user;
	}
	
	public User createUser(User user) {
		user.setUserPassword(hashPassword(user.getUserPassword()));
		user.setUserUuid(generateRandomString(16));
		user.setUserCreatedAt(LocalDateTime.now());
		user.setUserUpdatedAt(LocalDateTime.now());
		User savedUser = userRepository.save(user);
		return savedUser;
	}

	public User updateUserById(String userId, @Valid User user) {
		User userToBeUpdated = findById(userId);
		
		userToBeUpdated.setUserUpdatedAt(LocalDateTime.now());
		mapUpdatedUser(userToBeUpdated, user);
		// hashing of new password
		if(user.getUserPassword() != null)
		userToBeUpdated.setUserPassword(hashPassword(user.getUserPassword()));

		userRepository.save(userToBeUpdated);
		return userToBeUpdated;
	}
	
	private void mapUpdatedUser(User userToBeUpdated, @Valid User user) {
		userToBeUpdated.builder()
		.userName(user.getUserName()).userRealName(user.getUserRealName()).userPassword(user.getUserPassword())
		.userEmail(user.getUserEmail()).userGender(user.getUserGender()).userDOB(user.getUserDOB())
		.userPhoneNo(user.getUserPhoneNo()).userAddress(user.getUserAddress())
		.userExperience(user.getUserExperience())
		.build();
	}

	public User deleteUserById(String userId) {
		User userToBeDeleted = findById(userId);
		userRepository.deleteById(userId);
		return userToBeDeleted;
	}

	public static final SecureRandom random = new SecureRandom();

	public static String generateRandomString(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
    }
	
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
    
}
