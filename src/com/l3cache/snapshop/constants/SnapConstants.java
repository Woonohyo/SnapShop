package com.l3cache.snapshop.constants;

public final class SnapConstants {

	public static final String SERVER_URL = "http://125.209.199.221:8080";
	public static final String NEWSFEED_REQUEST = "/app/posts/";
	public static final String SEARCH_REQUEST = "/search/shop";
	public static final String LOGIN_REQUEST = "/app/users/login";
	public static final String SIGNUP_REQUEST = "/app/users/new";
	public static final String NEW_NAVER_POST_REQUEST = "/app/posts/newurl";
	public static final String NEW_CUSTOM_POST_REQUEST = "/app/posts/new";
	public static final String SNAP_REQUEST = "/app/posts/like";
	public static final String DEACTIVATE_REQUEST = "/app/users/delete";

	public static final String READ_POST_REQUEST = "/app/posts/{pid}/read";

	public static final String MYSNAP_REQUEST(int uid) {
		return "/app/users/" + uid + "/likes";
	}

	public static final String MYPOST_REQUEST(int uid) {
		return "/app/users/" + uid + "/posts";
	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int SUCCESS = 10;
	public static final int ERROR = 20;
	public static final int EMAIL_DUPLICATION = 21;
	public static final int EMAIL_ERROR = 22;
	public static final int PASSWORD_ERROR = 23;
	public static final int ACCESS_DENIED = 24;

	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int RESULT_LOAD_IMAGE = 200;

	public static final int CAMERA_BUTTON = 1001;
	public static final int GALLERY_BUTTON = 1002;
	public static final int INTERNET_BUTTON = 1003;
}
