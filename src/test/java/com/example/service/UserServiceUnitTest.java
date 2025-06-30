package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@example.com", 30);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createUser_ShouldReturnId() {
        // Given
        when(userDao.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return null;
        });

        // When
        Long id = userService.createUser("Test", "test@email.com", 25);

        // Then
        assertNotNull(id);
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        // Given
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        // Given
        when(userDao.findAll()).thenReturn(List.of(testUser));

        // When
        List<User> users = userService.getAllUsers();

        // Then
        assertEquals(1, users.size());
        assertEquals(testUser, users.get(0));
    }

    @Test
    void updateUser_WhenExists_ShouldUpdateFields() {
        // Given
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.updateUser(1L, "New Name", "new@email.com", 35);

        // Then
        verify(userDao).update(argThat(user ->
                user.getName().equals("New Name") &&
                        user.getEmail().equals("new@email.com") &&
                        user.getAge() == 35
        ));
    }

    @Test
    void deleteUser_WhenExists_ShouldReturnTrue() {
        // Given
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertTrue(result);
        verify(userDao, times(1)).delete(1L);
    }

    @Test
    void deleteUser_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertFalse(result);
        verify(userDao, never()).delete(anyLong());
    }

    @Test
    void updateUser_WhenNotExists_ShouldDoNothing() {
        // Given
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        // When
        userService.updateUser(1L, "New Name", "new@email.com", 35);

        // Then
        verify(userDao, never()).update(any(User.class));
    }
}