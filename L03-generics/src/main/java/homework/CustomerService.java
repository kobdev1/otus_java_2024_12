package homework;

import java.util.*;

public class CustomerService {

    private final NavigableMap<Customer, String> customers = new TreeMap<>();

    public Map.Entry<Customer, String> getSmallest() {
        return copy(customers.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> next = customers.higherEntry(customer);
        if (next != null) {
            return copy(next);
        } else {
            return null;
        }
    }

    public void add(Customer customer, String data) {
        customers.put(customer, data);
    }

    private Map.Entry<Customer, String> copy(Map.Entry<Customer, String> entry) {
        return Map.entry(new Customer(entry.getKey()), entry.getValue());
    }
}
