package com.l3cache.snapshop.volley;


import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.l3cache.snapshop.volley.DelegatingRequest.NewtorkResponseParser;

public class StringParser implements NewtorkResponseParser<String> {

	@Override
	public Response<String> parseResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed,
				HttpHeaderParser.parseCacheHeaders(response));
	}
}