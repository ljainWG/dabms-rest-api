package com.wg.dabms.notification;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;
import com.wg.dabms.exceptions.UserNotFoundException;
import com.wg.dabms.user.UserRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Notification> getNotificationsWithPagination(int page, int size) {
        // Assuming you are using Pageable
        Pageable pageable = PageRequest.of(page - 1, size); // Spring Data uses zero-based indexing
        return notificationRepository.findAll(pageable).getContent();
    }

    public int getTotalNotificationsCount() {
        return (int) notificationRepository.count();
    }
    
    public ResponseEnvelopeWithPagination2 createNotification(NotificationCreationDTO notificationCreationDTO) {
    	
//        // Check if the user has the required role
//        String currentUserRole = getCurrentUserRole(); // Implement this method based on your authentication
//        if (!currentUserRole.equals("ADMIN") && !currentUserRole.equals("RECEPTIONIST")) {
//            return ResponseEnvelopeWithPagination2.builder()
//                    .status(ApiResponseStatus.ERROR)
//                    .message("Access denied")
//                    .data(null)
//                    .error("User does not have permission to create notifications.")
//                    .timeStamp(LocalDateTime.now())
//                    .build();
//        }
    	
    	if (!userRepository.existsById(notificationCreationDTO.getNotificationReceiverUuid())) {
            throw new UserNotFoundException("Receiver of notification does not exist.");
        }

        // Generate a new UUID for the notification
        String notificationUuid = generateRandomString(16); // Assuming this method is defined

        // Create the notification entity
        Notification notification = Notification.builder()
                .notificationUuid(notificationUuid)
                
//                .notificationGeneratorUuid(getCurrentUserUuid()) // Implement this method to get the current user's UUID
//              set the uuid of notification generator here the generator will be the one who is using the api/ software so ask this quesion to pulkit because he has done it  
                
                .notificationReceiverUuid(notificationCreationDTO.getNotificationReceiverUuid())
                .notificationTitle(notificationCreationDTO.getNotificationTitle())
                .notificationDescription(notificationCreationDTO.getNotificationDescription())
                .notificationGeneratedAt(LocalDateTime.now())
                .notificationUpdatedAt(LocalDateTime.now())
                .build();

        // Save the notification
        notificationRepository.save(notification);

        return ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .data(notification)
                .error(null)
                .timeStamp(LocalDateTime.now())
                .build();
    }
    
    public ResponseEnvelopeWithPagination2 updateNotification(String notificationId, NotificationUpdationDTO notificationUpdationDTO) {
        
    	// Check if the user has the required role
//        String currentUserRole = getCurrentUserRole(); // Implement this method based on your authentication
//        if (!currentUserRole.equals("ADMIN") && !currentUserRole.equals("RECEPTIONIST")) {
//            return ResponseEnvelopeWithPagination2.builder()
//                    .status(ApiResponseStatus.ERROR)
//                    .message("Access denied")
//                    .data(null)
//                    .error("User does not have permission to update notifications.")
//                    .timeStamp(LocalDateTime.now())
//                    .build();
//        }

        // Fetch the existing notification
        Notification existingNotification = notificationRepository.findById(notificationId)
                .orElse(null); // Use Optional handling as per your preference

        if (existingNotification == null) {
            return ResponseEnvelopeWithPagination2.builder()
                    .status(ApiResponseStatus.ERROR)
                    .message("Notification not found")
                    .data(null)
                    .error("No notification found with the given ID.")
                    .timeStamp(LocalDateTime.now())
                    .build();
        }

        // Update the notification fields
        existingNotification.setNotificationTitle(notificationUpdationDTO.getNotificationTitle());
        existingNotification.setNotificationDescription(notificationUpdationDTO.getNotificationDescription());
        existingNotification.setNotificationUpdatedAt(LocalDateTime.now());

        // Save the updated notification
        notificationRepository.save(existingNotification);

        return ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .data(existingNotification)
                .error(null)
                .timeStamp(LocalDateTime.now())
                .build();
    }
    
    public ResponseEnvelopeWithPagination2 deleteNotification(String notificationId) {
//      //   Check if the user has the required role
//        String currentUserRole = getCurrentUserRole(); // Implement this method based on your authentication
//        if (!currentUserRole.equals("ADMIN") && !currentUserRole.equals("RECEPTIONIST")) {
//            return ResponseEnvelopeWithPagination2.builder()
//                    .status(ApiResponseStatus.ERROR)
//                    .message("Access denied")
//                    .data(null)
//                    .error("User does not have permission to delete notifications.")
//                    .timeStamp(LocalDateTime.now())
//                    .build();
//        }

        // Check if the notification exists
        if (!notificationRepository.existsById(notificationId)) {
            return ResponseEnvelopeWithPagination2.builder()
                    .status(ApiResponseStatus.ERROR)
                    .message("Notification not found")
                    .data(null)
                    .error("No notification found with the given ID.")
                    .timeStamp(LocalDateTime.now())
                    .build();
        }

        // Delete the notification
        notificationRepository.deleteById(notificationId);

        return ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .message("Notification deleted successfully.")
                .data(null)
                .error(null)
                .timeStamp(LocalDateTime.now())
                .build();
    }
    
    private static final SecureRandom random = new SecureRandom();

	private static String generateRandomString(int length) {
		byte[] randomBytes = new byte[length];
		random.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
	}
    
}
