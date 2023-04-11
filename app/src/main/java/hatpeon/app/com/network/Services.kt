package com.app.frammanagment.network

import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface Services {
    /*login*/
    /*registration*/
    @POST("reg")
    fun registration(@Body jsonObject: JsonObject): Call<JsonObject>



    @POST("login")
    fun login(@Body jsonObject: JsonObject): Call<JsonObject>

    @GET("banners")
    fun sliderImage(): Call<JsonObject>

    @GET("popular-restaurant")
    fun popularRestaurant(): Call<JsonObject>

    @GET("cuisine")
    fun cuisine(): Call<JsonObject>




/*    *//*foodByCuisine*//*
    //@GET("/devtest/pclb/app-bkash-recurring-payment/{userId}/{packageId}")
    @GET("cuisine/{cuisineId}/show?foodType={foodType}")
    fun restaurantBycuisine(@Query("cuisineId")cuisineId:String, @Query("foodType")foodType:String): Call<JsonObject>*/

    @GET("cuisine/{cuisineId}/show"+"?"+"foodType=foodType")
    fun restaurantBycuisine(
        @Path ("cuisineId") cuisineId: String,
        @Query ("foodType") foodType: String,
    ) : Call<JsonObject>


    @GET("restaurant/{id}")
    fun menuItem( @Path ("id") id: String): Call<JsonObject>

    @GET("search")
    fun search(): Call<JsonObject>

    @GET("restaurant-items")
    fun searchItem(): Call<JsonObject>

    @GET("cuisine/{id}/restaurants")
    fun homereadytocook( @Path ("id") id: String,): Call<JsonObject>


    @FormUrlEncoded
    @POST("get-delivery-charge")
    fun getShippingChanrge(@Field("restaurant_id") restaurant_id: String,
                     @Field("user_lat") user_lat: String,
                     @Field("user_long") user_long: String,

        ): Call<ResponseBody>

    @POST("profile")
    fun profileUpdate(@Body jsonObject: JsonObject): Call<JsonObject>

    @PUT("change-password")
    fun change_password(@Body jsonObject: JsonObject): Call<JsonObject>

    @POST("check-coupon")
    fun checkCupon(@Body jsonObject: JsonObject): Call<JsonObject>

    @POST("orders")
    fun orders(@Body requestBody: RequestBody): Call<JsonObject>

    @POST("orders/payment")
    fun ordersPayment(@Body requestBody: RequestBody): Call<JsonObject>

    @GET("orders/{id}/show")
    fun ordersTracking(@Path ("id") id: String): Call<JsonObject>
}