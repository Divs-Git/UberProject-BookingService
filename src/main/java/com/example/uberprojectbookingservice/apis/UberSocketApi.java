package com.example.uberprojectbookingservice.apis;

import com.example.uberprojectbookingservice.dtos.DriverLocationDto;
import com.example.uberprojectbookingservice.dtos.NearbyDriverRequestDto;
import com.example.uberprojectbookingservice.dtos.RideRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UberSocketApi {

    @POST("/api/v1/socket/new-ride")
    Call<Boolean> raiseRideRequest(@Body RideRequestDto requestDto);
}
