package ifrit;

import java.util.Date;

public class User implements IUser {
	private final String username;
	private final Date registerDate;
	
	public User(String username) {
		this.username = username;
		this.registerDate = new Date();
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public Date getRegisterDate() {
		return registerDate;
	}
	
	@Override
	public MemberType getMemberType() {
		return MemberType.USER;
	}
}
