package io.pivotal.repo;

import io.pivotal.domain.Member;

import org.springframework.data.gemfire.repository.GemfireRepository;

public interface MemberRepository extends GemfireRepository<Member, String> {
}
