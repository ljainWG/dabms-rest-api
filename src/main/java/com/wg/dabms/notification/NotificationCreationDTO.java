package com.wg.dabms.notification;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationCreationDTO {

	@Size(min=16, max=16)
	private String notificationReceiverUuid;

	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Notification title should contain only alphanumeric characters, spaces, and punctuation.")
	@Size(min = 10, message = "Notification title must be at least 10 characters long.")
	private String notificationTitle;

	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Notification description should contain only alphanumeric characters, spaces, and punctuation.")
	@Size(min = 10, message = "Notification description must be at least 10 characters long.")
	private String notificationDescription;

}
