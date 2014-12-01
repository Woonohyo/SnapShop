package com.l3cache.snapshop.retrofit;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SnapShopService {
	@GET("/app/users/login")
	void login(@Query("email") String email, @Query("password") String password, Callback<Response> cb);
}
