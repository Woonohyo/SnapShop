package com.l3cache.snapshop.retrofit;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.l3cache.snapshop.login.LoginResponse;
import com.l3cache.snapshop.login.SignUpResponse;

public interface SnapShopService {
	@GET("/app/users/login")
	void login(@Query("email") String email, @Query("password") String password, Callback<LoginResponse> cb);
	
	@POST("/app/users/new")
	void signUp(@Query("email") String email, @Query("password") String password, Callback<SignUpResponse> cb);
	
//	@POST("/app/posts/new")
}
