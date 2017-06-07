package io.pivotal.repo;

import io.pivotal.domain.Message;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

public interface MessageRepository extends GemfireRepository<Message, Date> {
	
	@Query("select * from /messages ORDER BY epoch DESC LIMIT 10")
	Collection<Message> getRecent10Messages();
	
}
