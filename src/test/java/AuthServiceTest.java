import domain.User;
import exception.NonExistingUserException;
import exception.WrongPasswordException;
import org.junit.Before;
import org.junit.Test;
import security.AuthService;
import security.Authentication;
import security.UserRepository;

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

}
