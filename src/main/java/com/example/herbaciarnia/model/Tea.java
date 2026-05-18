package com.example.herbaciarnia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tea {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String origin;
    private String type;
    private Integer temperatureCelsius;
    private Integer steepingTimeSeconds;
    private String notes;
}
