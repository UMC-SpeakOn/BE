package com.example.speakOn.domain.mySpeak.dto.response;

import com.example.speakOn.domain.mySpeak.dto.form.WaitScreenForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WaitScreenResponse {
    private WaitScreenForm waitScreenForm;

}
