package com.mediacity.repository;

import com.mediacity.model.Member;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findById(String id);

    Member save(Member member);

    void clear();
}
