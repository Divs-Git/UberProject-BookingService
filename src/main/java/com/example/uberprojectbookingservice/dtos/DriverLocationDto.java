package com.example.uberprojectbookingservice.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocationDto {
    String driverId;

    Double latitude;

    Double longitude;
}
