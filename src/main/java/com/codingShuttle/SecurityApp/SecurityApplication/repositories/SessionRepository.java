package com.codingShuttle.SecurityApp.SecurityApplication.repositories;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.Session;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {


    Optional<Session> findByRefreshToken(String token);

    Optional<Session> findById(Long userID);


    void deleteByUser(User user);

    List<Session> findByUser(User user);
}
