package com.example.uberprojectbookingservice.dtos;

import com.example.uberprojectentityservice.models.BookingStatus;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingDto {

    private BookingStatus status;

    private Optional<Long> driverId;
}
