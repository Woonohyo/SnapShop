package com.l3cache.snapshop.favorite;

public class FavoriteItem {
	private int id;
	private String name, status, image, profilePic, timeStamp, url;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public FavoriteItem() {
		// TODO Auto-generated constructor stub
	}

	public FavoriteItem(int id, String name, String status, String image, String profilePic, String timeStamp,
			String url) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
		this.image = image;
		this.profilePic = profilePic;
		this.timeStamp = timeStamp;
		this.url = url;
	}

}
