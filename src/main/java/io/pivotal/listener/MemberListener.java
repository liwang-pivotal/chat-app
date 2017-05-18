package io.pivotal.listener;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.domain.Member;

@SuppressWarnings("rawtypes")
@Component
public class MemberListener extends CacheListenerAdapter {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SimpMessagingTemplate webSocket;
	
	@Override
	public void afterCreate (EntryEvent e) {
		ObjectMapper mapper = new ObjectMapper();
		Member member = (Member) e.getNewValue();
		logger.info("Member added: " + member);
		try {
			webSocket.convertAndSend("/topic/member_add", mapper.writeValueAsString(member));
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
	}

}
