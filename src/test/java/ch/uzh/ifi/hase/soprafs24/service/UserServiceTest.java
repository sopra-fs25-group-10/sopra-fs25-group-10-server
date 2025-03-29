package ch.uzh.ifi.hase.soprafs24.service;

import java.util.Optional;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("testName");
    testUser.setUsername("testUsername");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void changePassword_validInputs_success() {
      // given: a user with existing password
      testUser.setPassword("oldPassword");
      Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
  
      // when: user changes password with correct current password
      userService.changePassword(testUser.getId(), "oldPassword", "newPassword123");
  
      // then: password should be updated and saved
      assertEquals("newPassword123", testUser.getPassword());
      Mockito.verify(userRepository).save(testUser);
  }

  @Test
  public void changePassword_incorrectCurrentPassword_throwsException() {
      // given: current password is incorrect
      testUser.setPassword("correctPassword");
      Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
  
      // when + then: expect 400 BAD_REQUEST
      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
          userService.changePassword(testUser.getId(), "wrongPassword", "newPassword")
      );
  
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
      assertTrue(exception.getReason().contains("Incorrect current password"));
  }

  @Test
  public void changePassword_emptyNewPassword_throwsException() {
      // given: new password is empty
      testUser.setPassword("correctPassword");
      Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
  
      // when + then: expect 400 BAD_REQUEST
      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
          userService.changePassword(testUser.getId(), "correctPassword", "")
      );
  
      assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
      assertTrue(exception.getReason().contains("New password must not be empty"));
  }

  @Test
  public void changePassword_userNotFound_throwsException() {
      // given: repository returns empty
      Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());
  
      // when + then: expect 404 NOT_FOUND
      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
          userService.changePassword(999L, "any", "new")
      );
  
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
      assertTrue(exception.getReason().contains("User not found"));
  }

  @Test
  public void userAuthenticate_invalidToken_throwsException() {
    // given
    Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(null);

    // when
    User freshUser = new User();
    freshUser.setUsername("testUsername");
    freshUser.setToken(testUser.getToken());

    // then
    ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.userAuthenticate(freshUser));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    assertEquals("User Not Authenticated", e.getReason());
  }

}
