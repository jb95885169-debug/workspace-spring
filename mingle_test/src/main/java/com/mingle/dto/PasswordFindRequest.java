package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 찾기 요청 (POST /password/find 폼)
 *
 * 저장된 비밀번호는 BCrypt 해시라 원래 값을 되돌릴 수 없다.
 * 그래서 이메일 + 닉네임으로 본인을 확인한 뒤 임시 비밀번호를 새로 발급한다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordFindRequest {

    private String email;
    private String nickname;
}
