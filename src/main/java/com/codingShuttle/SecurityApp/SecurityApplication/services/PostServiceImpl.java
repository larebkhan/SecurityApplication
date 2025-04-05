package com.codingShuttle.SecurityApp.SecurityApplication.services;

import com.codingShuttle.SecurityApp.SecurityApplication.dto.PostDTO;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.PostEntity;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.codingShuttle.SecurityApp.SecurityApplication.repositories.PostRepository;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PostDTO> getAllPosts() {

        return postRepository
                .findAll()
                .stream()
                .map(postEntity -> modelMapper.map(postEntity, PostDTO.class))
                .collect(Collectors.toList());
    }

//    @Override
//    public PostDTO createNewPost(PostDTO inputPost) {
//        // Map PostDTO to PostEntity
//        PostEntity postEntity = modelMapper.map(inputPost, PostEntity.class);
//
//        // Save the PostEntity
//        PostEntity savedPostEntity = postRepository.save(postEntity);
//
//        // Map the saved PostEntity back to PostDTO
//        PostDTO result = modelMapper.map(savedPostEntity, PostDTO.class);
//
//        return result;
//    }
    @Override
    public PostDTO createNewPost(PostDTO inputPost) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostEntity postEntity = modelMapper.map(inputPost, PostEntity.class);
        postEntity.setAuthor(user);
        return modelMapper.map(postRepository.save(postEntity),
                PostDTO.class);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        PostEntity postEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id "+postId));
        return modelMapper.map(postEntity, PostDTO.class);
    }

    @Override
    public PostDTO updatePost(PostDTO inputPost, Long postId) {
        PostEntity olderPost = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found with id "+postId));
        inputPost.setId(postId);
        modelMapper.map(inputPost, olderPost);
        PostEntity savedPostEntity = postRepository.save(olderPost);
        return modelMapper.map(savedPostEntity, PostDTO.class);
    }
}
