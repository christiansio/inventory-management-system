package com.personal.ims.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.auth.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
