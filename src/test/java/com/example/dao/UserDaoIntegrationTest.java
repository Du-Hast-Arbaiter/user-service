package com.example.dao;

import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        System.setProperty("test.mode", "true");
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        userDao = new UserDaoImpl();
    }

    @BeforeEach
    void clearDatabase() {

        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            session.createNativeQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testSaveAndFindUser() {

        User user = new User("Test User", "test@example.com", 30);

        userDao.save(user);
        Optional<User> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals(user.getName(), found.get().getName());
        assertEquals(user.getEmail(), found.get().getEmail());
        assertEquals(user.getAge(), found.get().getAge());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    void testUpdateUser() {
        User user = new User("Old Name", "old@email.com", 25);
        userDao.save(user);

        user.setName("New Name");
        user.setEmail("new@email.com");
        user.setAge(30);
        userDao.update(user);

        Optional<User> updated = userDao.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("New Name", updated.get().getName());
        assertEquals("new@email.com", updated.get().getEmail());
        assertEquals(30, updated.get().getAge());
    }

    @Test
    void testDeleteUser() {
        // Given
        User user = new User("To Delete", "delete@me.com", 99);
        userDao.save(user);
        Long id = user.getId();

        userDao.delete(id);

        assertFalse(userDao.findById(id).isPresent());
    }

    @Test
    void testFindAllUsers() {

        userDao.save(new User("Dan", "Dan@gmail.com", 20));
        userDao.save(new User("Oleg", "Oleg@yandex.ru", 25));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }
}