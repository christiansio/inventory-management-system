package com.personal.ims.feature.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.ims.feature.auth.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
