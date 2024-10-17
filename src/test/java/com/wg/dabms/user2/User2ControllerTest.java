package com.wg.dabms.user2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.enums.UserDepartment;
import com.wg.dabms.enums.UserGender;
import com.wg.dabms.enums.UserRole;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;
import com.wg.dabms.newexceptions.UserNotFoundException2;

//@WebMvcTest(User2Controller.class)
public class User2ControllerTest {

	@Mock
    private User2Service user2Service;

    @InjectMocks
    private User2Controller user2Controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper; // Declare ObjectMapper

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        objectMapper = new ObjectMapper(); // Initialize ObjectMapper
        mockMvc = MockMvcBuilders.standaloneSetup(user2Controller).build(); // Set up MockMvc with the controller
    }

//    @Test
//    public void testGetAllUsers() throws Exception {
//        User2DTO user1 = User2DTO.builder()
//                .userUuid("1")
//                .userName("johndoe")
//                .userRealName("John Doe")
//                .userEmail("john@example.com")
//                .userPassword("Johndoe@1")
//                .userGender(UserGender.MALE)
//                .userRole(UserRole.ADMIN)
//                .userDepartment(UserDepartment.ADMINISTRATION)
//                .userPhoneNo("1234567890")
//                .userAddress("123 Main St")
//                .userAvgRating(null)
//                .userNoOfReview(null)
//                .userExperience(null)
//                .userDOB(LocalDate.of(1990, 1, 1))
//                .userCreatedAt(LocalDateTime.now())
//                .userUpdatedAt(LocalDateTime.now())
//                .build();
//
//        User2DTO user2 = User2DTO.builder()
//                .userUuid("2")
//                .userName("janedoe")
//                .userRealName("Jane Doe")
//                .userEmail("jane@example.com")
//                .userPassword("Janedoe@1")
//                .userGender(UserGender.FEMALE)
//                .userRole(UserRole.DOCTOR)
//                .userDepartment(UserDepartment.CARDIOLOGY)
//                .userPhoneNo("0987654321")
//                .userAddress("456 Main St")
//                .userAvgRating(null)
//                .userNoOfReview(null)
//                .userExperience(null)
//                .userDOB(LocalDate.of(1995, 5, 15))
//                .userCreatedAt(LocalDateTime.now())
//                .userUpdatedAt(LocalDateTime.now())
//                .build();
//
//        when(user2Service.getAllUsers(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(Arrays.asList(user1, user2)));
//
//        mockMvc.perform(get("/v2/users?page=0&size=10")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.length()").value(2));
//    }

    @Test
    public void testCreateUser_ValidData() throws Exception {
        CreateUser2DTO createUser2DTO = CreateUser2DTO.builder()
                .userName("johndoe")
                .userRealName("John Doe")
                .userPassword("Password123!")
                .userEmail("john@gmail.com")
                .userGender(UserGender.MALE)
                .userRole(UserRole.ADMIN)
                .userDepartment(UserDepartment.ADMINISTRATION)
                .userPhoneNo("1234567890")
                .userAddress("123 Main St")
//                .userDOB(LocalDate.now())
                .build();

        User2DTO createdUser = User2DTO.builder()
                .userUuid("1")
                .userName("johndoe")
                .userRealName("John Doe")
                .userPassword("Password123!")
                .userEmail("john@gmail.com")
                .userGender(UserGender.MALE)
                .userRole(UserRole.ADMIN)
                .userDepartment(UserDepartment.ADMINISTRATION)
                .userPhoneNo("1234567890")
                .userAddress("123 Main St")
                .userExperience(null)
                .userAvgRating(null)
                .userNoOfReview(null)
//                .userDOB(LocalDate.now())
                .userCreatedAt(LocalDateTime.now())
                .userUpdatedAt(LocalDateTime.now())
                .build();

        when(user2Service.createUser(createUser2DTO)).thenReturn(ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("User created successfully.")
                .data(createdUser)
                .timeStamp(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/auth/v2/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUser2DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User created successfully."));
    }

    @Test
    public void testUpdateUser_ValidData() throws Exception {
        // Prepare the UpdateUser2DTO object with test data
        UpdateUser2DTO updateUser2DTO = UpdateUser2DTO.builder()
                .userRealName("Updated Name")
                .userEmail("updated@gmail.com")
                .userPhoneNo("0987654321")
                .userAddress("456 Main St")
                .userGender(UserGender.FEMALE)
                .userExperience(6)
//                .userDOB(LocalDate.of(1990, 1, 1))
                .build();

        // Prepare the expected response from the service
        ResponseEnvelopeWithPagination2 expectedResponse = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("User updated successfully.")
                .timeStamp(LocalDateTime.now())
                .build();

        // Mock the service call
        when(user2Service.updateUser(anyString(), any(UpdateUser2DTO.class))).thenReturn(expectedResponse);

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/v2/user/{userId}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser2DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User updated successfully."));
    }


    @Test
    public void testUpdateUser_UserNotFound() throws Exception {
        // Prepare the UpdateUser2DTO object with test data
        UpdateUser2DTO updateUser2DTO = UpdateUser2DTO.builder()
                .userRealName("Updated Name")
                .userEmail("updated@gmail.com")
                .userPhoneNo("0987654321")
                .userAddress("456 Main St")
                .userGender(UserGender.FEMALE)
                .userExperience(6)
                .build();

        // Mock the service call to throw an exception
        when(user2Service.updateUser(eq("1"), any(UpdateUser2DTO.class)))
                .thenThrow(new UserNotFoundException2("User not found with ID: 1"));

        // Perform the PATCH request and verify the response
        mockMvc.perform(patch("/v2/user/{userId}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser2DTO)))
                .andExpect(status().isNotFound()) // Check for 404 status
                .andExpect(jsonPath("$.status").value("ERROR")) // Check for expected status
                .andExpect(jsonPath("$.message").value("User not found with ID: 1")) // Check for expected message
                .andExpect(jsonPath("$.error").value("User Not Found")); // Check for expected error type
    }

    
//    @Test
//    public void testDeleteUser() throws Exception {
//        doNothing().when(user2Service).deleteUserById("1"); // Use doNothing for void methods
//
//        mockMvc.perform(delete("/v2/user/{userId}", "1").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("SUCCESS"))
//                .andExpect(jsonPath("$.message").value("User deleted successfully."));
//    }


}
