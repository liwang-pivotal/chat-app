package io.pivotal.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.domain.Member;
import io.pivotal.domain.Message;
import io.pivotal.repo.MemberRepository;
import io.pivotal.repo.MessageRepository;
import io.socket.client.Socket;

@RestController
public class ChatDemoController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private Socket socket;
	
	@RequestMapping(method = RequestMethod.GET, path = "/show")
	@ResponseBody
	public String create() throws Exception {
		socket.emit("test", "hi from inner space");
		return "new members created";
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/members")
	@ResponseBody
	@CrossOrigin
	public List<Member> getMembers() throws Exception {
		return (List<Member>) memberRepository.findAll();
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/add_member")
	@CrossOrigin
	public void addMember(@RequestBody String payload) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Member member = mapper.readValue(payload, Member.class);
		
		memberRepository.save(member);
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/delete_member")
	@CrossOrigin
	public void deleteMember(@RequestBody String payload) throws Exception {
		
		memberRepository.delete(payload);
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/messages")
	@ResponseBody
	@CrossOrigin
	public List<Message> getMessages() throws Exception {
		List<Message> messages = (List<Message>) messageRepository.getRecent10Messages();
		Collections.reverse(messages);
		return messages;
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/add_message")
	@CrossOrigin
	public void addMessage(@RequestBody String payload) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Message message = mapper.readValue(payload, Message.class);
		
		messageRepository.save(message);
	}

	
	@RequestMapping(method = RequestMethod.GET, path = "/test")
	//@CrossOrigin
	public String test() throws Exception {
		
		return "Hello World";
	}
}
