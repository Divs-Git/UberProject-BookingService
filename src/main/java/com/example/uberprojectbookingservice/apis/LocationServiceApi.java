package com.example.uberprojectbookingservice.apis;

import com.example.uberprojectbookingservice.dtos.DriverLocationDto;
import com.example.uberprojectbookingservice.dtos.NearbyDriverRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationServiceApi {

    @POST("/api/v1/location/nearby/drivers")
    Call<DriverLocationDto[]> getNearbyDrivers(@Body NearbyDriverRequestDto requestDto);
}
