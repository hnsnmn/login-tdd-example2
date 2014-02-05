package tdd.security;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: hongseongmin
 * Date: 2014. 2. 5.
 * Time: 오전 10:57
 * To change this template use File | Settings | File Templates.
 */
public class AuthServiceTest {

	public static final String USER_PASSWORD = "userPassword";
	public static final String USER_ID = "userId";
	public static final String NO_USER_ID = "noUserId";
	public static final String WRONG_PASSWORD = "wrongPassword";
	private AuthService authService;
	private UserRepository mockUserRepository;

	@Before
	public void setUp() throws Exception {
		mockUserRepository = mock(UserRepository.class);
		authService = new AuthService();
		authService.setUserRepository(mockUserRepository);
	}


	@Test
	public void canCreate() {
	}

	@Test
	public void givenInvalidId_throwIllegalArgEx() {
		assertIllegalArgExThrown(null, USER_PASSWORD);
		assertIllegalArgExThrown("", USER_PASSWORD);
		assertIllegalArgExThrown(USER_ID, null);
		assertIllegalArgExThrown(USER_ID, "");
	}

	@Test
	public void whenUserNotFound_throwNonExistingUserEx() {
		assertExceptionThrown(NO_USER_ID, USER_PASSWORD, NonExistingUserException.class);
		// 회기테스트
		for (int i = 1; i <= 100; i++)
			assertExceptionThrown(NO_USER_ID + i, USER_PASSWORD, NonExistingUserException.class);
	}

	@Test
	public void whenUserFoundButWrongPw_throwWrongPasswordEx() {
		givenUserExists(USER_ID, USER_PASSWORD);
		assertExceptionThrown(USER_ID, WRONG_PASSWORD, WrongPasswordException.class);
		verifyUserFound(USER_ID);
	}

	@Test
	public void whenUserFoundAndRightPw_returnAuth() {
		givenUserExists(USER_ID, USER_PASSWORD);
		Authentication auth = authService.authenticate(USER_ID, USER_PASSWORD);
		assertThat(auth.getId(), equalTo(USER_ID));
	}

	private void givenUserExists(String id, String password) {
		when(mockUserRepository.findById(id)).thenReturn(new User(id, password));
	}

	private void verifyUserFound(String id) {
		verify(mockUserRepository).findById(id);
	}

	private void assertIllegalArgExThrown(String id, String password) {
		assertExceptionThrown(id, password, IllegalArgumentException.class);
	}

	private void assertExceptionThrown(String id, String password, Class<? extends Exception> type) {
		Exception throwEx = null;
		try {
			authService.authenticate(id, password);
		} catch (Exception e) {
			throwEx = e;
		}
		assertThat(throwEx, instanceOf(type));
	}

	private class AuthService {
		private UserRepository userRepository;

		private void setUserRepository(UserRepository userRepository) {
			this.userRepository = userRepository;
		}

		public Authentication authenticate(String id, String password) {
			assertIdAndPassword(id, password);

			User user = findUserOrThrowEx(id);
			throwExceptionIfPasswordIsWrong(password, user);
			return createAuthentication(user);
		}

		private Authentication createAuthentication(User user) {
			return new Authentication(user.getId());
		}

		private void throwExceptionIfPasswordIsWrong(String password, User user) {
			if (!user.matchPassword(password))
				throw new WrongPasswordException();
		}

		private User findUserOrThrowEx(String id) {
			User user = findUserById(id);
			if (user == null)
				throw new NonExistingUserException();
			return user;
		}

		private void assertIdAndPassword(String id, String password) {
			if (id == null || id.isEmpty()) throw new IllegalArgumentException();
			if (password == null || password.isEmpty()) throw new IllegalArgumentException();
		}


		private User findUserById(String id) {
			return userRepository.findById(id);
		}

	}

	private class User {

		private String id;
		private String password;
		public User(String id, String password) {
			this.id = id;
			this.password = password;
		}

		private String getId() {
			return id;
		}

		public boolean matchPassword(String password) {
			return this.password.equals(password);
		}

	}

	private class Authentication {

		private String id;

		public Authentication(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}

	}

	private interface UserRepository {
		User findById(String id);

	}

	private class WrongPasswordException extends RuntimeException {

	}

	private class NonExistingUserException extends RuntimeException {
	}
}



