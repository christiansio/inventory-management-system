package com.personal.ims.core.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.personal.ims.auth.entity.Location;
import com.personal.ims.auth.entity.Role;
import com.personal.ims.auth.entity.RolePermission;
import com.personal.ims.auth.entity.User;
import com.personal.ims.auth.enums.PermissionCode;
import com.personal.ims.auth.enums.RoleClassification;
import com.personal.ims.auth.repository.LocationRepository;
import com.personal.ims.auth.repository.RolePermissionRepository;
import com.personal.ims.auth.repository.RoleRepository;
import com.personal.ims.auth.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Value("${ADMIN_FIRST_NAME}")
    private String adminFirstName;

    @Value("${ADMIN_LAST_NAME}")
    private String adminLastName;

    @Value("${ADMIN_LOCATION}")
    private String adminLocation;

    @Value("${ADMIN_LOCATION_ADDRESS}")
    private String adminLocationAddress;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!args.containsOption("seed-admin")) {
            return;
        }
        if (roleRepository.findByClassification(RoleClassification.ADMIN).isPresent()) {
            return;
        }

        // Role Provision
        Role role = new Role();

        role.setName("ADMIN");
        role.setClassification(RoleClassification.ADMIN);
        role.setDescription("Provisioned Admin Account");

        Role savedRole = roleRepository.save(role);

        // Initial Role Permission Provision
        List<PermissionCode> adminPermissions = List.of(
                PermissionCode.USER_VIEW,
                PermissionCode.USER_CREATE,
                PermissionCode.USER_EDIT,
                PermissionCode.USER_DELETE,
                PermissionCode.USER_ASSIGN_ROLE,
                PermissionCode.USER_ASSIGN_BRANCH,
                PermissionCode.USER_UPDATE_STATUS,
                PermissionCode.USER_ROLE_CREATE,
                PermissionCode.USER_ROLE_EDIT,
                PermissionCode.USER_ROLE_ASSIGN_PERMISSIONS);

        for (PermissionCode code : adminPermissions) {
            RolePermission rolePermission = new RolePermission();

            rolePermission.setRole(savedRole);
            rolePermission.setCode(code);

            rolePermissionRepository.save(rolePermission);
        }

        // Initial Location Provision
        Location location = new Location();

        location.setName(adminLocation);
        location.setAddress(adminLocationAddress);
        location.setIsActive(true);

        locationRepository.save(location);

        // Initial User Provision
        User user = new User();

        String passwordHash = passwordEncoder.encode(adminPassword);

        user.setEmail(adminEmail);
        user.setPasswordHash(passwordHash);
        user.setFirstName(adminFirstName);
        user.setLastName(adminLastName);
        user.setRole(savedRole);
        user.setLocation(location);
        user.setIsActive(true);

        userRepository.save(user);
    }

}
