package com.mingle.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.mingle.type.UploadDir;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ImageBlurServiceImpl implements ImageBlurService {

    /**
     * 줄일 가로 크기 (px)
     * 이 정도로 줄이면 원본을 되살릴 수 없고, 화면에서 늘리면 흐릿하게 보인다.
     */
    private static final int BLUR_WIDTH = 32;

    /** 만든 복사본은 항상 jpg로 저장한다 */
    private static final String BLUR_EXTENSION = "jpg";

    @Override
    public String getBlurredUrl(String photoUrl) {

        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            return null;
        }

        File source = toFile(photoUrl);

        if (source == null || !source.exists()) {
            return null;
        }

        String blurName = baseName(source.getName()) + "." + BLUR_EXTENSION;
        File target = new File(UploadDir.BLUR.getDir(), blurName);

        // 이미 만들어 둔 게 있으면 그대로 쓴다
        if (target.exists()) {
            return UploadDir.BLUR.getUrl() + blurName;
        }

        return create(source, target) ? UploadDir.BLUR.getUrl() + blurName : null;
    }

    @Override
    public void delete(String photoUrl) {

        if (photoUrl == null) {
            return;
        }

        File source = toFile(photoUrl);

        if (source == null) {
            return;
        }

        File blur = new File(UploadDir.BLUR.getDir(), baseName(source.getName()) + "." + BLUR_EXTENSION);

        if (blur.exists() && !blur.delete()) {
            log.warn("흐린 사진 삭제 실패: " + blur.getName());
        }
    }

    /** 작게 줄여서 저장 (실패하면 false, 화면은 기본 이미지를 쓴다) */
    private boolean create(File source, File target) {

        try {
            BufferedImage original = ImageIO.read(source);

            if (original == null || original.getWidth() == 0) {
                log.warn("이미지를 읽지 못했습니다: " + source.getName());
                return false;
            }

            int height = Math.max(1, original.getHeight() * BLUR_WIDTH / original.getWidth());

            BufferedImage small = new BufferedImage(BLUR_WIDTH, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = small.createGraphics();

            // png처럼 투명한 부분이 있으면 검게 나오므로 흰색을 먼저 깔아 둔다
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, BLUR_WIDTH, height);

            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics.drawImage(original, 0, 0, BLUR_WIDTH, height, null);
            graphics.dispose();

            File dir = new File(UploadDir.BLUR.getDir());

            if (!dir.exists() && !dir.mkdirs()) {
                log.warn("흐린 사진 폴더를 만들지 못했습니다 : " + dir.getPath());
                return false;
            }

            return ImageIO.write(small, BLUR_EXTENSION, target);

        } catch (IOException e) {
            // 사진 하나 때문에 목록 조회가 실패하면 안 되므로 로그만 남긴다
            log.warn("흐린 사진을 만들지 못했습니다: " + source.getName(), e);
            return false;
        }
    }

    /** "/uploads/profile/abc.png" → C:/upload/profile/abc.png */
    private File toFile(String url) {

        if (!url.startsWith(UploadDir.BASE_URL)) {
            return null;
        }

        return new File(UploadDir.BASE_DIR + url.substring(UploadDir.BASE_URL.length()));
    }

    /** "abc.png" → "abc" */
    private String baseName(String fileName) {

        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? fileName : fileName.substring(0, dot);
    }
}
