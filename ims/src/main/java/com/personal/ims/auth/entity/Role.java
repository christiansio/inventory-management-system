package com.personal.ims.auth.entity;

import com.personal.ims.auth.entity.enums.RoleClassification;
import com.personal.ims.core.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleClassification classification;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

}
