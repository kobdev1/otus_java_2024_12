package ru.otus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TestPlugin {
    private static final Logger logger = LoggerFactory.getLogger(TestPlugin.class);

    private static final Set<Class> TEST_ANNOTATIONS =
            new HashSet<>(Arrays.asList(Test.class, After.class, Before.class));
    private static int testPass = 0;
    private static int testFailed = 0;

    public static void start(Class testClass) {
        testFailed = 0;
        testPass = 0;
        logger.info("starting tests");
        var methods = filterMethods(testClass.getDeclaredMethods());
        var tests = methods.getOrDefault(Test.class, Collections.emptyList());
        List<Method> beforeMethods = methods.get(Before.class);
        List<Method> afterMethods = methods.get(After.class);
        if (!tests.isEmpty()) {
            for (var test : tests) {
                var instance = createTestInstance(testClass);
                invokeBeforeAndAfter(instance, beforeMethods);
                invoke(instance, test, true);
                invokeBeforeAndAfter(instance, afterMethods);
            }
        }
        logger.info("total tests: {} | tests passed: {} | tests failed: {}", tests.size(), testPass, testFailed);
    }

    private static void invokeBeforeAndAfter(Object instance, List<Method> method) {
        method.forEach(method1 -> invoke(instance, method1, false));
    }

    private static void invoke(Object instance, Method method, Boolean isTest) {
        method.setAccessible(true);
        try {
            method.invoke(instance);
            if (isTest) {
                testPass++;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Exception class: {}", e.getClass().getName());
            logger.error("Exception method: {}", method.getName());
            logger.error("Exception message: {}", e.getMessage());

            if (e.getCause() != null) {
                logger.error("Cause: {}", e.getCause().getClass().getName());
                logger.error("Cause message: {}", e.getCause().getMessage());
            }
            if (isTest) {
                testFailed++;
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
            logger.error("Failed create instance: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static Map<Class, List<Method>> filterMethods(Method[] methodsArr) {
        Map<Class, List<Method>> methods = new HashMap<>();
        for (Method method : methodsArr) {
            for (Annotation annotation : method.getAnnotations()) {
                var inspectedAnnotation = annotation.annotationType();
                if (TEST_ANNOTATIONS.contains(inspectedAnnotation)) {
                    methods.computeIfAbsent(inspectedAnnotation, k -> new ArrayList<>())
                            .add(method);
                }
            }
        }
        return methods;
    }
}
