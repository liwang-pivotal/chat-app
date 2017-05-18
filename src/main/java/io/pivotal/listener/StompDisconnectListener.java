package io.pivotal.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import io.pivotal.repo.MemberRepository;

@Component
public class StompDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private SimpMessagingTemplate webSocket;
	
	@Autowired
	private MemberRepository memberRepository;
	
	public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();

        System.out.println("Disconnect event :" + sessionId);
        deleteMember(sessionId);
        logger.info("Member deleted: " + sessionId);
        webSocket.convertAndSend("/topic/member_delete", sessionId);
    }

	private void deleteMember(String socket) {
		memberRepository.delete(socket);
	}
}
