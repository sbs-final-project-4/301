package com.yk.Motivation.domain.board.entity;

import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Setter
@Getter
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder
@ToString(callSuper = true)
public class Board extends BaseEntity {
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String code;
    private String icon;
}
