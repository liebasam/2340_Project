package ifrit.test;

import ifrit.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class UnitTests {

	IUser user = new User("sam");
	
	@Test
	public void test() {
		assertEquals(user.getUsername(), "sam");
		assertEquals(user.getMemberType(), MemberType.USER);
	}

}
