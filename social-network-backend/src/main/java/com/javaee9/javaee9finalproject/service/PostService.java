package com.javaee9.javaee9finalproject.service;

import com.javaee9.javaee9finalproject.converter.PostConverter;
import com.javaee9.javaee9finalproject.dto.PostDto;
import com.javaee9.javaee9finalproject.entity.Post;
import com.javaee9.javaee9finalproject.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;

    private final PostConverter postConverter;

    public PostService(PostRepository postRepository, PostConverter postConverter) {
        this.postRepository = postRepository;
        this.postConverter = postConverter;
    }

    // TODO
    // migrate to dto
    // use @ExceptionHandler for dealing with internal issues
    public List<PostDto> readRecentPosts() {
        // 1). create boundary timestamp - in java
        ZonedDateTime boundary = ZonedDateTime.now(Clock.systemUTC()).minusDays(1);
        // 2). ask db of posts created after that boundary
        return readRecentPosts(boundary);
    }

    public List<PostDto> readRecentPosts(ZonedDateTime boundary) {
        var result = postRepository.queryAllRecentPosts(boundary);
        log.debug("result: {}", result);
        log.info("number of read post: [{}]", result.size());
        return result
                .stream()
//                .map(post -> postConverter.fromEntityToDto(post))
                .map(postConverter::fromEntityToDto)
                .collect(Collectors.toList());
    }

    // receipt
// 1. convert to entity from dto
// 2. store entity into db
// 3. return to client dto based on stored entity (with id and creationTimestamp and updateTimestamp)
    public PostDto createNewPost(PostDto toStore) {
        log.info("creating new post: [{}]", toStore);

        var entity = postConverter.fromDtoToEntity(toStore);
        postRepository.save(entity);
        var result = postConverter.fromEntityToDto(entity);

        log.info("created post: [{}]", result);
        return result;
    }
}