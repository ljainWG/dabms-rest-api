package com.wg.dabms.security;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.envelope.ResponseEnvelope;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;
import com.wg.dabms.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserDetailsService userDetailsService;

	private final AuthenticationManager authenticationManager;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil;

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public ResponseEntity<ResponseEnvelope> login(@RequestBody JwtRequest user) {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
			UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
			String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
			ResponseEnvelope envelope = new ResponseEnvelope(ApiResponseStatus.SUCCESS, "Logged in successfully.", Map.of("jwtToken", jwtToken));
			return ResponseEntity.ok(envelope);
	}
	
	@PostMapping("/user/logout")
	public ResponseEntity<ResponseEnvelopeWithPagination2> logoutUser(@RequestHeader("Authorization") String authorizationHeader) {
	    // Check if the header is present and well-formed
	    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	        ResponseEnvelopeWithPagination2 errorResponse = ResponseEnvelopeWithPagination2.builder()
	                .status(ApiResponseStatus.ERROR)
	                .message("Invalid token")
	                .data(null)
	                .error("Invalid token")
	                .timeStamp(LocalDateTime.now())
	                .build();

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	    }

	    // Extract the JWT token (removing "Bearer " prefix)
	    String jwtToken = authorizationHeader.substring(7);

	    // Add the token to the blacklist
	    jwtUtil.blacklistToken(jwtToken);

	    // Create success response
	    ResponseEnvelopeWithPagination2 successResponse = ResponseEnvelopeWithPagination2.builder()
	            .status(ApiResponseStatus.SUCCESS)
	            .message("Logged out successfully")
	            .data(null)
	            .error(null)
	            .timeStamp(LocalDateTime.now())
	            .build();

	    // Respond with success message
	    return ResponseEntity.status(HttpStatus.OK).body(successResponse);
	}

}