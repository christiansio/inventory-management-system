package com.personal.ims.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.auth.entity.Role;
import com.personal.ims.auth.enums.RoleClassification;

public interface RoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findById(String roleId);

    public Optional<Role> findByClassification(RoleClassification classification);
}
