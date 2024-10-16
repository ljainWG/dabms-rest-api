package com.wg.dabms.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.wg.dabms.enums.UserDepartment;
import com.wg.dabms.enums.UserGender;
import com.wg.dabms.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "Users") // Specifies the table name in the database
public class User {

    @Id
    @Column(name = "user_uuid", nullable = false, length = 255, unique = true)
    private String userUuid;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, message = "Username must be at least 5 characters")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+=]*$", message = "Username must contain alphanumeric characters and allowed special symbols")
    @Column(name = "user_name", nullable = false, length = 255)
    private String userName;

    @NotBlank(message = "Real name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Real name must contain only alphabetic characters and spaces")
    @Column(name = "user_real_name", nullable = false, length = 255)
    private String userRealName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must be at least 8 characters long and contain upper and lower case letters, numbers, and special characters.")
     // regex is causing some issue due to regex not able to creat or update user please look into this
    @Column(name = "user_password", nullable = false, length = 255)
    private String userPassword;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Pattern(regexp = ".+@(gmail\\.com|yahoo\\.com|watchguard\\.com)$", message = "Email must have a valid domain")
    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_gender", nullable = false, length = 20)
    private UserGender userGender;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_department", nullable = false, length = 20)
    private UserDepartment userDepartment;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, max = 10, message = "Phone number must be 10 numbers")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    @Column(name = "user_phone_no", nullable = false, length = 10)
    private String userPhoneNo;

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, message = "Address must be at least 5 characters")
    @Pattern(regexp = "^(?=.*[a-zA-Z].{5,})[a-zA-Z0-9\\s,]*$", message = "Address must have at least 5 alphabetic characters and may include numbers and spaces")
    @Column(name = "user_address", nullable = false, columnDefinition = "TEXT")
    private String userAddress;

    @Column(name = "user_avg_rating")
    private Integer userAvgRating; // Only for doctors

    @Column(name = "user_no_of_review")
    private Integer userNoOfReview; // for patients no of reviews given by him	// for doctors no of reviews given to him

    @Column(name = "user_experience")
    private Integer userExperience; // Only for doctors

    @Past(message = "Date of birth must be in the past")
    @Column(name = "user_dob", nullable = false)
    private LocalDate userDOB;

    @Column(name = "user_created_at")
    private LocalDateTime userCreatedAt;

    @Column(name = "user_updated_at")
    private LocalDateTime userUpdatedAt;
       
    public User() {
    }
	public User(String userUuid, String userName, String userRealName, String userPassword, String userEmail,
			UserGender userGender, UserRole userRole, UserDepartment userDepartment, String userPhoneNo,
			String userAddress, Integer userAvgRating, Integer userNoOfReview, Integer userExperience,
			LocalDate userDOB, LocalDateTime userCreatedAt, LocalDateTime userUpdatedAt) {
		this.userUuid = userUuid;
		this.userName = userName;
		this.userRealName = userRealName;
		this.userPassword = userPassword;
		this.userEmail = userEmail;
		this.userGender = userGender;
		this.userRole = userRole;
		this.userDepartment = userDepartment;
		this.userPhoneNo = userPhoneNo;
		this.userAddress = userAddress;
		this.userAvgRating = userAvgRating;
		this.userNoOfReview = userNoOfReview;
		this.userExperience = userExperience;
		this.userDOB = userDOB;
		this.userCreatedAt = userCreatedAt;
		this.userUpdatedAt = userUpdatedAt;
	}
}
