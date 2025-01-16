package homework;

import java.util.*;

public class CustomerReverseOrder {

    private final ArrayDeque<Customer> customersDeq = new ArrayDeque<>();

    public void add(Customer customer) {
        customersDeq.add(customer);
    }

    public Customer take() {
        return customersDeq.removeLast();
    }
}
