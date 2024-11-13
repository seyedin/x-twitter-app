package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Retweet {
    private int retweetId;
    private User user;
    private Tweet originalTweetId;
    private Retweet parentRetweetId;
    private String additionalContent;
    private LocalDateTime createDate;
    private List<Retweet> childRetweets;

}
