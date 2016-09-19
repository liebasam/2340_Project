package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Lucien on 9/16/2016.
 */
public class IUserTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = AssertionError.class)
    public void testTest() throws Exception {
        assert(1 == 1);
        assert(1 == 2);
    }

}