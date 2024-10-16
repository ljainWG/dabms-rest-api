package com.wg.dabms.notification;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "Notifications") // Specify the table name in the database
public class Notification {

	@Id
	@Column(name = "notification_uuid", nullable = false, length = 255, unique = true)
	private String notificationUuid;

	@Column(name = "notification_generator_uuid", nullable = false, length = 255)
	private String notificationGeneratorUuid;

	@Column(name = "notification_receiver_uuid", nullable = false, length = 255)
	private String notificationReceiverUuid;

	@Column(name = "notification_title")
	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Notification title should contain only alphanumeric characters, spaces, and punctuation.")
	@Size(min = 10, message = "Notification title must be at least 10 characters long.")
	private String notificationTitle;

	@Column(name = "notification_description")
	@Pattern(regexp = "^[a-zA-Z0-9 ,.]+$", message = "Notification description should contain only alphanumeric characters, spaces, and punctuation.")
	@Size(min = 10, message = "Notification description must be at least 10 characters long.")
	private String notificationDescription;

	@Column(name = "notification_generated_at", nullable = false)
	private LocalDateTime notificationGeneratedAt;

	@Column(name = "notification_updated_at", nullable = false)
	private LocalDateTime notificationUpdatedAt;

}