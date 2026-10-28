package com.personal.ims.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.auth.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
}
