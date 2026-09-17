package com.mingle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.mapper.UserMapper;
import com.mingle.type.UploadDir;
import com.mingle.vo.UserPhotoVO;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ProfilePhotoServiceImpl implements ProfilePhotoService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageBlurService imageBlurService;

    @Override
    @Transactional
    public void addPhotos(int userId, List<String> tempFileNames) {

        if (tempFileNames == null || tempFileNames.isEmpty()) {
            return;
        }

        int count = userMapper.countPhotos(userId);
        int displayOrder = userMapper.selectMaxDisplayOrder(userId);

        for (String tempFileName : tempFileNames) {

            if (tempFileName == null || tempFileName.trim().isEmpty()) {
                continue;   // 화면에서 지운 자리
            }

            if (count >= MAX_PHOTOS) {
                throw new IllegalArgumentException("사진은 " + MAX_PHOTOS + "장까지 등록할 수 있습니다.");
            }

            // 이름 검사와 파일 이동은 공용 저장소가 한다
            String photoUrl = fileStorageService.moveFromTemp(tempFileName, UploadDir.PROFILE);

            count++;
            displayOrder++;

            // 사진이 하나도 없던 회원이면 첫 장이 대표 (uk_photos_primary는 회원당 한 장만 허용)
            int isPrimary = (count == 1) ? 1 : 0;

            userMapper.insertProfilePhoto(userId, photoUrl, isPrimary, displayOrder);
        }

        log.info("프로필 사진 등록 - userId: " + userId + ", 총 " + count + "장");
    }

    @Override
    @Transactional
    public void deletePhoto(int userId, int photoId) {

        UserPhotoVO photo = userMapper.selectPhoto(photoId, userId);

        if (photo == null) {
            throw new IllegalArgumentException("삭제할 사진을 찾을 수 없습니다.");
        }

        userMapper.deletePhoto(photoId, userId);

        // 대표 사진을 지웠으면 남은 사진 중 첫 장을 대표로 (카드에 사진이 안 나오는 것을 막는다)
        if (photo.getIsPrimary() == 1) {

            List<UserPhotoVO> rest = userMapper.selectPhotos(userId);

            if (!rest.isEmpty()) {
                userMapper.updatePhotoPrimary(rest.get(0).getId(), userId);
            }
        }

        fileStorageService.delete(photo.getPhotoUrl());

        // 이 사진으로 만들어 둔 흐린 복사본도 함께 지운다
        imageBlurService.delete(photo.getPhotoUrl());

        log.info("프로필 사진 삭제 - userId: " + userId + ", photoId: " + photoId);
    }

    @Override
    @Transactional
    public void setPrimaryPhoto(int userId, int photoId) {

        if (userMapper.selectPhoto(photoId, userId) == null) {
            throw new IllegalArgumentException("사진을 찾을 수 없습니다.");
        }

        // 기존 대표를 해제한 뒤 지정 (같은 트랜잭션이라 대표가 둘인 상태가 생기지 않는다)
        userMapper.clearPrimaryPhoto(userId);
        userMapper.updatePhotoPrimary(photoId, userId);
    }
}
