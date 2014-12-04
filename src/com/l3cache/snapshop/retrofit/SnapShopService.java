package com.l3cache.snapshop.retrofit;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.l3cache.snapshop.login.LoginResponse;
import com.l3cache.snapshop.login.SignUpResponse;
import com.l3cache.snapshop.upload.UploadResponse;

public interface SnapShopService {
	@GET("/app/users/login")
	void login(@Query("email") String email, @Query("password") String password, Callback<LoginResponse> cb);

	@POST("/app/users/new")
	void signUp(@Query("email") String email, @Query("password") String password, Callback<SignUpResponse> cb);

	@FormUrlEncoded
	@POST("/app/posts/newurl")
	void uploadSnap(@Field("title") String title, @Field("shopUrl") String shopUrl, @Field("contents") String contents,
			@Field("image") String imageUrl, @Field("price") String price, @Field("id") int id, Callback<UploadResponse> cb);
	
}
