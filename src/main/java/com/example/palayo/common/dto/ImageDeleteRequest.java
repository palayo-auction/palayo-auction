package com.example.palayo.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ImageDeleteRequest {
    private List<String> imageUrls;

}

