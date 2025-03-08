package com.codingShuttle.SecurityApp.SecurityApplication.repositories;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.PostEntity;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
