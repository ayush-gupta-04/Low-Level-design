package PaymentSystem.entities;

public final class User {
    private final String userId;
    private final String name;
    private final String contactDetails;

    public User(String userId, String name, String contactDetails) {
        this.userId = userId;
        this.name = name;
        this.contactDetails = contactDetails;
    }

    public String getUserId() {return userId;}
    public String getName() {return name;}
    public String getContactDetails() {return contactDetails;}
}