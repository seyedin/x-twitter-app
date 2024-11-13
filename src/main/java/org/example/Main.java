/*
//package org.example;
//
//
//import org.example.model.Retweet;
//import org.example.model.Tweet;
//import org.example.service.TagService;
//import org.example.service.TweetService;
//import org.example.service.UserService;
//import org.example.service.impl.TagServiceImpl;
//import org.example.service.impl.TweetServiceImpl;
//import org.example.service.impl.UserServiceImpl;
//import org.example.util.Utils;
//
//import java.security.NoSuchAlgorithmException;
//import java.util.List;
//
//public class Main {
//    public static final TweetService tweetService = new TweetServiceImpl();
//    public static final TagService tagService = new TagServiceImpl();
//    public static final UserService userService = new UserServiceImpl();
//
//    public static void main2(String[] args) {
//
//        List<Tweet> tweets = tweetService.getTweetsAndRetweetsByUserId(1);
//
//        for (Tweet tweet : tweets) {
//            System.out.println("Tweet: " + tweet.getContent());
//            for (Retweet retweet : tweet.getRetweets()) {
//                System.out.println("Retweet: " + retweet.getAdditionalContent());
//                for (Retweet childRetweet : retweet.getChildRetweets()) {
//                    System.out.println("Retweet of Retweet: " + childRetweet.getAdditionalContent());
//                }
//            }
//        }
//    }
//
//    public static void main(String[] args) throws NoSuchAlgorithmException {
//
//        System.out.println(Utils.hashPassword("123"));
//    }
//}

package org.example;


import org.example.model.Tag;
import org.example.model.Tweet;
import org.example.model.User;
import org.example.service.ReTweetService;
import org.example.service.TagService;
import org.example.service.TweetService;
import org.example.service.UserService;

import org.example.service.impl.ReTweetServiceImpl;
import org.example.service.impl.TagServiceImpl;
import org.example.service.impl.TweetServiceImpl;
import org.example.service.impl.UserServiceImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final TweetService tweetService = new TweetServiceImpl();
    public static final TagService tagService = new TagServiceImpl();
    public static final UserService userService = new UserServiceImpl();
    public static final ReTweetService reTweetService = new ReTweetServiceImpl();

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            startMenu();
        }
    }

    private static void startMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Application!");
        System.out.println("1. SignUp");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                signUp(scanner);
                break;
            case 2:
                login(scanner);
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void signUp(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter email: ");
        String email = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();
        System.out.print("Enter display name: ");
        String displayName = scanner.next();
        System.out.print("Enter Bio: ");
        String bio = scanner.next();
        User user = new User(username, email, password, displayName, bio, LocalDateTime.now());
        User registeredUser = userService.registerUser(user);
        if (registeredUser != null) {
            System.out.println("Sign Up successful!");
        } else {
            System.out.println("User already exists. Try again.");
        }
        startMenu();
    }

    private static void login(Scanner scanner) {
        System.out.print("Enter username or email: ");
        String usernameOrEmail = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();
        try {
            boolean isLoggedIn = userService.loginUser(usernameOrEmail, password, usernameOrEmail);
            if (isLoggedIn) {
                System.out.println("Login successful!");
                userDashboard(user, scanner);
            } else {
                System.out.println("Login failed. Try again.");
                startMenu();
            }
        } catch (SQLException e) {
            System.out.println("An error occurred during login. Please try again.");
            e.printStackTrace();
        }
    }

    public static void userDashboard(User user, Scanner scanner) throws SQLException {
        System.out.println("\nUser Dashboard:");
        System.out.println("(1) Tweets Dashboard");
        System.out.println("(2) Update your profile");
        System.out.println("(3) Logout");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                tweetsDashboard(user, scanner);
            case 2:
                updateDashboard(user, scanner);
                break;
            case 3:
                System.out.println("Logged out successfully!");
                startMenu();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                userDashboard(user, scanner);
        }
        userDashboard(user, scanner);
    }

    private static void updateDashboard(User user, Scanner scanner) throws SQLException {
        System.out.println("Update profile");
        System.out.println("(1) Update Password");
        System.out.println("(2) Update Username");
        System.out.println("(3) Update Bio");
        System.out.println("(4) Update Display Name");
        System.out.println("(5) Back to user Profile");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                System.out.println("Enter your new password: ");
                String newPassword = scanner.next();
                user.setPassword(newPassword);
                userService.updateUserProfile(user.getUserId(), user.getDisplayName(), user.getBio(), user.getUsername(), user.getPassword());
                break;
            case 2:
                System.out.println("Enter your new username: ");
                String newUsername = scanner.next();
                user.setUsername(newUsername);
                userService.updateUserProfile(user.getUserId(), user.getDisplayName(), user.getBio(), user.getUsername(), user.getPassword());
                break;
            case 3:
                System.out.println("Enter your new bio: ");
                String newBio = scanner.next();
                user.setBio(newBio);
                userService.updateUserProfile(user.getUserId(), user.getDisplayName(), user.getBio(), user.getUsername(), user.getPassword());
                break;
            case 4:
                System.out.println("Enter your new display name: ");
                String newDisplayName = scanner.next();
                user.setDisplayName(newDisplayName);
                userService.updateUserProfile(user.getUserId(), user.getDisplayName(), user.getBio(), user.getUsername(), user.getPassword());
                break;
            case 5:
                System.out.println("Back to user Profile.");
                userDashboard(user, scanner);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                updateDashboard(user, scanner);
        }
    }

    private static void tweetsDashboard(Scanner scanner, User user) throws SQLException {
        System.out.println("Tweet dashboard");
        System.out.println("(1) Post a new tweet");
        System.out.println("(3) Post a retweet");
        System.out.println("(3) Delete tweeted posts");
        System.out.println("(4) Edit tweeted posts");
        System.out.println("(5) Edit retweeted posts");
        System.out.println("(6) Like tweet posts");
        System.out.println("(7) Dislike tweet posts");
        System.out.println("(8) View tweets dashboard");
        System.out.println("(9) Back to user Profile");
        System.out.print("Choose an option: ");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                postNewTweet(scanner, user.getUserId());
                break;
            case 2:

                break;
            case 3:
                deleteTweetedPosts(scanner);
                break;
            case 4:
                editTweetedPosts(scanner, user);
                break;
            case 5:
                editReTweetedPosts();
                break
            case 6:
                tweetService.likeTweet(user.getUserId(),);
                break
            case 7:
                dislikeTweetPosts();
                break;
            case 8:
                viewTweetsDashboard(scanner, user);
                break;
            case 9:
                System.out.println("Back to user Profile.");
                userDashboard(user, scanner);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                tweetsDashboard(scanner, user);
        }
    }

    public static void postNewTweet(Scanner scanner, int userId) {
        System.out.println("Adding a New Tweet:");
        System.out.print("Enter content: ");
        String content = scanner.next();

        List<Tweet> allTweets = tweetService.getTweetByUserId(userId);
        Tweet newTweet = new Tweet(allTweets.size() + 1, content);

        System.out.println("Enter tags (comma separated). If the tag doesn't exist, it will be added:");
        scanner.nextLine();
        String tagInput = scanner.nextLine();
        String[] tagNames = tagInput.split(",");
        for (String tagName : tagNames) {
            List<Tag> tag = tagService.findAllTag(tagName.trim());
            newTweet.addTag(tag);
        }
        tweetService.addTweet(newTweet, userId);
        System.out.println("Article added successfully!");
    }

    private static void deleteTweetedPosts(Scanner scanner) throws SQLException {
        System.out.print("Enter tweet ID to delete: ");
        int tweetId = Integer.parseInt(scanner.next());
        boolean success = tweetService.deleteTweetById(tweetId);
        if (success) {
            System.out.println("Tweet deleted successfully!");
        } else {
            System.out.println("Failed to delete tweet. Please try again.");
        }
    }

    public static void editTweetedPosts(Scanner scanner, User user) throws SQLException {
        System.out.println("Enter Tweet ID to Edit:");

        List<Tweet> allTweets = tweetService.getTweetByUserId(user.getUserId());
        for (Tweet tweet : allTweets) {
            if (!tweet.isPosted()) {
                System.out.println("Id: " + tweet.getId() + ", Content: " + tweet.getContent());
            }
        }

        int idtwiet=3;

        int tweetId = Integer.parseInt(scanner.next());
        Tweet tweet = tweetService.findTweetById(tweetId);

        if (tweet != null) {
            System.out.println("Editing Tweet: " + tweet.getContent());
            System.out.print("Enter new content: ");
            String newContent = scanner.next();

            tweetService.updateTweet(tweet.getId(), newContent);
        } else {
            System.out.println("Article not found.");
        }
    }

    private static void viewTweetsDashboard(Scanner scanner, User user) throws SQLException {
        System.out.println("View Dashboard");
        System.out.println("(1) View my all tweeted posts");
        System.out.println("(2) View tweets of other users");
        System.out.println("(3) View by filter Tag");
        System.out.println("(4) Back to tweets dashboard");
        System.out.print("Choose an option: ");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                viewMyAllTweetedPosts(scanner, user);
                break;
            case 2:
                viewTweetsOfOtherUsers
                break;
            case 3:
                viewByFilterTag
                break;
            case 4:
                System.out.println("Back to tweets dashboard");
                tweetsDashboard(scanner, user);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                viewTweetsDashboard(scanner, user);
        }
    }

    public static void viewMyAllTweetedPosts(Scanner scanner, User user) throws SQLException {
        List<Tweet> myTweets = tweetService.getTweetByUserId(user.getUserId());
        if (myTweets.isEmpty()) {
            System.out.println("No Tweet found.");
            userDashboard(user, scanner);
        } else {
            System.out.println("All My Tweets: ");
            for (int i = 0; i < myTweets.size(); i++) {
                Tweet tweet = myTweets.get(i);
                System.out.println((i + 1) + ". Content: " + tweet.getContent() + ", Tag: " + tweet.getTags() + ", Tweet Id: " + tweet.getId());
            }
            System.out.println("Enter the number of the tweet you want to view in detail, or 0 to go back:");
            int choice = Integer.parseInt(scanner.next());
            if (choice > 0 && choice <= myTweets.size()) {
                Tweet selectedTweet = myTweets.get(choice - 1);
                System.out.println("Content: " + selectedTweet.getContent());
                System.out.println("Retweets: " + selectedTweet.getRetweets());
                System.out.println("CreateDate: " + selectedTweet.getCreateDate());
                System.out.println("Tags: " + selectedTweet.getTags());
            }
        }
    }

    public static Tag addNewTag(Scanner scanner) throws SQLException {
        System.out.print("Enter new Tag Id: ");
        String newTagId = scanner.next();
        System.out.print("Enter new Tag name: ");
        String newTagName = scanner.next();

        Tag newTag = new Tag();
        newTag.setTagId(Integer.parseInt(newTagId));
        newTag.setName(newTagName);
        Tag tag = tagService.addTagToTweet(tweetId, tagName);
        return tag;
    }



}


            }*/
