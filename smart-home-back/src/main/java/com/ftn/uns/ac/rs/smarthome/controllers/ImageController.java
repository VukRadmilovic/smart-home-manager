package com.ftn.uns.ac.rs.smarthome.controllers;

import com.ftn.uns.ac.rs.smarthome.utils.S3API;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    private final S3API s3;

    public ImageController(S3API s3) {
        this.s3 = s3;
    }

    @PostMapping(value = "/{name}", consumes = "multipart/form-data")
    public CompletionStage<ResponseEntity<String>> uploadImage(@PathVariable String name,
                                         @RequestPart("image") MultipartFile image,
                                         @RequestPart("type") String type) {
        try {
            File file = File.createTempFile("temp", null);
            var inputStream = image.getInputStream();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return s3.put("images", name, file, type)
                    .thenApply(lol -> ResponseEntity.ok().body("Uploaded"))
                    .exceptionally(e -> ResponseEntity.badRequest().body(e.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(value = "/{name}")
    public CompletionStage<ResponseEntity<String>> deleteImage(@PathVariable String name) {
        return s3.delete("images", name)
                .thenApply(lol -> ResponseEntity.ok().body("Deleted"))
                .exceptionally(e -> ResponseEntity.badRequest().body(e.getMessage()));
    }

    @GetMapping(value = "/{name}")
    public CompletionStage<ResponseEntity<String>> getImage(@PathVariable String name) {
        return s3.get("images", name)
                .thenApply(url -> ResponseEntity.ok().body(url))
                .exceptionally(e -> ResponseEntity.badRequest().body(e.getMessage()));
    }
}
