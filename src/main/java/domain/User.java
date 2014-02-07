package domain;

/**
* Created with IntelliJ IDEA.
* User: hongseongmin
* Date: 2014. 2. 7.
* Time: 오후 3:00
* To change this template use File | Settings | File Templates.
*/
public class User {
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
