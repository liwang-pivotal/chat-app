package io.pivotal.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

@Region("members")
public class Member {
	
	private String username;
	@Id
	private String socket;
	private String avatar;
	
	public Member() {
	}

	public Member(String username, String socket, String avatar) {
		super();
		this.username = username;
		this.socket = socket;
		this.avatar = avatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSocket() {
		return socket;
	}

	public void setSocket(String socket) {
		this.socket = socket;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "Member [username=" + username + ", socket=" + socket + ", avatar=" + avatar + "]";
	}
	
	
}
