package io.pivotal;

import io.pivotal.domain.Member;
import io.pivotal.domain.Message;
import io.pivotal.listener.MemberListener;
import io.pivotal.listener.MessageListener;
import io.socket.client.IO;
import io.socket.client.Socket;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class ChatDemoConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Autowired
	private ClientCache clientCache;

	@Bean(name = "gemfireCache")
	public ClientCache clientCache() {
		ClientCacheFactory ccf = new ClientCacheFactory();
		ccf.addPoolLocator("localhost", 10334);
		ccf.setPdxSerializer(new ReflectionBasedAutoSerializer(".*"));
		ccf.setPdxReadSerialized(false);
		ccf.setPoolSubscriptionEnabled(true);

		ClientCache clientCache = ccf.create();

		return clientCache;
	}
	
	@Bean
	public Socket createSocket() throws Exception {
		Socket socket = IO.socket("http://localhost:3000");
		socket.connect();
		
		return socket;
	}

	@SuppressWarnings("unchecked")
	@Bean(name = "messagesRegion")
	public Region<String, Message> messagesRegion(@Autowired MessageListener messageListener) {
		ClientRegionFactory<String, Message> messagesRegionFactory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		messagesRegionFactory.addCacheListener(messageListener);
		Region<String, Message> messagesRegion = messagesRegionFactory.create("messages");
		
		messagesRegion.registerInterest("ALL_KEYS");
		
		return messagesRegion;
	}
	
	@SuppressWarnings("unchecked")
	@Bean(name = "membersRegion")
	public Region<String, Member> membersRegion(@Autowired MemberListener memberListener) {
		ClientRegionFactory<String, Member> membersRegionFactory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		membersRegionFactory.addCacheListener(memberListener);
		Region<String, Member> membersRegion = membersRegionFactory.create("members");
		
		membersRegion.registerInterest("ALL_KEYS");
		
		return membersRegion;
	}
	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }

}
