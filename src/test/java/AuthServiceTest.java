import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: hongseongmin
 * Date: 2014. 2. 5.
 * Time: 오후 10:13
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
		authService = new AuthService();
		mockUserRepository = mock(UserRepository.class);
		authService.setUserRepository(mockUserRepository);
	}

	@Test
	public void canCreate() {
		AuthService authService = new AuthService();
	}

	@Test
	public void givenInvalidId_throwIllegalArgEx() {
		assertIllegalArgEx(null, USER_PASSWORD);
		assertIllegalArgEx("", USER_PASSWORD);
		assertIllegalArgEx(USER_ID, null);
		assertIllegalArgEx(USER_ID, "");
	}

	@Test
	public void whenUserNotFound_throwNonExistingUserEx() {
		assertExceptionThrown(NO_USER_ID, USER_PASSWORD, NonExistingUserException.class);

		for (int i = 1; i <= 100; i++)
			assertExceptionThrown(NO_USER_ID + i, USER_PASSWORD, NonExistingUserException.class);
	}

	@Test
	public void whenUserFoundButWrongPassword_thrownWrongPasswordEx() {
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

	private void assertIllegalArgEx(String id, String userPassword) {
		assertExceptionThrown(id, userPassword, IllegalArgumentException.class);
	}

	private void assertExceptionThrown(String id, String userPassword, Class<? extends Exception> type) {
		Exception throwEx = null;
		try {
			authService.authenticate(id, userPassword);
		} catch (Exception e) {
			throwEx = e;
		}
		assertThat(throwEx, instanceOf(type));
	}

	private class AuthService {

		private void setUserRepository(UserRepository userRepository) {
			this.userRepository = userRepository;
		}

		private UserRepository userRepository;

		public Authentication authenticate(String id, String password) {
			throwExceptionIfIdAndPwIsInvalid(id, password);

			User user = findUserOrThrowNonExistingEx(id);
			throwExceptionIfPasswordIsWrong(password, user);

			return createAuthencation(user);
		}

		private void throwExceptionIfIdAndPwIsInvalid(String id, String password) {
			if (id == null || id.isEmpty())
				throw new IllegalArgumentException();
			if (password == null || password.isEmpty())
				throw new IllegalArgumentException();
		}

		private User findUserOrThrowNonExistingEx(String id) {
			User user = findUserById(id);
			if (user == null)
				throw new NonExistingUserException();
			return user;
		}

		private void throwExceptionIfPasswordIsWrong(String password, User user) {
			if (!user.matchPassword(password))
				throw new WrongPasswordException();
		}

		private Authentication createAuthencation(User user) {
			return new Authentication(user.getId());
		}

		private User findUserById(String id) {
			return userRepository.findById(id);
//			if (id.equals("userId"))
//				return new User(id, "12345");
//			return null;
		}
	}

	private class NonExistingUserException extends RuntimeException {
	}

	private class User {
		private String id;
		private String password;

		public User(String id, String password) {
			this.id = id;
			this.password = password;
		}

		public boolean matchPassword(String password) {
			return this.password == password;
		}

		public String getId() {
			return this.id;
		}
	}

	private class WrongPasswordException extends RuntimeException {
	}

	private interface UserRepository {
		User findById(String id);
	}

	private class Authentication {
		public String id;

		public Authentication(String id) {
			this.id = id;
		}

		private String getId() {
			return id;
		}
	}
}
