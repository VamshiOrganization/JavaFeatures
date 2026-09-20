package com.vmc.java8;

import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LazyLoading {
    public static void main(String[] args) {
        List<User> users = Arrays.asList(new User(28, "sreeja"),
                new User(33, "Vamshi"),
                new User(30, "Ajay"));
        List<User> processedUsers = users.stream().filter(u -> {
            System.out.println("1. Entering Filter: " + u.getName());
            return u.getAge() > 18;
        }).map(u -> {
            System.out.println("2. Passed Filter: " + u.getName());
            // Suspected slow operation (e.g., fetching profile from cache/DB)
            return u;
        }).peek(u -> System.out.println("3. Mapping Complete: " + u.getName()))
                .limit(2).collect(Collectors.toList());
        System.out.println("===================");

        int[] arr = {1, 2, 3, 4, 5};
        IntStream graterThan3Stream = Arrays.stream(arr).filter(value -> {
            System.out.println("value:" + value + " below than 3");
            return value > 3;
        });
        System.out.println("IntStream created." + graterThan3Stream);
        int gratherThan3 = graterThan3Stream.findFirst().getAsInt();
        System.out.println(gratherThan3);
        System.out.println("===================");

        Stream<String> names = Stream.of("Ali", "Bob", "Charlie").filter(s -> {
            System.out.println("Filtering: " + s);
            return s.length() > 3;
        });
        System.out.println("Stream created.");
        names.findFirst();
        System.out.println("===================");

        Stream<Integer> iterateStream = Stream.iterate(5, i -> i + 10)
                .peek(u -> System.out.println(" Mapping Complete: " + u)).limit(10);
        Set<Integer> set = iterateStream.collect(Collectors.toSet());
        System.out.println(set);
    }
}
