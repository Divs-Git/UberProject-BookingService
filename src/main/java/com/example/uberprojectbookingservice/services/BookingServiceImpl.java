package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.dtos.CreateBookingDto;
import com.example.uberprojectbookingservice.dtos.CreateBookingResponseDto;
import com.example.uberprojectbookingservice.dtos.DriverLocationDto;
import com.example.uberprojectbookingservice.dtos.NearbyDriverRequestDto;
import com.example.uberprojectbookingservice.repositories.BookingRepository;
import com.example.uberprojectbookingservice.repositories.PassengerRepository;
import com.example.uberprojectentityservice.models.Booking;
import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Passenger;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{


    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;

    private final RestTemplate restTemplate;

    private static final String LOCATION_SERVICE = "http://localhost:7777";

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails) {
        Passenger passenger = passengerRepository.findById(bookingDetails.getPassengerId()).orElseThrow(() -> new EntityNotFoundException("Passnger not found"));
        Booking booking = Booking.builder()
                                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                                .startLocation(bookingDetails.getStartLocation())
                                .dropLocation(bookingDetails.getEndLocation())
                                .passenger(passenger)
                                .build();

        Booking newBooking = bookingRepository.save(booking);

        // make an api to location service to fetch nearby driver
        NearbyDriverRequestDto request = NearbyDriverRequestDto
                                            .builder()
                                            .latitude(bookingDetails.getStartLocation().getLatitude())
                                            .longitude(bookingDetails.getStartLocation().getLongitude())
                                            .build();

        ResponseEntity<DriverLocationDto[]> results =  restTemplate.postForEntity(LOCATION_SERVICE + "/api/v1/location/nearby/drivers",request,DriverLocationDto[].class);
        if (results.getStatusCode().is2xxSuccessful() && results.getBody() != null) {
            List<DriverLocationDto> driverLocations = Arrays.asList(results.getBody());

            driverLocations.forEach(driverLocationDto -> {
                System.out.println(driverLocationDto.getDriverId() + " " + driverLocationDto.getLatitude() + " " + driverLocationDto.getLongitude());
            });
        }

        return CreateBookingResponseDto.builder()
                    .bookingId(newBooking.getId())
                    .bookingStatus(newBooking.getBookingStatus().toString())
//                    .driver(Optional.of(newBooking.getDriver()))
                    .build();
    }
}
