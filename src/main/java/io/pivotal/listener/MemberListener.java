package io.pivotal.listener;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.domain.Member;
import io.socket.client.Socket;

@SuppressWarnings("rawtypes")
@Component
public class MemberListener extends CacheListenerAdapter {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	Socket socket;
	
	@Override
	public void afterCreate (EntryEvent e) {
		ObjectMapper mapper = new ObjectMapper();
		Member member = (Member) e.getNewValue();
		try {
			logger.info("Member added: " + member);
			socket.emit("member_add", mapper.writeValueAsString(member));
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
	}

}
