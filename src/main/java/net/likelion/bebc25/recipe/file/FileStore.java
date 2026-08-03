package net.likelion.bebc25.recipe.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStore {

    // 저장 경로를 반환
    String getFullPath(String fileName);

    // 업로드된 파일을 저장소에 저장하고 저장된 파일명을 반환합니다.
    String storeFile(MultipartFile multipartFile) throws IOException;

    // 저장된 파일명을 기반으로 다운로드용 Resource를 생성합니다
    Resource getFileResource(String storeFileName) throws IOException;

    // 파일명을 기반으로 이미지 파일 여부를 판별합니다
    boolean isImage(String fileName);
}
