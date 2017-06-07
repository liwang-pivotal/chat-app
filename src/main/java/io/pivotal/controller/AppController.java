package io.pivotal.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.domain.Member;
import io.pivotal.domain.Message;
import io.pivotal.repo.MemberRepository;
import io.pivotal.repo.MessageRepository;

@RestController
public class AppController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SimpMessagingTemplate webSocket;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@RequestMapping(method = RequestMethod.GET, path = "/test")
	public void test() throws Exception {
		webSocket.convertAndSend("/topic/test", "disconnect");
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/members")
	@ResponseBody
	@CrossOrigin
	public List<Member> getMembers() throws Exception {
		return (List<Member>) memberRepository.findAll();
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/messages")
	@ResponseBody
	@CrossOrigin
	public List<Message> getMessages() throws Exception {
		List<Message> messages = (List<Message>) messageRepository.getRecent10Messages();
		Collections.reverse(messages);
		return messages;
	}

}
