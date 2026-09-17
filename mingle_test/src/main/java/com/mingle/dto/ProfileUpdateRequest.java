package com.mingle.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로필 수정 요청 (POST /profile 폼)
 *
 * 사진은 먼저 /api/uploads/temp로 올려 두고,
 * 여기에는 그때 받은 임시 파일명만 담아 보낸다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String nickname;
    private String phone;
    private String region;
    private String job;
    private Integer height;

    private List<Integer> interestIds;

    /** 새로 추가할 사진의 임시 파일명 (temp 폴더에 올라와 있는 것) */
    private List<String> tempFileNames;
}
