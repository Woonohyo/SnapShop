package com.l3cache.snapshop.retrofit;

import retrofit.Callback;
import retrofit.http.DELETE;
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

import com.l3cache.snapshop.SnapConstants;

public interface SnapShopService {
	/**
	 * 로그인 요청
	 * 
	 * @param email
	 *            로그인하려는 사용자의 이메일 주소
	 * @param password
	 *            로그인하려는 사용자의 패스워드
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백<SignInResponse>
	 */
	@GET(SnapConstants.LOGIN_REQUEST)
	void login(@Query("email") String email, @Query("password") String password, Callback<SignInResponse> cb);

	/**
	 * 회원가입 요청
	 * 
	 * @param email
	 *            신규 회원의 이메일 주소
	 * @param password
	 *            신규 회원의 비밀번호
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백<SignUpResponse>
	 */
	@POST(SnapConstants.SIGNUP_REQUEST)
	void signUp(@Query("email") String email, @Query("password") String password, Callback<SignUpResponse> cb);

	/**
	 * 네이버 쇼핑 API를 검색 결과를 이용하여 새 포스트 작성
	 * 
	 * @param title
	 *            포스트의 타이틀 (필수)
	 * @param shopUrl
	 *            상품의 온라인 URL (선택)
	 * @param contents
	 *            포스트의 내용 (선택)
	 * @param imageUrl
	 *            상품 이미지의 URL (필수. 쇼핑 API의 검색 결과에서 자동으로 복사됨)
	 * @param price
	 *            상품의 가격 (필수. 쇼핑 API의 검색 결과에서 자동으로 복사됨)
	 * @param id
	 *            사용자의 ID (SnapPreference에서 읽어옴)
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백(UploadResponse)
	 */
	@FormUrlEncoded
	@POST(SnapConstants.NEW_NAVER_POST_REQUEST)
	void uploadSnap(@Field("title") String title, @Field("shopUrl") String shopUrl, @Field("contents") String contents,
			@Field("image") String imageUrl, @Field("price") String price, @Field("id") int id,
			Callback<DefaultResponse> cb);

	/**
	 * 카메라/앨범을 이용하여 새 포스트 작성
	 * 
	 * @param title
	 *            포스트의 타이틀 (필수)
	 * @param shopUrl
	 *            상품의 온라인 URL (선택)
	 * @param contents
	 *            포스트의 내용 (선택)
	 * @param imageFile
	 *            사용자가 선택한 이미지 (필수)
	 * @param price
	 *            상품의 가격 (필수)
	 * @param id
	 *            사용자의 ID (SnapPreference에서 읽어옴)
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백(UploadResponse)
	 */
	@Multipart
	@POST(SnapConstants.NEW_CUSTOM_POST_REQUEST)
	void uploadSnap(@Part("title") TypedString title, @Part("shopUrl") TypedString shopUrl,
			@Part("contents") TypedString contents, @Part("image") TypedFile imageFile,
			@Part("price") TypedString price, @Part("id") int id, Callback<DefaultResponse> cb);

	/**
	 * 해당 포스트를 현재 사용자의 Snap 목록에 추가한다.
	 * 
	 * @param uid
	 *            현재 사용자의 ID (SnapPreference에서 읽어옴)
	 * @param pid
	 *            해당 포스트의 ID
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백(DefaultResponse)
	 */
	@FormUrlEncoded
	@POST(SnapConstants.SNAP_REQUEST)
	void snapPost(@Field("uid") int uid, @Field("pid") int pid, Callback<DefaultResponse> cb);

	/**
	 * 해당 포스트를 현재 사용자의 Snap 목록에서 제거한다.
	 * 
	 * @param uid
	 *            현재 사용자의 ID (SnapPreference에서 읽어옴)
	 * @param pid
	 *            해당 포스트의 ID
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백(DefaultResponse)
	 */
	@DELETE(SnapConstants.SNAP_REQUEST)
	void unSnapPost(@Query("uid") int uid, @Query("pid") int pid, Callback<DefaultResponse> cb);

	/**
	 * 해당 포스트의 조회수를 1 증가시킨다.
	 * 
	 * @param pid
	 *            해당 포스트의 ID
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백(DefaultResponse)
	 */
	@POST(SnapConstants.READ_POST_REQUEST)
	void readPost(@Path("pid") long pid, Callback<DefaultResponse> cb);

	/**
	 * 해당 포스트를 서버에서 제거한다.
	 * 
	 * @param pid
	 *            해당 포스트의 ID
	 * @param uid
	 *            삭제를 요청하는 사용자의 ID
	 * @param cb
	 *            성공/실패 여부 처리를 위한 콜백(DefaultResponse)
	 */
	@DELETE(SnapConstants.POST_DELETE_REQUEST)
	void deletePost(@Path("pid") int pid, @Query("uid") int uid, Callback<DefaultResponse> cb);
}
