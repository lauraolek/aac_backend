package com.augmentative.communication.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for handling image storage and retrieval.
 * This is a mock implementation using the local filesystem.
 *
 * In a production environment, this service would be replaced with
 * an implementation that connects to a NoSQL database (as requested)
 * or a cloud storage provider like Amazon S3, Google Cloud Storage, etc.
 *
 * For a NoSQL database, the `saveImage` method would convert the
 * `MultipartFile` to a byte array and store it in the database with a
 * unique identifier. The `getImage` method would retrieve the byte array
 * using that identifier.
 */
@Service
public class ImageStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Generate a unique filename to prevent conflicts
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);
        System.out.println(filePath);
        // Save the file to the local filesystem
        Files.copy(file.getInputStream(), filePath);

        // Return a URL that can be used to retrieve the image
        // In a real scenario, this URL would point to a public-facing
        // endpoint or a CDN.
        return "/images/" + filename;
    }

    /**
     * Deletes an image file from storage given its URL.
     * This method extracts the filename from the URL and attempts to delete the file.
     * @param imageUrl The URL of the image to delete (e.g., "/api/images/uuid-filename.jpg").
     * @return true if the file was successfully deleted or didn't exist, false if an error occurred.
     */
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty() || !imageUrl.startsWith("/images/")) {
            System.out.println("Invalid image URL for deletion: " + imageUrl);
            return false;
        }

        String filename = imageUrl.substring("/images/".length());
        Path filePath = Paths.get(uploadDir).resolve(filename);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Deleted image file: " + filename);
                return true;
            } else {
                System.out.println("Image file not found for deletion: " + filename);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to delete image file " + filename + ": " + e.getMessage());
            return false;
        }
    }
}