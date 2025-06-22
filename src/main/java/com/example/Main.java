package com.example;

import com.example.dao.UserDao;
import com.example.dao.UserDaoImpl;
import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            logger.info("Application started");
            showMenu();
            HibernateUtil.shutdown();
        } catch (Exception e) {
            logger.error("Critical error occurred: ", e);
            System.err.println("A critical error occurred. See logs for details.");
        } finally {
            scanner.close();
        }
    }

    private static void showMenu() {
        while (true) {
            System.out.println("\nUser Management System");
            System.out.println("1. Create User");
            System.out.println("2. Find User by ID");
            System.out.println("3. List All Users");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> createUser();
                case 2 -> findUserById();
                case 3 -> listAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createUser() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter age: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = new User(name, email, age);
            userDao.save(user);
            System.out.println("User created successfully with ID: " + user.getId());
            logger.info("Created new user: {}", user);
        } catch (Exception e) {
            logger.error("Error creating user: ", e);
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private static void findUserById() {
        try {
            System.out.print("Enter user ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                System.out.println("User found: " + user.get());
            } else {
                System.out.println("User not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error finding user: ", e);
            System.out.println("Error finding user: " + e.getMessage());
        }
    }

    private static void listAllUsers() {
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }
            System.out.println("List of Users:");
            users.forEach(System.out::println);
        } catch (Exception e) {
            logger.error("Error listing users: ", e);
            System.out.println("Error listing users: " + e.getMessage());
        }
    }

    private static void updateUser() {
        try {
            System.out.print("Enter user ID to update: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> userOpt = userDao.findById(id);
            if (userOpt.isEmpty()) {
                System.out.println("User not found with ID: " + id);
                return;
            }

            User user = userOpt.get();
            System.out.println("Current user details: " + user);

            System.out.print("Enter new name (leave blank to keep current): ");
            String name = scanner.nextLine();
            if (!name.isBlank()) {
                user.setName(name);
            }

            System.out.print("Enter new email (leave blank to keep current): ");
            String email = scanner.nextLine();
            if (!email.isBlank()) {
                user.setEmail(email);
            }

            System.out.print("Enter new age (leave blank to keep current): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.isBlank()) {
                user.setAge(Integer.parseInt(ageInput));
            }

            userDao.update(user);
            System.out.println("User updated successfully.");
            logger.info("Updated user: {}", user);
        } catch (Exception e) {
            logger.error("Error updating user: ", e);
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("Enter user ID to delete: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                userDao.delete(id);
                System.out.println("User deleted successfully.");
                logger.info("Deleted user with ID: {}", id);
            } else {
                System.out.println("User not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error deleting user: ", e);
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}