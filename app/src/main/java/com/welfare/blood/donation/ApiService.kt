package com.welfare.blood.donation
import com.welfare.blood.donation.models.RegisterRequest
import com.welfare.blood.donation.models.RegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("user/register/")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("createRequest")
    suspend fun createRequest(@Body bloodRequest: BloodRequest): Response<BloodResponse>

    @POST("donateBlood")
    suspend fun donateBlood(@Body request: DonateBloodRequest): DonateBloodResponse
    @POST("user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    data class LoginRequest(
        val name: String,
        val password: String
    )

    data class LoginResponse(
        val auth: Boolean,
        val token: String,
        val role: String
    )

    data class BloodRequest(
        val bloodForMyself: Boolean,
        val patientName: String,
        val age: Int,
        val bloodType: String,
        val requiredUnit: Int,
        val requiredDate: String,
        val hospital: String,
        val location: String
    )

    data class BloodResponse(
        val id: Int,
        val bloodForMyself: Boolean,
        val patientName: String,
        val age: Int,
        val bloodType: String,
        val requiredUnit: Int,
        val requiredDate: String,
        val hospital: String,
        val location: String
    )

    data class DonateBloodRequest(
        val bloodType: String,
        val location: String
    )

    data class DonateBloodResponse(
        val id: Int,
        val bloodType: String,
        val location: String
    )
}
