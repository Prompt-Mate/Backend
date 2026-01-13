package com.tave.PromptMate.common;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 기존 텍스트 업로드
    public String uploadContent(String content, String fileName) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain; charset=UTF-8");
        metadata.setContentLength(bytes.length);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        amazonS3Client.putObject(bucket, fileName, inputStream, metadata);

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 이미지 업로드 추가
    public String uploadImage(MultipartFile file, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 이미지 삭제
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;

        String fileName = imageUrl.substring(imageUrl.indexOf(".com/") + 5);
        amazonS3Client.deleteObject(bucket, fileName);
    }
}