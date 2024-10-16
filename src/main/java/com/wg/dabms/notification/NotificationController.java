package com.wg.dabms.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ResponseEnvelopeWithPagination2> getAllNotifications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

//        // Assuming you have a way to get the current user's roles
//        String currentUserRole = getCurrentUserRole(); // Implement this method based on your authentication
//
//        // Check if the user has the required role
//        if (!currentUserRole.equals("ADMIN") && !currentUserRole.equals("RECEPTIONIST")) {
//            return ResponseEntity.status(403).body(
//                ResponseEnvelopeWithPagination2.builder()
//                    .status(ApiResponseStatus.ERROR)
//                    .message("Access denied")
//                    .data(null)
//                    .error("User does not have permission to access notifications.")
//                    .timeStamp(LocalDateTime.now())
//                    .build()
//            );
//        }

        List<Notification> notifications = notificationService.getNotificationsWithPagination(page, size);
        int totalRecords = notificationService.getTotalNotificationsCount(); // Implement this in your service

        return ResponseEntity.ok(
            ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.SUCCESS)
                .data(notifications)
                .error(null)
                .currentPageNo(page)
                .totalNoOfRecords(totalRecords)
                .totalNoOfPages((int) Math.ceil((double) totalRecords / size)) // Calculate total pages
                .recordsPerPage(size)
                .timeStamp(LocalDateTime.now())
                .build()
        );
    }
    
    @PostMapping
    public ResponseEntity<ResponseEnvelopeWithPagination2> createNotification(
            @Valid @RequestBody NotificationCreationDTO notificationCreationDTO) {

        // Check if the user has the required role and create the notification
        ResponseEnvelopeWithPagination2 response = notificationService.createNotification(notificationCreationDTO);

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{notificationId}")
    public ResponseEntity<ResponseEnvelopeWithPagination2> updateNotification(
            @PathVariable String notificationId,
            @Valid @RequestBody NotificationUpdationDTO notificationUpdationDTO) {

        // Call the service to update the notification
        ResponseEnvelopeWithPagination2 response = notificationService.updateNotification(notificationId, notificationUpdationDTO);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ResponseEnvelopeWithPagination2> deleteNotification(
            @PathVariable String notificationId) {

        // Call the service to delete the notification
        ResponseEnvelopeWithPagination2 response = notificationService.deleteNotification(notificationId);

        return ResponseEntity.ok(response);
    }

}
