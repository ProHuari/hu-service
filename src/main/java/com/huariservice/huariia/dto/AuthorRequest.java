package com.huariservice.huariia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * Incoming payload used to create or update an {@link com.huariservice.huariia.entities.Author}.
 *
 * <p>Bean Validation annotations are evaluated by Spring when the controller parameter is
 * annotated with {@code @Valid}; any violation results in an HTTP 400 response.</p>
 *
 * @param channelName public channel name (required, max 100 chars, must be unique)
 * @param channelUrl  absolute URL of the channel (required, valid URL, max 254 chars, must be unique)
 */
public record AuthorRequest(
        @NotBlank(message = "Channel name is required")
        @Size(max = 100, message = "Channel name must be at most 100 characters")
        String channelName,

        @NotBlank(message = "Channel URL is required")
        @Size(max = 254, message = "Channel URL must be at most 254 characters")
        @URL(message = "Invalid channel URL")
        String channelUrl
) {}
