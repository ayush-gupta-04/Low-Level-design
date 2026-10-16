package CarRentalSystem.services;

import CarRentalSystem.entities.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomerService {

    private final Map<String, Customer> customersById =  new HashMap<>();

    public void addCustomer(Customer customer) {
        customersById.putIfAbsent(customer.getId(), customer);
    }

    public Optional<Customer> getCustomer(String customerId) {
        return Optional.ofNullable(customersById.get(customerId));
    }

    public void removeCustomer(String customerId) {
        if(!customersById.containsKey(customerId)) return;
        customersById.remove(customerId);
    }
}
