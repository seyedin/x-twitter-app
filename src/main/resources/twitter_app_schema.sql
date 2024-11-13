-- SQL script to create tables and add test data

CREATE TABLE Users
(
    id           SERIAL PRIMARY KEY,
    username     VARCHAR(50) UNIQUE  NOT NULL,
    display_name VARCHAR(100)        NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    bio          TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Tweets
(
    id           SERIAL PRIMARY KEY,
    user_id      INT REFERENCES Users (id) ON DELETE CASCADE,
    content      VARCHAR(280) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Tags
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE Tweet_Tag
(
    tweet_id INT REFERENCES Tweets(id) ON DELETE CASCADE,
    tag_id   INT REFERENCES Tags (id) ON DELETE CASCADE,
    PRIMARY KEY (tweet_id, tag_id)
);

CREATE TABLE Retweets
(
    id                 SERIAL PRIMARY KEY,
    user_id            INT REFERENCES Users (id) ON DELETE CASCADE,
    original_tweet_id  INT REFERENCES Tweets(id) ON DELETE CASCADE,
    parent_retweet_id  INT REFERENCES Retweets (id) ON DELETE CASCADE,
    additional_content VARCHAR(280),
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Likes
(
    id       SERIAL PRIMARY KEY,
    user_id  INT REFERENCES Users (id) ON DELETE CASCADE,
    tweet_id INT REFERENCES Tweets (id) ON DELETE CASCADE
);

CREATE TABLE DisLikes
(
    id       SERIAL PRIMARY KEY,
    user_id  INT REFERENCES Users (id) ON DELETE CASCADE,
    tweet_id INT REFERENCES Tweets (id) ON DELETE CASCADE
);

-- Test data
INSERT INTO Users (username, display_name, email, password, bio)
VALUES ('user1', 'User One', 'user1@example.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'This is User One bio.'),
       ('user2', 'User Two', 'user2@example.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'This is User Two bio.'),
       ('user3', 'User Three', 'user3@example.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'This is User Three bio.'),
       ('user4', 'User Four', 'user4@example.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'This is User Four bio.');

INSERT INTO Tweets (user_id, content)
VALUES (1, 'This is a tweet from User One.'),
       (2, 'This is a tweet from User Two.'),
       (3, 'This is a tweet from User Three.'),
       (4, 'This is a tweet from User Four.');

INSERT INTO Tags (name)
VALUES ('Java'),
       ('Programming'),
       ('Tech');

INSERT INTO Tweet_Tag (tweet_id, tag_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 3);

INSERT INTO Retweets (user_id, original_tweet_id, parent_retweet_id, additional_content)
VALUES (1, 2, NULL, 'Retweeting tweet of User Two by User One.'),
       (3, 1, NULL, 'Retweeting tweet of User One by User Three.'),
       (2, NULL, 1, 'Retweeting a retweet from User Three.');

INSERT INTO Likes (user_id, tweet_id)
VALUES (1, 3),
       (2, 1),
       (3, 2);

INSERT INTO DisLikes (user_id, tweet_id)
VALUES (4, 3);

