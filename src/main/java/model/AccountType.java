package model;

import java.io.Serializable;

/**
 * Represents a user authentication level
 */
public enum AccountType implements Serializable
{
    User(0),
    Worker(1),
    Manager(2),
    Admin(3);
    
    private final int value;
    AccountType(int value) { this.value = value; }
    
    /**
     * Returns whether this account type has sufficient permissions
     * @param authLevel minimum authorization level required
     * @return whether this account type is at or above authLevel
     */
    public boolean isAuthorized(AccountType authLevel) {
        return value >= authLevel.value;
    }
}
