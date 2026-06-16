package org.individualproject.flashcards.infrastructure.security.config;

import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.RoleJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.entity.RoleEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleJpaRepository roleRepository) {
        return args -> {
            if (!roleRepository.existsByName("ROLE_USER")) {
                roleRepository.saveAndFlush(new RoleEntity(null, "ROLE_USER"));
            }
            if (!roleRepository.existsByName("ROLE_ADMIN")) {
                roleRepository.saveAndFlush(new RoleEntity(null, "ROLE_ADMIN"));
            }
            if (!roleRepository.existsByName("ROLE_GUEST")) {
                roleRepository.saveAndFlush(new RoleEntity(null, "ROLE_GUEST"));
            }
        };
    }
}