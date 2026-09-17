package com.mingle.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로필 사진 (mingle_profile_photos)
 * photoUrl은 화면에서 쓰는 경로 (/uploads/profile/...)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPhotoVO {

    private int id;
    private int userId;
    private String photoUrl;
    private int isPrimary;      // 1이면 대표 사진 (회원당 한 장)
    private int displayOrder;
}
