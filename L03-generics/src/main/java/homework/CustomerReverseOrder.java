package homework;

import java.util.*;

public class CustomerReverseOrder {

    private final Deque<Customer> customersDeq = new ArrayDeque<>();

    public void add(Customer customer) {
        customersDeq.add(customer);
    }

    public Customer take() {
        return customersDeq.removeLast();
    }
}
