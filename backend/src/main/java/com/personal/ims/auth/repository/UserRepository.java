package com.personal.ims.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
