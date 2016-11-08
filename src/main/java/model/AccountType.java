package model;

import java.io.Serializable;

public enum AccountType implements Serializable
{
    User(0),
    Worker(1),
    Manager(2),
    Admin(3);
    
    private final int value;
    AccountType(int value) { this.value = value; }
    
    public boolean isAuthorized(AccountType authLevel) {
        return value >= authLevel.value;
    }
}
