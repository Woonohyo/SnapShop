package com.l3cache.snapshop.retrofit;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import com.l3cache.snapshop.constants.SnapConstants;
import com.l3cache.snapshop.login.LoginResponse;
import com.l3cache.snapshop.login.SignUpResponse;
import com.l3cache.snapshop.upload.UploadResponse;

public interface SnapShopService {
	// 로그인
	@GET(SnapConstants.LOGIN_REQUEST)
	void login(@Query("email") String email, @Query("password") String password, Callback<LoginResponse> cb);

	// 회원가
	@POST(SnapConstants.SIGNUP_REQUEST)
	void signUp(@Query("email") String email, @Query("password") String password, Callback<SignUpResponse> cb);

	// 새 포스트 작성
	@FormUrlEncoded
	@POST(SnapConstants.NEW_NAVER_POST_REQUEST)
	void uploadSnap(@Field("title") String title, @Field("shopUrl") String shopUrl, @Field("contents") String contents,
			@Field("image") String imageUrl, @Field("price") String price, @Field("id") int id,
			Callback<UploadResponse> cb);

	// 새 포스트 작성 (파일)
	@Multipart
	@POST(SnapConstants.NEW_CUSTOM_POST_REQUEST)
	void uploadSnap(@Part("title") TypedString title, @Part("shopUrl") TypedString shopUrl,
			@Part("contents") TypedString contents, @Part("image") TypedFile imageFile,
			@Part("price") TypedString price, @Part("id") int id, Callback<UploadResponse> cb);

	// 포스트 스냅하기
	@FormUrlEncoded
	@POST(SnapConstants.SNAP_REQUEST)
	void snapPost(@Field("uid") int uid, @Field("pid") int pid);

	// 포스트 조회수 증가
	@POST(SnapConstants.READ_POST_REQUEST)
	void readPost(@Path("pid") long pid, Callback<DefaultResponse> cb);
}
