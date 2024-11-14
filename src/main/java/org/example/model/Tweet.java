package org.example.model;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Tweet {
    private Integer id;
    private int userId;
    private String content;
    private LocalDateTime createDate;
    private List<Tag> tags;
    private List<Retweet> retweets;
}
