package com.l3cache.snapshop.volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class NewsfeedRequest extends Request<JSONObject> {
	private Map<String, String> mParams = new HashMap<String, String>();
	private Listener<JSONObject> mListener;

	public NewsfeedRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
	}

	public NewsfeedRequest(String url, Map<String, String> params, Listener<JSONObject> responseListener,
			ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		mListener = responseListener;
		mParams = params;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected Map<String, String> getParams() {
		return mParams;
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}
}
