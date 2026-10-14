package CarRentalSystem.entities;

public class Customer {
    private final String id;
    private final String name;
    private final String licenseNo;
 
    public Customer(String id, String name, String licenseNo) {
        this.id = id;
        this.name = name;
        this.licenseNo = licenseNo;
    }
 
    public String getId() { return id; }
    public String getName() { return name; }
}
