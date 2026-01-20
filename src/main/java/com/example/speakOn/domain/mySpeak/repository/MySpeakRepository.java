package com.example.speakOn.domain.mySpeak.repository;

import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MySpeakRepository {

    private final EntityManager em;

    /**
     * 특정 사용자의 모든 MyRole 조회
     * User와 Avatar를 함께 fetch join으로 조회 (N+1 문제 방지)
     *
     * @param userId 사용자 ID
     * @return MyRole 리스트
     * @throws Exception 조회 실패 시
     */
    public List<MyRole> findAllWithUserAvatar(Long userId){
        try {
            List<MyRole> roles = em.createQuery("select r from MyRole r " +
                            "join fetch r.user u " +
                            "join fetch r.avatar a " +
                            "where u.id = :userId", MyRole.class)
                    .setParameter("userId", userId)
                    .getResultList();

            return roles;
        } catch (Exception e) {
            log.error("MyRole 조회 중 오류 발생 - userId: {}", userId, e);
            throw new MySpeakException(MySpeakErrorCode.MYROLE_FETCH_FAILED);
        }

    }
}
