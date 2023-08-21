package com.example.social_media_api.repository;

import com.example.social_media_api.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderIdAndReceiverId(long SenderId, long ReceiverId);
}
