package com.personal.ims.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.auth.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

}
