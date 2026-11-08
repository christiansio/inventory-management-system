package com.personal.ims.feature.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.feature.auth.entity.Role;
import com.personal.ims.feature.auth.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRole(Role role);

}
