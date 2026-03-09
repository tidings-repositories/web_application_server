package com.delivalue.tidings.domain.topic.service;

import com.delivalue.tidings.domain.data.entity.Post;
import com.delivalue.tidings.domain.data.entity.PostTopic;
import com.delivalue.tidings.domain.data.entity.Topic;
import com.delivalue.tidings.domain.data.repository.PostRepository;
import com.delivalue.tidings.domain.data.repository.PostTopicRepository;
import com.delivalue.tidings.domain.data.repository.TopicRepository;
import com.delivalue.tidings.domain.topic.dto.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final PostTopicRepository postTopicRepository;
    private final PostRepository postRepository;

    public List<TopicResponse> getAllTopics(String category) {
        List<Topic> topics = category != null
                ? topicRepository.findAllByCategoryAndIsActiveTrue(category)
                : topicRepository.findAllByIsRecommendableTrue();
        return topics.stream().map(TopicResponse::new).toList();
    }

    public void tagPostWithTopic(String internalId, String postId, Long topicId) {
        Optional<Post> post = postRepository.findByIdAndDeletedAtIsNull(postId);
        if (post.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!post.get().getInternalUserId().equals(internalId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (!topicRepository.existsById(topicId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (postTopicRepository.existsByPostIdAndTopicId(postId, topicId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        PostTopic postTopic = PostTopic.builder()
                .postId(postId)
                .topicId(topicId)
                .tagSource("USER")
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        postTopicRepository.insert(postTopic);
    }

    public void untagPostFromTopic(String internalId, String postId, Long topicId) {
        Optional<Post> post = postRepository.findByIdAndDeletedAtIsNull(postId);
        if (post.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!post.get().getInternalUserId().equals(internalId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<PostTopic> postTopics = postTopicRepository.findAllByPostId(postId);
        PostTopic target = postTopics.stream()
                .filter(pt -> topicId.equals(pt.getTopicId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        postTopicRepository.delete(target);
    }

    public List<TopicResponse> getPostTopics(String postId) {
        List<Long> topicIds = postTopicRepository.findAllByPostId(postId).stream()
                .map(PostTopic::getTopicId)
                .toList();
        return topicRepository.findAllById(topicIds).stream()
                .map(TopicResponse::new)
                .toList();
    }
}
