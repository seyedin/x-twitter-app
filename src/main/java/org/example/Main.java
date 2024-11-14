package org.example;

import org.example.model.Retweet;
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

import java.util.List;
import java.util.Scanner;

public class Main {
    public static final TweetService tweetService = new TweetServiceImpl();
    public static final TagService tagService = new TagServiceImpl();
    public static final UserService userService = new UserServiceImpl();
    public static final ReTweetService reTweetService = new ReTweetServiceImpl();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            startMenu();
        }
    }

    private static void startMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=======================================");
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

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setDisplayName(displayName);
        user.setBio(bio);

        User registeredUser = userService.registerUser(user);
        if (registeredUser != null) {
            System.out.println("Sign Up successful!");
        } else {
            System.out.println("User already exists. Try again.");
        }
        startMenu();
    }

    private static void login(Scanner scanner) {
        System.out.print("Would you like to login with your username (1)  or email (2) ? ");
        int choice = Integer.parseInt(scanner.next());

        String username = "";
        String email = "";
        switch (choice) {
            case 1:
                System.out.print("Please enter your username: ");
                username = scanner.next();
                break;
            case 2:
                System.out.print("Please enter your email address: ");
                email = scanner.next();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                login(scanner);
        }

        System.out.print("Enter password: ");
        String password = scanner.next();


        User user = userService.loginUser(username, password, email);
        if (user != null) {
                System.out.println("Login successful!");
            userDashboard(scanner, user);
            } else {
                System.out.println("Login failed. Try again.");
                startMenu();
            }
    }

    public static void userDashboard(Scanner scanner, User user) {
        System.out.println("\n=======================================");
        System.out.println("User Dashboard:");
        System.out.println("(1) Tweets Dashboard");
        System.out.println("(2) Update your profile");
        System.out.println("(3) Logout");
        System.out.print("Choose an option: ");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                tweetsDashboard(scanner, user);
                break;
            case 2:
                updateDashboard(scanner, user);
                break;
            case 3:
                System.out.println("Logged out successfully!\n");
                startMenu();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                userDashboard(scanner, user);
        }
        userDashboard(scanner, user);
    }

    private static void updateDashboard(Scanner scanner, User user) {
        System.out.println("\n=======================================");
        System.out.println("Update profile");
        System.out.println("(1) Update Password");
        System.out.println("(2) Update Username");
        System.out.println("(3) Update Bio");
        System.out.println("(4) Update Display Name");
        System.out.println("(5) Back to user Profile");
        System.out.print("Choose an option: ");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                System.out.print("Enter your new password: ");
                String newPassword = scanner.next();
                user.setPassword(newPassword);
                userService.updatePassword(user.getUserId(), user.getPassword());
                System.out.print("Updated password successfully!");
                break;
            case 2:
                System.out.print("Enter your new username: ");
                String newUsername = scanner.next();
                user.setUsername(newUsername);
                userService.updateUsername(user.getUserId(), user.getUsername());
                System.out.print("Updated username successfully!");
                break;
            case 3:
                System.out.print("Enter your new bio: ");
                String newBio = scanner.next();
                user.setBio(newBio);
                userService.updateBio(user.getUserId(), user.getBio());
                System.out.print("Updated bio successfully!");
                break;
            case 4:
                System.out.print("Enter your new display name: ");
                String newDisplayName = scanner.next();
                user.setDisplayName(newDisplayName);
                userService.updateDisplayName(user.getUserId(), user.getDisplayName());
                System.out.println("Updated display name successfully!");
                break;
            case 5:
                System.out.println("Back to user Profile.");
                userDashboard(scanner, user);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                updateDashboard(scanner, user);
        }
    }

    private static void tweetsDashboard(Scanner scanner, User user) {
        System.out.println("=======================================");
        System.out.println("Tweet dashboard");
        System.out.println("(1) Post a new tweet");
        System.out.println("(2) Post a retweet");
        System.out.println("(3) Delete tweeted posts");
        System.out.println("(4) Edit tweeted posts");
        System.out.println("(5) Like tweet posts");
        System.out.println("(6) Dislike tweet posts");
        System.out.println("(7) View tweets dashboard");
        System.out.println("(8) Back to user Profile");
        System.out.print("Choose an option: ");

        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                postNewTweet(scanner, user);
                break;
            case 2:
                postRetweet(scanner, user);
                break;
            case 3:
                deleteTweetedPosts(scanner, user);
                break;
            case 4:
                editTweetedPosts(scanner, user);
                break;
            case 5:
                likeTweet(scanner, user);
                break;
            case 6:
                dislikeTweetPosts(scanner, user);
                break;
            case 7:
                viewTweetsDashboard(scanner, user);
                break;
            case 8:
                System.out.println("Back to user profile.");
                userDashboard(scanner, user);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                tweetsDashboard(scanner, user);
        }
    }

    private static void viewTweetsDashboard(Scanner scanner, User user) {
        System.out.println("\n=======================================");
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
                viewTweetsOfOtherUsers(scanner, user);
                break;
            case 3:
                viewTweetByFilterTag(scanner, user);
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

    public static void likeTweet(Scanner scanner, User user) {
        List<Tweet> tweets = tweetService.getAllTweets();
        System.out.println("Select tweet Id:");
        for (Tweet tweet : tweets) {
            System.out.println("Id: " + tweet.getId() + ", Content: " + tweet.getContent());
        }
        int tweetId = Integer.parseInt(scanner.next());
        Integer id = tweetService.likeTweet(user.getUserId(), tweetId);
        if (id != null) {
            System.out.println("Like");
        } else {
            System.out.println("Failed to like");
        }
        tweetsDashboard(scanner, user);
    }

    public static void dislikeTweetPosts(Scanner scanner, User user) {
        List<Tweet> tweets = tweetService.getAllTweets();
        System.out.println("Select tweet Id:");
        for (Tweet tweet : tweets) {
            System.out.println("Id: " + tweet.getId() + ", Content: " + tweet.getContent());
        }
        int tweetId = Integer.parseInt(scanner.next());
        Integer id = tweetService.dislikeTweet(user.getUserId(), tweetId);
        if (id != null) {
            System.out.println("disLike");
        } else {
            System.out.println("Failed to dislike");
        }
        tweetsDashboard(scanner, user);
    }

    public static void editTweetedPosts(Scanner scanner, User user) {

        List<Tweet> tweets = tweetService.getAllTweets();
        for (Tweet tweet : tweets) {
            System.out.println("Id: " + tweet.getId() + ", Content: " + tweet.getContent());
        }

        System.out.print("Enter Tweet ID to Edit:");
        int tweetId = Integer.parseInt(scanner.next());

        System.out.print("Enter new content: ");
        String newContent = scanner.next();
        boolean result = tweetService.updateTweet(tweetId, newContent);

        if (result) {
            System.out.println("Tweet updated successfully!");
            tweetsDashboard(scanner, user);
        } else {
            System.out.println("Failed to update tweet. Please try again.");
            editTweetedPosts(scanner, user);
        }
    }

    public static void postRetweet(Scanner scanner, User user) {
        List<Tweet> tweets = tweetService.getAllTweets();
        String newContent = "";
        System.out.println("Select id of tweet for retweet: ");
        for (Tweet tweet : tweets) {
            System.out.println("Id: " + tweet.getId() + ", Content: " + tweet.getContent());
        }
        int selectedTweetId = Integer.parseInt(scanner.next());
        System.out.println("Do you want to change the content? Yes(1) / No(2).");
        int choice = Integer.parseInt(scanner.next());
        switch (choice) {
            case 1:
                System.out.println("Enter your new content: ");
                newContent = scanner.next();
                break;
            case 2:
                newContent = "";
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                postRetweet(scanner, user);
        }
        reTweetService.postReTweet(user.getUserId(), selectedTweetId, null, newContent);
        System.out.println("Post retweeted is successful.");
        tweetsDashboard(scanner, user);
    }

    public static void postNewTweet(Scanner scanner, User user) {
        System.out.println("Adding a New Tweet:");
        System.out.print("Enter content: ");
        String content = scanner.next();

        System.out.print("Enter tag: ");
        String tagName = scanner.next();

        Tweet tweet = tweetService.postTweet(user.getUserId(), content);

        tagService.addTagToTweet(tweet.getId(), tagName);

        System.out.println("Tweet added successfully!");
        tweetsDashboard(scanner, user);
    }

    private static void deleteTweetedPosts(Scanner scanner, User user) {
        List<Tweet> tweets = tweetService.getAllTweets();
        for (Tweet tweet : tweets) {
            System.out.println("Id: " + tweet.getId() + ", Content: " + tweet.getContent());
        }
        System.out.print("Enter tweet ID to delete: ");
        int tweetId = Integer.parseInt(scanner.next());

        boolean success = tweetService.deleteTweetById(tweetId);
        if (success) {
            System.out.println("Tweet deleted successfully!");
            tweetsDashboard(scanner, user);
        } else {
            System.out.println("Failed to delete tweet. Please try again.");
            deleteTweetedPosts(scanner, user);
        }
    }

    public static void viewMyAllTweetedPosts(Scanner scanner, User user) {
        List<Tweet> tweets = tweetService.getTweetsAndRetweetsByUserId(user.getUserId());

        for (Tweet tweet : tweets) {
            System.out.println("----------------------------");
            System.out.println("Tweet content: " + tweet.getContent());
            List<Retweet> retweets = tweet.getRetweets();

            if (retweets != null && !retweets.isEmpty()) {
                for (Retweet retweet : retweets) {
                    if (retweet.getAdditionalContent() != null && !retweet.getAdditionalContent().isEmpty()) {
                        System.out.println("----------------------------");
                        System.out.println("Retweet content: " + retweet.getAdditionalContent());
                    }

                    List<Retweet> childRetweets = retweet.getChildRetweets();
                    if (childRetweets != null) {
                        for (Retweet childRetweet : childRetweets) {
                            if (childRetweet.getAdditionalContent() != null && !childRetweet.getAdditionalContent().isEmpty()) {
                                System.out.println("----------------------------");
                                System.out.println("Retweet of Retweet content: " + childRetweet.getAdditionalContent());
                            }
                        }
                    }
                }
            }
        }
        viewTweetsDashboard(scanner, user);
    }

    public static void viewTweetsOfOtherUsers(Scanner scanner, User user) {
        List<Tweet> tweets = tweetService.getAllTweetsAndRetweets();

        for (Tweet tweet : tweets) {

            // نمایش محتوای توییت فقط در صورتی که خالی یا null نباشد
            if (tweet.getContent() != null && !tweet.getContent().isEmpty()) {
                System.out.println("\nTweet content: " + tweet.getContent());
            }

            List<Retweet> retweets = tweet.getRetweets();
            if (retweets != null && !retweets.isEmpty()) {
                for (Retweet retweet : retweets) {

                    // نمایش محتوای ری‌توییت فقط در صورتی که خالی یا null نباشد
                    if (retweet.getAdditionalContent() != null && !retweet.getAdditionalContent().isEmpty()) {
                        System.out.println("\nRetweet content: " + retweet.getAdditionalContent());
                    }

                    List<Retweet> childRetweets = retweet.getChildRetweets();
                    if (childRetweets != null && !childRetweets.isEmpty()) {
                        for (Retweet childRetweet : childRetweets) {

                            // نمایش محتوای ری‌توییت از ری‌توییت فقط در صورتی که خالی یا null نباشد
                            if (childRetweet.getAdditionalContent() != null && !childRetweet.getAdditionalContent().isEmpty()) {
                                System.out.println("\nRetweet of Retweet content: " + childRetweet.getAdditionalContent());
                            }
                        }
                    }
                }
            }
        }
        viewTweetsDashboard(scanner, user);
    }

    public static void viewTweetByFilterTag(Scanner scanner, User user) {
        System.out.println("list of tags: ");
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Enter your tag name: ");
        String tagName = scanner.next();
        List<Tweet> tweetsByTag = tweetService.getTweetsByTag(tagName);
        for (Tweet tweet : tweetsByTag) {
            System.out.println("Tweet content: " + tweet.getContent());
        }
        viewTweetsDashboard(scanner, user);
    }
}
