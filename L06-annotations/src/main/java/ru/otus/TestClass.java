package ru.otus;

import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class TestClass {
    @Before
    public void before() {
        System.out.println("Before");
    }

    @Test
    public void test() {
        System.out.println("test 0");
    }

    @Test
    public void test1() {
        System.out.println("test 1");
    }

    @Deprecated
    public void test2() {
        System.out.println("test 2");
    }

    @Test
    public void test3() {
        var ex = 1 / 0; // ArithmeticException
    }

    @Test
    public void test4() {
        throw new UnsupportedOperationException("test 4");
    }

    @Test
    public void test5() {
        System.out.println("test 5");
    }

    @After
    public void after() {
        System.out.println("After");
    }
}
