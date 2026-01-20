package com.example.speakOn.domain.mySpeak.converter;

import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.dto.form.WaitScreenForm;
import com.example.speakOn.domain.mySpeak.exception.MySpeakException;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MySpeakConverter {
    /**
     * MyRole 엔티티를 MyRoleFormDto로 변환
     *
     * @param myRoles MyRole 엔티티 리스트
     * @return MyRoleFormDto (응답 포맷)
     * @throws MySpeakException 변환 실패 시
     */
    public WaitScreenForm convertToWaitScreenForm(List<MyRole> myRoles) {
        log.debug("MyRole 변환 시작 - 개수: {}", myRoles.size());

        try {
            List<WaitScreenForm.MyRoleDto> roleDtos = myRoles.stream()
                    .map(r -> new WaitScreenForm.MyRoleDto(r))
                    .collect(Collectors.toList());

            WaitScreenForm result = new WaitScreenForm(roleDtos);

            return result;

        } catch (Exception e) {
            log.error("MyRole 변환 중 오류", e);
            throw new MySpeakException(MySpeakErrorCode.MYROLE_CONVERSION_FAILED);
        }
    }
}
