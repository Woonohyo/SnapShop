package com.l3cache.snapshop.data;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class User extends RealmObject {
	private String email;
	private int uid;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

}
