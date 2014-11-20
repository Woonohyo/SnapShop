package com.l3cache.snapshop.newsfeed;

public class NewsfeedData {
	private String imgName;
	private String name;
	private String price;

	public NewsfeedData(String imgName, String name, String price) {
		this.imgName = imgName;
		this.name = name;
		this.price = price;
	}

	public NewsfeedData() {
		// TODO Auto-generated constructor stub
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

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

}
