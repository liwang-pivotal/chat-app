package io.pivotal.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

@Region("messages")
public class Message {

	@Id
	private Date epoch;
	private String username;
	private String avatar;
	private String message;
	
	public Message() {}

	public Message(Date epoch, String username, String avatar, String message) {
		super();
		this.epoch = epoch;
		this.username = username;
		this.avatar = avatar;
		this.message = message;
	}

	public Date getEpoch() {
		return epoch;
	}

	public void setEpoch(Date epoch) {
		this.epoch = epoch;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message [epoch=" + epoch + ", username=" + username + ", avatar=" + avatar + ", message=" + message
				+ "]";
	}

}
