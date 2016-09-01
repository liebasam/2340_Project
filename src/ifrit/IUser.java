package ifrit;

import java.util.Date;

public interface IUser {
	String getUsername();
	Date getRegisterDate();
	MemberType getMemberType();
}
