package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 임시 업로드 결과 (POST /api/uploads/temp)
 *
 * tempFileName : 가입 / 수정 폼이 그대로 되돌려 보낼 이름
 * tempUrl      : 저장 전에 화면에서 미리 보여 줄 주소
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempUploadResponse {

    private String tempFileName;
    private String tempUrl;
}
