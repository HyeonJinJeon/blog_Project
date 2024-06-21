//package com.example.blog_project.controller;
//
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Controller
//public class ImageUploadController {
//    private static final String UPLOAD_DIR = "/Users/jeonhyeonjin/blog_project/";
//
//    @PostMapping("/Users/jeonhyeonjin/blog_project/")
//    @ResponseBody
//    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile upload) {
//        try {
//            // 파일 저장 경로 생성
//            if (!Files.exists(Paths.get(UPLOAD_DIR))) {
//                Files.createDirectories(Paths.get(UPLOAD_DIR));
//            }
//
//            // 고유한 파일명 생성
//            String fileName = UUID.randomUUID().toString() + "_" + upload.getOriginalFilename();
//            Path filePath = Paths.get(UPLOAD_DIR, fileName);
//
//            // 파일 저장
//            Files.write(filePath, upload.getBytes());
//
//            // CKEditor에서 필요한 JSON 반환
//            return ResponseEntity.ok(new CKEditorUploadResponse(1, "/uploads/" + fileName));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("파일 업로드 실패!");
//        }
//    }
//
//    private static class CKEditorUploadResponse {
//        private int uploaded;
//        private String fileName;
//        private String url;
//
//        public CKEditorUploadResponse(int uploaded, String url) {
//            this.uploaded = uploaded;
//            this.url = url;
//        }
//    }
//}
