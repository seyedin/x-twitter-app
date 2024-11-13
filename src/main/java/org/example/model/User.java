package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userId;
    private String username;
    private String displayName;
    private String email;
    private String password;
    private String bio;
    private LocalDateTime createDate;
    private List<Tweet> tweets;
    private List<Retweet> retweets;

}