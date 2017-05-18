package io.pivotal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.domain.Member;
import io.pivotal.domain.Message;
import io.pivotal.repo.MemberRepository;
import io.pivotal.repo.MessageRepository;

@Controller
public class HomeController {
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private MessageRepository messageRepository;
    
	@RequestMapping(value = "/")
    public String index() {
        return "index.html";
    }
	
	@MessageMapping("/test")
    public void test(String payload) throws Exception {
    	System.out.println(payload);
    }
    
    @MessageMapping("/new_member")
    public void newMember(String payload) throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	Member member = mapper.readValue(payload, Member.class);
    	memberRepository.save(member);
    }
    
    @MessageMapping("/member_history")
    @SendTo("/topic/member_history")
    public String members(String payload) throws Exception {
        return payload;
    }
    
    @MessageMapping("/message_history")
    @SendTo("/topic/message_history")
    public String messages(String payload) throws Exception {
        return payload;
    }
    
    @MessageMapping("/send")
	public void send(String payload) throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	Message message = mapper.readValue(payload, Message.class);
    	messageRepository.save(message);
	}
       
}