package com.mingle.service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mingle.type.UploadDir;
import com.mingle.type.UploadFileType;
import com.mingle.util.StoredFile;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class FileStorageServiceImpl implements FileStorageService {

    /** 이 서비스가 만드는 파일명 형식 (UUID 32자 + 확장자) */
    private static final Pattern STORED_FILE_NAME =
            Pattern.compile("^[0-9a-f]{32}\\.[a-z0-9]{2,5}$");

    @Override
    public StoredFile save(MultipartFile file, UploadDir dir, List<UploadFileType> allowed) {

        UploadFileType type = checkType(file, allowed);
        String fileName = newFileName(file);

        write(file, dir, fileName);

        return new StoredFile(fileName, dir.getUrl() + fileName, type);
    }

    @Override
    public StoredFile saveTemp(MultipartFile file, List<UploadFileType> allowed) {
        return save(file, UploadDir.TEMP, allowed);
    }

    @Override
    public String moveFromTemp(String tempFileName, UploadDir dir) {

        // saveTemp가 만든 이름만 허용 (../ 같은 경로가 들어오는 것을 막는다)
        if (!isStoredFileName(tempFileName)) {
            throw new IllegalArgumentException("잘못된 파일입니다.");
        }

        File source = new File(UploadDir.TEMP.getDir(), tempFileName);

        if (!source.exists()) {
            throw new IllegalArgumentException("파일을 다시 올려 주세요. (임시 파일이 없습니다)");
        }

        File target = new File(createDir(dir), tempFileName);

        try {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return dir.getUrl() + tempFileName;
    }

    @Override
    public void delete(String url) {

        if (url == null || !url.startsWith(UploadDir.BASE_URL)) {
            return;
        }

        // /uploads/profile/abc.jpg → C:/upload/profile/abc.jpg
        File file = new File(UploadDir.BASE_DIR + url.substring(UploadDir.BASE_URL.length()));

        // 파일이 안 지워져도 화면 동작을 막지는 않는다 (DB에서는 이미 지워졌음)
        if (file.exists() && !file.delete()) {
            log.warn("파일 삭제 실패: " + url);
        }
    }

    @Override
    public boolean isStoredFileName(String fileName) {
        return fileName != null && STORED_FILE_NAME.matcher(fileName).matches();
    }

    @Override
    public List<String> findOlderThan(UploadDir dir, long hours) {

        List<String> old = new ArrayList<>();

        File[] files = new File(dir.getDir()).listFiles();

        if (files == null) {
            return old;
        }

        long limit = System.currentTimeMillis() - (hours * 60 * 60 * 1000);

        for (File file : files) {
            try {
                BasicFileAttributes attributes =
                        Files.readAttributes(file.toPath(), BasicFileAttributes.class);

                if (attributes.creationTime().toMillis() < limit) {
                    old.add(file.getName());
                }

            } catch (IOException e) {
                // 한 파일을 못 읽어도 나머지는 계속 본다
                log.warn("파일 정보를 읽지 못했습니다: " + file.getName(), e);
            }
        }

        return old;
    }

    /** 확장자와 Content-Type이 둘 다 맞는 종류를 찾는다 */
    private UploadFileType checkType(MultipartFile file, List<UploadFileType> allowed) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String extension = extractExtension(file.getOriginalFilename());
        String contentType = file.getContentType();

        for (UploadFileType type : allowed) {
            if (type.matches(extension, contentType)) {
                return type;
            }
        }

        throw new IllegalArgumentException(allowedMessage(allowed));
    }

    /** 원본 파일명은 쓰지 않는다 (한글/공백, 경로 조작 문제) */
    private String newFileName(MultipartFile file) {

        return UUID.randomUUID().toString().replace("-", "")
                + "." + extractExtension(file.getOriginalFilename());
    }

    private void write(MultipartFile file, UploadDir dir, String fileName) {

        try {
            file.transferTo(new File(createDir(dir), fileName));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private File createDir(UploadDir dir) {

        File directory = new File(dir.getDir());

        if (!directory.exists() && !directory.mkdirs()) {
            throw new UncheckedIOException(new IOException("업로드 폴더를 만들 수 없습니다: " + dir.getDir()));
        }
        return directory;
    }

    /** 소문자 확장자, 없으면 빈 문자열 */
    private String extractExtension(String originalFilename) {

        if (originalFilename == null) {
            return "";
        }

        int dot = originalFilename.lastIndexOf('.');
        return (dot == -1) ? "" : originalFilename.substring(dot + 1).toLowerCase();
    }

    private String allowedMessage(List<UploadFileType> allowed) {

        StringBuilder message = new StringBuilder();

        for (UploadFileType type : allowed) {

            if (message.length() > 0) {
                message.append(" 또는 ");
            }

            message.append(type == UploadFileType.IMAGE ? "사진(" : "동영상(")
                   .append(type.getExtensionNames())
                   .append(")");
        }

        return message + "만 올릴 수 있습니다.";
    }
}
