package com.example.uberprojectbookingservice.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearbyDriverRequestDto {

    Double latitude;

    Double longitude;
}
