package com.l3cache.snapshop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class Proxy {

	public String searchItem(String query) {
		try {
			
			
			
			URL url = new URL("http://10.73.45.133:8080/search/shop?query=" + query + "&display=20&start=1&sort=sim");

			// URL url = new URL(
			// "http://openapi.naver.com/search?key=913fc8ed6e0924f21000afe7ede1df4b&query=노트북&display=5&start=1&target=shop&sort=sim");
			Log.i("Proxy", url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// 서버 접속시 timeout (ms)
			conn.setConnectTimeout(10 * 1000);

			// read시 timeout (ms)
			conn.setReadTimeout(10 * 1000);

			// 요청 방식
			conn.setRequestMethod("GET");
			// 연결 지속
			conn.setRequestProperty("Connection", "Keep-Alive");
			// requesting character-set UTF-8
			conn.setRequestProperty("Accept-Charset", "UTF-8");

			// 캐시된 데이터 사용 대신 매번 서버로부터 리로드
			conn.setRequestProperty("Cache-Control", "no-cache");
			// 서버로부터 json 형식의 타입으로 데이터 요청
			conn.setRequestProperty("Accept", "application/json");

			// InputStream으로 서버로부터 응답 받기
			conn.setDoInput(true);

			conn.connect();

			int status = conn.getResponseCode();
			Log.i("Proxy", "ProxyResponseCode:" + status);

			switch (status) {
			// 정상적으로 연결이 된 상태 (200, 201)
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();

				return sb.toString();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
