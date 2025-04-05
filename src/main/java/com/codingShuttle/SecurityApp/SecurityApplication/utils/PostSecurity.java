package com.codingShuttle.SecurityApp.SecurityApplication.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.codingShuttle.SecurityApp.SecurityApplication.dto.PostDTO;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.services.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostSecurity {
    
    private final PostService postService;

    public boolean isOwnerOfPost(Long postId){
        // Get the current user from the security context
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostDTO postDTO = postService.getPostById(postId);
        return postDTO.getUserDto().getId().equals(user.getId());
    }
}
