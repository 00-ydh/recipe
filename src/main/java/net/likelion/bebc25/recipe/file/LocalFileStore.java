package net.likelion.bebc25.recipe.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalFileStore implements FileStore {

    // 이미지 업로드 파일 경로
    @Value("${file.mainImageDir}")
    private String mainImageDir;

    // 저장소의 절대 경로 Path를 반환합니다.
    private Path getUploadPath() {
        return Paths.get(mainImageDir).toAbsolutePath();
    }

    // 파일명에서 확장자를 추출합니다.
    // 원본 파일명에서 마지막 점 위치의 다음 문자를 추출합니다.
    private String extractExt(String originalFilename){
        if(originalFilename == null){
            return "";
        }
        int pos = originalFilename.lastIndexOf('.');
        return pos >= 0 ? originalFilename.substring(pos + 1) : "";
    }

    // UUID를 활용하여 중복되지 않는 서버 저장용 파일명을 생성합니다.
    private String createStoreFileName(String originalFilename){
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString(); // 128비트 임의 난수 식별자 생성
        return uuid + "." + ext;
    }

    // 경로.파일이름
    @Override
    public String getFullPath(String fileName) {
        return getUploadPath().resolve(fileName).toString();
    }

    @Override
    public String storeFile(MultipartFile multipartFile) throws IOException {
        // 업로드된 파일 데이터가 없거나 비어있는 경우 진행하지 않음
        if(multipartFile == null || multipartFile.isEmpty()){
            return null;
        }
        // 사용자가 업로드한 원본 파일명
        String originalFilename = multipartFile.getOriginalFilename();
        // 서버에 저장할 겹치지 않는 유일한 파일명 생성
        String storeFileName = createStoreFileName(originalFilename);

        // 지정된 업로드 저장소 폴더가 없으면 자동으로 디렉터리 생성
        Path uploadPath = getUploadPath();
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        // 실제 지정 경로로 업로드 바이너리 파일 저장
        File destination = uploadPath.resolve(storeFileName).toFile();
        multipartFile.transferTo(destination);

        return storeFileName;

    }

    // 저장된 파일명을 기반으로 스프링이 바이너리를 읽을 수 있는 Resource 객체로 감싸서 반환합니다.
    @Override
    public Resource getFileResource(String storeFilename) throws IOException {
        return new UrlResource("file:" + getFullPath(storeFilename));
    }

    // MIME 타입 또는 파일 확장가 등을 검사하여 이미지 파일여부를 판별합니다.
    @Override
    public boolean isImage(String filename){
        if(filename == null || filename.isEmpty()) {
            return false;
        }
        try {
            // 자바 표준 시스템을 통한 파일 MIME 타입 확인
            String contentType = Files.probeContentType(Paths.get(filename));
            if (contentType != null && contentType.startsWith("image/")) {
                return true;
            }
        } catch (IOException ignored) {
        }
        // 예외 처리 상황 시 파일 확장자 기반으로 백업 검사
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                || lower.endsWith(".gif") || lower.endsWith(".webp") || lower.endsWith(".svg");
    }
}
