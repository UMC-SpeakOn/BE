package com.example.speakOn.domain.myRole.repository;

import com.example.speakOn.domain.myRole.entity.MyRole;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyRoleRepositoryImpl implements MyRoleRepositoryCustom {

    private final EntityManager em;

    //대화 세션 생성하는데 필요해서 생성
    @Override
    public MyRole findMyRoleById(Long myRoleId) {
        return em.find(MyRole.class, myRoleId);
    }

}
