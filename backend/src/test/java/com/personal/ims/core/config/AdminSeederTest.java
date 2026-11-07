package com.personal.ims.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.personal.ims.auth.enums.PermissionCode;
import com.personal.ims.auth.repository.LocationRepository;
import com.personal.ims.auth.repository.RolePermissionRepository;
import com.personal.ims.auth.repository.RoleRepository;
import com.personal.ims.auth.repository.UserRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(args = "--seed-admin")
class AdminSeederTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

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

    @Test
    @Transactional
    void createsAdminWithConfiguredCredentials() {

        var admin = userRepository.findByEmail(adminEmail).orElseThrow();

        Set<PermissionCode> permissionCodes = rolePermissionRepository.findByRole(admin.getRole())
                .stream()
                .map(permission -> permission.getCode())
                .collect(Collectors.toSet());

        assertThat(admin.getFirstName()).isEqualTo(adminFirstName);
        assertThat(admin.getLastName()).isEqualTo(adminLastName);
        assertThat(admin.getRole().getName()).isEqualTo("ADMIN");
        assertThat(admin.getLocation().getName()).isEqualTo(adminLocation);
        assertThat(admin.getLocation().getAddress()).isEqualTo(adminLocationAddress);
        assertThat(permissionCodes).containsExactlyInAnyOrder(
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

    }
}
