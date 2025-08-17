package com.augmentative.communication.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A utility class to create a mock MultipartFile from a byte array or a local file.
 * This is useful for programmatic file uploads, such as seeding initial data,
 * where a real MultipartFile from an HTTP request is not available.
 */
public class InMemoryMultipartFile implements MultipartFile {

    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    /**
     * Creates an InMemoryMultipartFile from a byte array.
     * @param content The byte array representing the file content.
     * @param originalFilename The original filename (e.g., "image.png").
     * @param contentType The content type (e.g., "image/png").
     */
    public InMemoryMultipartFile(byte[] content, String originalFilename, String contentType) {
        this.content = content;
        this.name = originalFilename;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    /**
     * Creates an InMemoryMultipartFile from a local file Path.
     * @param filePath The path to the local file.
     * @param contentType The content type (e.g., "image/png").
     * @throws IOException If an I/O error occurs reading the file.
     */
    public InMemoryMultipartFile(Path filePath, String contentType) throws IOException {
        this.content = Files.readAllBytes(filePath);
        this.name = filePath.getFileName().toString();
        this.originalFilename = filePath.getFileName().toString();
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), content);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        Files.write(dest, content);
    }
}