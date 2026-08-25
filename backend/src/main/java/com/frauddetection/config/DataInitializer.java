package com.frauddetection.config;

import com.frauddetection.entity.Role;
import com.frauddetection.entity.User;
import com.frauddetection.repository.RoleRepository;
import com.frauddetection.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {

        /*
         * Create ADMIN role
         */
        Role adminRole =
                roleRepository
                        .findByRoleName("ADMIN")
                        .orElseGet(() ->
                                roleRepository.save(
                                        new Role(
                                                null,
                                                "ADMIN"
                                        )
                                )
                        );


        /*
         * Create USER role
         */
        if (roleRepository
                .findByRoleName("USER")
                .isEmpty()) {

            roleRepository.save(
                    new Role(
                            null,
                            "USER"
                    )
            );
        }


        /*
         * Create default ADMIN user
         */
        if (!userRepository.existsByEmail(
                "admin@frauddetection.com"
        )) {

            User admin = new User();

            admin.setFullName(
                    "System Administrator"
            );

            admin.setEmail(
                    "admin@frauddetection.com"
            );

            admin.setPassword(
                    passwordEncoder.encode(
                            "Admin@123"
                    )
            );

            admin.setCreatedAt(
                    LocalDateTime.now()
            );

            admin.setRole(
                    adminRole
            );

            userRepository.save(admin);

            System.out.println(
                    "ADMIN user created successfully"
            );
        }
    }
}