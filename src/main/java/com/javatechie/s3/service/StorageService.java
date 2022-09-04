package com.javatechie.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.javatechie.s3.config.UploadResponse;
import com.javatechie.s3.model.Image;
import com.javatechie.s3.repository.ImageRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.image.upload.url}")
    private String S3_FILE_UPLOAD_BASE_URL;

    @Autowired
    private ImageRepo imageRepository;

    @Autowired
    private AmazonS3 s3Client;

    public UploadResponse uploadFile(MultipartFile file) {
        UUID uuid = UUID.randomUUID();
        String id =String.valueOf(uuid);

        String fileName = id + "_" + file.getOriginalFilename();
        System.out.println("Filename: " + fileName);

        Image image = new Image();
        image.setName(fileName);
        image.setUniqueId(id);
        image.setCreatedDate(LocalDateTime.now());
        image.setLastUpdatedDate(LocalDateTime.now());
        image.setSize(String.valueOf(file.getSize()));
        image.setUrl(S3_FILE_UPLOAD_BASE_URL+fileName);
        String message = "";
        UploadResponse response = null;
        try {
            System.out.println("Entered try block");
            File fileObj = convertMultiPartFileToFile(file);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            fileObj.delete();
            imageRepository.save(image);
            System.out.println("Image saved in repository");
            message = "File uploaded Successfully: " + fileName;
            response = new UploadResponse();
            response.setUrl(image.getUrl());
            response.setSize(image.getSize());
            System.out.println(message + " . Exit try block");
        } catch (Exception ex) {
            System.out.println("Error uploading file: " + ex.getMessage());
            message = "Something Went Wrong";
            return response;
        }
        return response;
    }


    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }


    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
