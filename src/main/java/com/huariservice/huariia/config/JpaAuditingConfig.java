package com.huariservice.huariia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA auditing for {@code @CreatedDate} and {@code @LastModifiedDate} fields.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
