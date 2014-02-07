package security;

import domain.User;

/**
* Created with IntelliJ IDEA.
* User: hongseongmin
* Date: 2014. 2. 7.
* Time: 오후 3:02
* To change this template use File | Settings | File Templates.
*/
public interface UserRepository {
	User findById(String id);
}
