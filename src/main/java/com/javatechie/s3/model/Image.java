package com.javatechie.s3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uniqueid")
    private String uniqueId;
    private String name;
    private String url;
    private String size;

    @Column(name = "createddate")
    private LocalDateTime createdDate;
    @Column(name = "lastupdateddate")
    private LocalDateTime lastUpdatedDate;
}

