package io.pivotal.config;

import io.pivotal.domain.Member;
import io.pivotal.domain.Message;
import io.pivotal.listener.MemberListener;
import io.pivotal.listener.MessageListener;
import io.pivotal.spring.cloud.service.gemfire.GemfireServiceConnectorConfig;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PccConfig extends AbstractCloudConfig {

	@Autowired
	private ClientCache clientCache;
	
//	@Bean(name = "gemfireCache")
//	public ClientCache clientCache() {
//		ClientCacheFactory ccf = new ClientCacheFactory();
//		ccf.addPoolLocator("localhost", 10334);
//		ccf.setPdxSerializer(new ReflectionBasedAutoSerializer(".*"));
//		ccf.setPdxReadSerialized(false);
//		ccf.setPoolSubscriptionEnabled(true);
//
//		ClientCache clientCache = ccf.create();
//
//		return clientCache;
//	}

	public ServiceConnectorConfig createGemfireConnectorConfig() {
        GemfireServiceConnectorConfig gemfireConfig = new GemfireServiceConnectorConfig();
        gemfireConfig.setPoolSubscriptionEnabled(true);
        gemfireConfig.setPdxSerializer(new ReflectionBasedAutoSerializer(".*"));
        gemfireConfig.setPdxReadSerialized(false);

        return gemfireConfig;
    }
    
	@Bean(name = "gemfireCache")
    public ClientCache getGemfireClientCache() throws Exception {			
		Cloud cloud = new CloudFactory().getCloud();
		ClientCache clientCache = cloud.getSingletonServiceConnector(ClientCache.class,  createGemfireConnectorConfig());

        return clientCache;
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

}
