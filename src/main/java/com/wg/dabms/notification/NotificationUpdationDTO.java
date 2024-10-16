package com.wg.dabms.notification;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationUpdationDTO {

	@Column(name = "notification_title")
	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Notification title should contain only alphanumeric characters, spaces, and punctuation.")
	@Size(min = 10, message = "Notification title must be at least 10 characters long.")
	private String notificationTitle;

	@Column(name = "notification_description")
	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Notification description should contain only alphanumeric characters, spaces, and punctuation.")
	@Size(min = 10, message = "Notification description must be at least 10 characters long.")
	private String notificationDescription;

}
