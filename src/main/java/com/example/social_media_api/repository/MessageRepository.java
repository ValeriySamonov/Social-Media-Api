package com.example.social_media_api.repository;

import com.example.social_media_api.model.Message;
import com.example.social_media_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtDesc(User sender, User receiver, User sender2, User receiver2);

    List<Message> findBySenderIdAndReceiverId(long SenderId, long ReceiverId);
}
