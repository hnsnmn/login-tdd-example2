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
 * Date: 2014. 2. 3.
 * Time: 오전 10:40
 * To change this template use File | Settings | File Templates.
 */
public class AuthServiceTest {

	public static final String USER_PASSWORD = "userPassword";
	public static final String NO_USER_ID = "noUserId";
	public static final String USER_ID = "userId";
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
		AuthService authService = new AuthService();
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
		for (int i=1; i <= 100; i++)
			assertExceptionThrown(NO_USER_ID + i, USER_PASSWORD, NonExistingUserException.class);
	}

	@Test
	public void whenUserFoundButWrongPw_throwWrongPasswordEx() {
		// BDD-style tests(given, when, then)
		givenUserExists(USER_ID, USER_PASSWORD);
		assertExceptionThrown(USER_ID, WRONG_PASSWORD, WrongPasswordException.class);
		vierifyUserFound(USER_ID);
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

	private void vierifyUserFound(String userId) {
		verify(mockUserRepository).findById(userId);
	}

	private void assertExceptionThrown(String id, String userPassword, Class<? extends Exception> type) {
		Exception thrownEx = null;
		try {
			authService.authenticate(id, userPassword);
		} catch (Exception e) {
			thrownEx = e;
		}
		assertThat(thrownEx, instanceOf(type));
	}


	private void assertIllegalArgExThrown(String id, String userPassword) {
		assertExceptionThrown(id, userPassword, IllegalArgumentException.class);
	}

	public class Authentication {
		private String id;

		public Authentication(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}

	public class AuthService {
		private UserRepository userRepository;


		public void setUserRepository(UserRepository userRepository) {
			this.userRepository = userRepository;
		}


		public Authentication authenticate(String id, String password ) {
			assertIdAndPw(id, password);

			User user = findUserOrThrowEx(id);
			throwExceptionIfPasswordIsWrong(password, user);
			return createAuthentication(user);
		}

		private void assertIdAndPw(String id, String password) {
			if (id == null || id.isEmpty())
				throw new IllegalArgumentException();
			if (password == null || password.isEmpty())
				throw new IllegalArgumentException();
		}

		private User findUserOrThrowEx(String id) {
			User user = findUserById(id);
			if (user == null)
				throw new NonExistingUserException();
			return user;
		}

		private void throwExceptionIfPasswordIsWrong(String password, User user) {
			if (!user.matchPassword(password))
				throw new WrongPasswordException();
		}

		private Authentication createAuthentication(User user) {
			return new Authentication(user.getId());
		}

		private User findUserById(String id) {
			return userRepository.findById(id);
		}
	}

	public class NonExistingUserException extends RuntimeException {
	}


	public class User {
		private final String id;
		private final String password;

		public User(String id, String password) {
			this.id = id;
			this.password = password;
		}

		public String getId() {
			return id;
		}

		public boolean matchPassword(String password) {
			return this.password == password;
		}
	}

	public interface UserRepository {
		User findById(String id);
	}

	public class WrongPasswordException extends RuntimeException {
	}
}
