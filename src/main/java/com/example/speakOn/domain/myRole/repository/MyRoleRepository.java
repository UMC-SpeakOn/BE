package com.example.speakOn.domain.myRole.repository;

import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MyRoleRepository {

    private final EntityManager em;

    //대화 세션 생성하는데 필요해서 생성
    public MyRole findById(Long myRoleId) {
        return em.find(MyRole.class, myRoleId);
    }
}
