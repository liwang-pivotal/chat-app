package io.pivotal.listener;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.domain.Message;
import io.socket.client.Socket;

@SuppressWarnings("rawtypes")
@Component
public class MessageListener extends CacheListenerAdapter {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	Socket socket;
	
	@Override
	public void afterCreate (EntryEvent e) {
		ObjectMapper mapper = new ObjectMapper();
		Message message = (Message) e.getNewValue();
		try {
			logger.info("Message added: " + message);
			socket.emit("messages", mapper.writeValueAsString(message));
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
	}
	
}
