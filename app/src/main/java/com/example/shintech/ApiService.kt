package com.example.shintech

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // --- 회원 (Users) --- //
    @Headers("No-Authentication: true")
    @POST("users/signup")
    fun signup(@Body request: SignupRequest): Call<ResponseBody>

    @Headers("No-Authentication: true")
    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("No-Authentication: true")
    @POST("users/password/reset")
    fun findPassword(@Body request: PasswordResetRequest): Call<PasswordResetResponse>

    @PUT("users/update")
    fun updateUser(@Body request: UpdateUserRequest): Call<ResponseBody>

    @HTTP(method = "DELETE", path = "users/withdraw", hasBody = true)
    fun withdraw(@Body request: WithdrawRequest): Call<ResponseBody>

    // --- 상품 (Phones) --- //
    @Multipart
    @POST("phones")
    fun createPhone(
        @Part("phone") request: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>
    
    @GET("phones")
    fun getPhones(): Call<List<Phone>>

    @GET("phones/{id}")
    fun getPhoneDetail(@Path("id") phoneId: Long): Call<Phone>

    @Multipart
    @PUT("phones/{id}")
    fun updatePhone(
        @Path("id") phoneId: Long,
        @Part("phone") request: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>

    @DELETE("phones/{id}")
    fun deletePhone(@Path("id") phoneId: Long): Call<ResponseBody>

    // --- 장바구니 (Cart) --- //
    @POST("cart/items")
    fun addToCart(@Body request: CartItemAddRequest): Call<ResponseBody>

    @GET("cart")
    fun getCartItems(): Call<CartResponse>

    @POST("cart/items/decrease")
    fun decreaseCartItem(@Body request: CartItemDecreaseRequest): Call<ResponseBody>

    @DELETE("cart/items/{phoneId}")
    fun deleteCartItem(@Path("phoneId") phoneId: Long): Call<ResponseBody>

    @DELETE("cart")
    fun clearCart(): Call<ResponseBody>

    // --- 찜 (Favorites) --- //
    @POST("favorites")
    fun addFavorite(@Query("phoneId") phoneId: Long): Call<ResponseBody>

    @GET("favorites")
    fun getFavorites(): Call<List<FavoritePhoneResponse>>

    @DELETE("favorites/{phoneId}")
    fun deleteFavorite(@Path("phoneId") phoneId: Long): Call<ResponseBody>

    // --- 리뷰/평점 (Reviews) --- //
    @POST("reviews")
    fun createReview(@Body request: ReviewCreateRequest): Call<ResponseBody>

    @GET("reviews/phones/{phoneId}/average")
    fun getAverageRating(@Path("phoneId") phoneId: Long): Call<PhoneRatingResponse>

    @GET("reviews/phones/{phoneId}")
    fun getPhoneReviews(@Path("phoneId") phoneId: Long): Call<List<PhoneReviewResponse>>

    @DELETE("reviews/{reviewId}")
    fun deleteReview(@Path("reviewId") reviewId: Long): Call<ResponseBody>

    // --- 댓글 (Comments) --- //
    @POST("comments")
    fun createComment(@Body request: CommentsCreateRequest): Call<ResponseBody>

    @GET("comments/phones/{phoneId}") 
    fun getPhoneComments(@Path("phoneId") phoneId: Long): Call<List<CommentResponse>>

    @PUT("comments/{commentId}")
    fun updateComment(@Path("commentId") commentId: Long, @Body request: CommentUpdateRequest): Call<ResponseBody>

    @DELETE("comments/{commentId}")
    fun deleteComment(@Path("commentId") commentId: Long): Call<ResponseBody>

    // --- 결제 (Payment) --- //
    @POST("v1/kakao-pay/ready")
    fun readyKakaoPay(@Body request: KakaoPayRequest): Call<KakaoPayResponse>

    // --- 구매 내역 (Orders) --- //
    @GET("orders/history")
    fun getOrderHistory(): Call<List<OrderHistoryResponse>>

    // --- 관리자 (Admin) --- //
    @GET("admin/totalSales")
    fun getTotalSales(): Call<Long>
}
