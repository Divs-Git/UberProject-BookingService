package com.example.uberprojectbookingservice.services;

import com.example.uberprojectbookingservice.apis.LocationServiceApi;
import com.example.uberprojectbookingservice.apis.UberSocketApi;
import com.example.uberprojectbookingservice.dtos.*;
import com.example.uberprojectbookingservice.repositories.BookingRepository;
import com.example.uberprojectbookingservice.repositories.DriverRepository;
import com.example.uberprojectbookingservice.repositories.PassengerRepository;
import com.example.uberprojectentityservice.models.Booking;
import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Driver;
import com.example.uberprojectentityservice.models.Passenger;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{


    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;

    private final RestTemplate restTemplate;

//    private static final String LOCATION_SERVICE = "http://localhost:7777";

    private final LocationServiceApi locationServiceApi;
    private final DriverRepository driverRepository;

    private final UberSocketApi uberSocketApi;

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository, LocationServiceApi locationServiceApi, DriverRepository driverRepository , UberSocketApi uberSocketApi) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = new RestTemplate();
        this.locationServiceApi = locationServiceApi;
        this.driverRepository = driverRepository;
        this.uberSocketApi = uberSocketApi;
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

        processNearbyDriverAsync(request, bookingDetails.getPassengerId(), newBooking.getId());
//
//        ResponseEntity<DriverLocationDto[]> results =  restTemplate.postForEntity(LOCATION_SERVICE + "/api/v1/location/nearby/drivers",request,DriverLocationDto[].class);
//        if (results.getStatusCode().is2xxSuccessful() && results.getBody() != null) {
//            List<DriverLocationDto> driverLocations = Arrays.asList(results.getBody());
//
//            driverLocations.forEach(driverLocationDto -> {
//                System.out.println(driverLocationDto.getDriverId() + " " + driverLocationDto.getLatitude() + " " + driverLocationDto.getLongitude());
//            });
//        }

        return CreateBookingResponseDto.builder()
                    .bookingId(newBooking.getId())
                    .bookingStatus(newBooking.getBookingStatus().toString())
//                    .driver(Optional.of(newBooking.getDriver()))
                    .build();
    }

    @Override
    public UpdateBookingResponseDto updateBooking(UpdateBookingDto bookingDto, Long bookingId) {
        Optional<Driver> driver = driverRepository.findById(bookingDto.getDriverId().get());
            if(driver.isPresent()) {
                bookingRepository.updateBookingStatusAndDriverById(bookingId, bookingDto.getStatus(), driver.get());
                Optional<Booking> booking = bookingRepository.findById(bookingId);
                    return UpdateBookingResponseDto.builder()
                            .bookingId(bookingId)
                            .status(booking.get().getBookingStatus())
                            .driver(Optional.ofNullable(booking.get().getDriver()))
                            .build();
            }

           return null;
    }

    public void processNearbyDriverAsync(NearbyDriverRequestDto requestDto, Long passengerId, Long bookingId) {
        Call<DriverLocationDto[]> call = locationServiceApi.getNearbyDrivers(requestDto);

        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DriverLocationDto> driverLocations = Arrays.asList(response.body());

                    driverLocations.forEach(driverLocationDto -> {
                        System.out.println(driverLocationDto.getDriverId() + " " + driverLocationDto.getLatitude() + " " + driverLocationDto.getLongitude());
                    });

                    raiseRideRequestAysnc(RideRequestDto.builder().passengerId(passengerId).bookingId(bookingId).build());
                } else {
                    System.out.println("Request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void raiseRideRequestAysnc(RideRequestDto requestDto) {
        Call<Boolean> call = uberSocketApi.raiseRideRequest(requestDto);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                assert response.body() != null;
                System.out.println("Driver response is: " + response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

}
