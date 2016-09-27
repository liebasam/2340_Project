package model;

/**
 * Enum for Authorization level for users
 * Created by Stanley on 9/27/2016.
 */
public enum AuthLevel {
    ADMIN("ADMIN"),
    WORKER("WORKER"),
    NORMAL_USER("NORMAL_USER");

    private final String name;

    /**
     *
     * The constructor for AuthLevel
     * @param name name of the type of user auth
     */
    AuthLevel(String name) {
        this.name = name;
    }

    /**
     * Getter for name
     * @return name of the auth type
     */
    public String getName() { return name; }

    @Override
    public String toString() { return name; }

}
