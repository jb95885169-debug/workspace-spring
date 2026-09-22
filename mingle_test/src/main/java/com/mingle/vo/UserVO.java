package com.mingle.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private int id;
    private String email;
    private String passwordHash;   // BCrypt 해시 (mingle_users.password_hash)
    private String phone;
    private String status;
    private Date suspendedUntil;
    private Date lastLoginAt;
    private Date createdAt;
    private Date updatedAt;
}
