package com.l3cache.snapshop.data;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.RealmClass;

@RealmClass
public class NewsfeedData extends RealmObject {
	private int pid;
	private String title;
	private String shopUrl;
	private String contents;
	private String imageUrl;
	private int numLike;
	private String price;
	private String writeDate;
	private String writer;
	private int userLike;
	private int read;
	private int userId;

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public int getNumLike() {
		return numLike;
	}

	public void setNumLike(int numLike) {
		this.numLike = numLike;
	}

	public int getUserLike() {
		return userLike;
	}

	public void setUserLike(int isLike) {
		this.userLike = isLike;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	private String name;

	public NewsfeedData() {
	}

	public NewsfeedData(String title, String shopUrl, String contents, String imageUrl, String price, int userId) {
		this.title = title;
		this.shopUrl = shopUrl;
		this.contents = contents;
		this.imageUrl = imageUrl;
		this.price = price;
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String url) {
		this.shopUrl = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getWriter() {
		return writer;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
