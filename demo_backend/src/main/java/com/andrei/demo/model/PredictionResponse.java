package com.andrei.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponse {
    private String personName;
    private List<String> skills;
    private String projectType;
    private double confidence;
}
