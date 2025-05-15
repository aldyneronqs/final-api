package com.ronquillo.social_media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class SocialMediaController {

    @Autowired
    private SocialMediaRepository socialMediaRepository;

    // GET all posts
    @GetMapping
    public ResponseEntity<List<SocialMedia>> getAllPosts() {
        return ResponseEntity.ok(socialMediaRepository.findAllByOrderByCreatedAtDesc());
    }

    // GET post by ID
    @GetMapping("/{id}")
    public ResponseEntity<SocialMedia> getPostById(@PathVariable Long id) {
        return socialMediaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST to create a new post
    @PostMapping
    public ResponseEntity<SocialMedia> createPost(@RequestBody SocialMedia post) {
        SocialMedia savedPost = socialMediaRepository.save(post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    // PUT to update an existing post
    @PutMapping("/{id}")
    public ResponseEntity<SocialMedia> updatePost(@PathVariable Long id, @RequestBody SocialMedia updatedPost) {
        return socialMediaRepository.findById(id)
                .map(existingPost -> {
                    existingPost.setContent(updatedPost.getContent());
                    existingPost.setImageUrl(updatedPost.getImageUrl());
                    existingPost.setAuthor(updatedPost.getAuthor());

                    // Set current time for updatedAt
                    existingPost.setUpdatedAt(LocalDateTime.now());

                    // Save the updated post
                    SocialMedia savedPost = socialMediaRepository.save(existingPost);
                    return ResponseEntity.ok(savedPost);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE a post by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        if (socialMediaRepository.existsById(id)) {
            socialMediaRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Post with ID " + id + " was successfully deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Post with ID " + id + " not found.");
        }
    }

    // POST to create multiple posts (Bulk upload)
    @PostMapping("/bulk")
    public ResponseEntity<List<SocialMedia>> createBulkPosts(@RequestBody List<SocialMedia> posts) {
        // Save all posts in one go using saveAll()
        List<SocialMedia> savedPosts = socialMediaRepository.saveAll(posts);
        return new ResponseEntity<>(savedPosts, HttpStatus.CREATED);
    }
}
