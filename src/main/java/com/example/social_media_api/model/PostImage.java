package com.example.social_media_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    private String fileName;

}
