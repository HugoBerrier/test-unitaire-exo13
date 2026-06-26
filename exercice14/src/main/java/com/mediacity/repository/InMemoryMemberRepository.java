package com.mediacity.repository;

import com.mediacity.model.Member;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> storage = new HashMap<>();

    @Override
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Member save(Member member) {
        storage.put(member.getId(), member);
        return member;
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
