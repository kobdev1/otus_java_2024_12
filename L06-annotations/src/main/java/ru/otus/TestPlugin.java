package ru.otus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TestPlugin {
    private static final HashSet<Class> testAnnotations =
            new HashSet<>(Arrays.asList(Test.class, After.class, Before.class));

    public static void start(Class testClass) {
        var methods = filterMethods(testClass.getDeclaredMethods());
        checkTestAnnotations(methods);
        var tests = methods.get(Test.class);
        List<Method> beforeMethods = methods.get(Before.class);
        List<Method> afterMethods = methods.get(After.class);
        for (var test : tests) {
            var instance = createTestInstance(testClass);
            invokeBeforeAndAfter(instance, beforeMethods);
            invoke(instance, test);
            invokeBeforeAndAfter(instance, afterMethods);
        }
    }

    private static void checkTestAnnotations(HashMap<Class, List<Method>> methods) {
        if (methods.isEmpty()) {
            throw new RuntimeException("No any test annotations found");
        }
        if (methods.getOrDefault(Before.class, null) == null) {
            throw new RuntimeException("No before annotations found");
        }
        if (methods.getOrDefault(After.class, null) == null) {
            throw new RuntimeException("No after annotations found");
        }
        if (methods.getOrDefault(Test.class, null) == null) {
            throw new RuntimeException("No test annotations found");
        }
    }

    private static void invokeBeforeAndAfter(Object instance, List<Method> method) {
        method.forEach(method1 -> invoke(instance, method1));
    }

    private static void invoke(Object instance, Method method) {
        method.setAccessible(true);
        try {
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Exception class: " + e.getClass().getName());
            System.err.println("Exception method: " + method.getName());
            System.err.println("Exception message: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getClass().getName());
                System.err.println("Cause message: " + e.getCause().getMessage());
            }
        }
    }

    private static Object createTestInstance(Class testClass) {
        try {
            return testClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
            System.err.println(Arrays.stream(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    private static HashMap<Class, List<Method>> filterMethods(Method[] methodsArr) {
        HashMap<Class, List<Method>> methods = new HashMap<>();
        for (Method method : methodsArr) {
            for (Annotation annotation : method.getAnnotations()) {
                var inspectedAnnotation = annotation.annotationType();
                if (testAnnotations.contains(inspectedAnnotation)) {
                    methods.computeIfAbsent(inspectedAnnotation, k -> new ArrayList<>())
                            .add(method);
                }
            }
        }
        return methods;
    }
}
