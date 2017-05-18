package io.pivotal.listener;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.domain.Message;

@SuppressWarnings("rawtypes")
@Component
public class MessageListener extends CacheListenerAdapter {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SimpMessagingTemplate webSocket;
	
	@Override
	public void afterCreate (EntryEvent e) {
		ObjectMapper mapper = new ObjectMapper();
		Message message = (Message) e.getNewValue();
		logger.info("Message added: " + message);
		try {
			webSocket.convertAndSend("/topic/messages", mapper.writeValueAsString(message));
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
	}
	
}
