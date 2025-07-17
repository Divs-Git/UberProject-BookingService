package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.dtos.CreateBookingDto;
import com.example.uberprojectbookingservice.dtos.CreateBookingResponseDto;
import com.example.uberprojectentityservice.models.Booking;

public interface BookingService {
    CreateBookingResponseDto createBooking(CreateBookingDto booking);
}
