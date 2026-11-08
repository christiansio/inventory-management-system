package com.personal.ims.feature.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.feature.auth.entity.Role;
import com.personal.ims.feature.auth.enums.RoleClassification;

public interface RoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findById(String roleId);

    public Optional<Role> findByClassification(RoleClassification classification);
}
