package com.example.demo.document.dto;

import lombok.Builder;
import lombok.Value;

@Value 
@Builder 
public class DocumentDTO {
    Long id;      
    String title; 
    String content; 
}
