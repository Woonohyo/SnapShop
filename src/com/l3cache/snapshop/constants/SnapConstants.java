package com.l3cache.snapshop.constants;

public final class SnapConstants {
	public static final String SERVER_URL() {
		return "http://125.209.199.221:8080";
	}

	public static final String NEWSFEED_REQUEST() {
		return "/app/posts/";
	}

	public static final String SEARCH_REQUEST() {
		return "/search/shop";
	}

	public static final String LOGIN_REQUEST() {
		return "/app/users/login";
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
