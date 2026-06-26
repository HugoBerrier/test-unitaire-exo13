package com.mediacity.repository;

public interface SeriousDelayRepository {

    int countForMemberAndYear(String memberId, int year);

    void recordSeriousDelay(String memberId, int year);

    void clear();
}
