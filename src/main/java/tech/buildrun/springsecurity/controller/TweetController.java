package tech.buildrun.springsecurity.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.springsecurity.controller.dto.CreateTweetDto;
import tech.buildrun.springsecurity.controller.dto.FeedDto;
import tech.buildrun.springsecurity.controller.dto.FeedItemDto;
import tech.buildrun.springsecurity.entities.Role;
import tech.buildrun.springsecurity.entities.Tweet;
import tech.buildrun.springsecurity.repository.TweetRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.util.UUID;

@RestController
public class TweetController {

    private final TweetRepository tweetRepository;

    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto dto,
                                            JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(dto.content());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                        @RequestParam(value = "search", required = false) String search){

        Page<Tweet> tweets;

        if (search != null && !search.isBlank()) {
            tweets = tweetRepository.findByContentContainingIgnoreCaseOrUser_UsernameContainingIgnoreCase(
                    search, search,
                    PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp")
            );
        } else {
            tweets = tweetRepository.findAll(
                    PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp")
            );
        }

        var tweetDtos = tweets.map(tweet ->
                new FeedItemDto(
                        tweet.getTweetId(),
                        tweet.getContent(),
                        tweet.getUser().getUsername())
        );


        return ResponseEntity.ok(
                new FeedDto(
                        tweetDtos.getContent(),
                        page,
                        pageSize,
                        tweetDtos.getTotalPages(),
                        tweetDtos.getTotalElements()
                )
        );
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") long tweetId,
                                            JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = tweetRepository.findById(tweetId)
                        .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.admin.name()));

        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            tweetRepository.deleteById(tweetId);
            return  ResponseEntity.ok().build();
        }else {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }


}
