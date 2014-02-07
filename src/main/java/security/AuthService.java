package security;

import domain.User;
import exception.NonExistingUserException;
import exception.WrongPasswordException;

/**
* Created with IntelliJ IDEA.
* User: hongseongmin
* Date: 2014. 2. 7.
* Time: 오후 3:03
* To change this template use File | Settings | File Templates.
*/
public class AuthService {

	public void setUserRepository(UserRepository userRepository) {
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
