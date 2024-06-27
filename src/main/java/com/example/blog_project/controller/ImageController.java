package com.example.blog_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class ImageController {

    private static final String UPLOAD_DIR = "/Users/jeonhyeonjin/blog_project/";

//    @GetMapping("/local-image")
//    public ResponseEntity<Resource> getImage(@RequestParam String filename) {
//        try {
//            Path file = Paths.get("/Users/jeonhyeonjin/blog_project/").resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//
//            if (resource.exists() || resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (MalformedURLException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    //post의 content를 CKEditor를 사용하여 이미지를 넣을 때 로컬에 업로드하고 CKEditor에 다시 불러오는 api
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile upload) {
        try {
            // 업로드할 디렉토리가 없으면 생성
            if (!Files.exists(Paths.get(UPLOAD_DIR))) {
                Files.createDirectories(Paths.get(UPLOAD_DIR));
            }
            String originalFileName = upload.getOriginalFilename();
            String fileName = originalFileName;

            // 파일 이름 중복 처리
            int count = 1;
            while (Files.exists(Paths.get(UPLOAD_DIR, fileName))) {
                String extension = "";
                int dotIndex = originalFileName.lastIndexOf(".");
                if (dotIndex != -1) {
                    extension = originalFileName.substring(dotIndex);
                    fileName = originalFileName.substring(0, dotIndex) + "_" + count + extension;
                } else {
                    fileName = originalFileName + "_" + count;
                }
                count++;
            }

            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // 파일 저장
            Files.write(filePath, upload.getBytes());

            // CKEditor에서 필요한 JSON 반환
            return ResponseEntity.ok().body(new CKEditorUploadResponse(1, "/Users/jeonhyeonjin/blog_project/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("파일 업로드 실패!");
        }
    }


    // CKEditor에서 필요한 JSON 응답을 위한 클래스(uploadImage에 사용)
    @RequiredArgsConstructor
    static class CKEditorUploadResponse {
        private int uploaded;
        private String url;

        public CKEditorUploadResponse(int uploaded, String url) {
            this.uploaded = uploaded;
            this.url = url;
        }

        public int getUploaded() {
            return uploaded;
        }

        public String getUrl() {
            return url;
        }
    }

    //이미지의 src가 /Users/jeonhyeonjin/blog_project/로 시작하는 이미지를 보여달라는 요청을 받는 api
    @GetMapping("/Users/jeonhyeonjin/blog_project/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // 파일 경로를 생성하고, 리소스를 로드
            Path file = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로가 잘못되었습니다.", e);
        }
    }

    private String extractFilename(String path) {
        return Paths.get(path).getFileName().toString();
    }
}
