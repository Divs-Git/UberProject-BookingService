package com.example.uberprojectbookingservice.controllers;

import com.example.uberprojectbookingservice.dtos.CreateBookingDto;
import com.example.uberprojectbookingservice.dtos.CreateBookingResponseDto;
import com.example.uberprojectbookingservice.dtos.UpdateBookingDto;
import com.example.uberprojectbookingservice.dtos.UpdateBookingResponseDto;
import com.example.uberprojectbookingservice.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> createBooking(@RequestBody CreateBookingDto createBookingDto) {
        return new ResponseEntity<>(bookingService.createBooking(createBookingDto), HttpStatus.CREATED);
    }

    @PostMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(@RequestBody UpdateBookingDto request, @PathVariable Long bookingId) {
        return new ResponseEntity<>(bookingService.updateBooking(request,bookingId),HttpStatus.OK);
    }
}
