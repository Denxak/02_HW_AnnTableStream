package ait.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableInfo {
    public static void runInfo(Object object) {
        Class<?> clazz = object.getClass();
        runInfo(clazz);
    }

    public static void runInfo(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        runInfo(clazz);
    }

    public static void runInfo(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            System.out.println(clazz.getName() + " not a Scheme");
            return;
        }
        Table tableAnn = clazz.getAnnotation(Table.class);
        String tableName = "".equals(tableAnn.name()) ? clazz.getSimpleName().toLowerCase() : tableAnn.name();
        String idField = Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Id is not defined"))
                .getName();
        List<String> uniqueIndexes = Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Index.class) && field.getAnnotation(Index.class).unique())
                .map(Field::getName)
                .collect(Collectors.toList());
        List<String> nonUniqueIndexes = Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Index.class) && !field.getAnnotation(Index.class).unique())
                .map(Field::getName)
                .collect(Collectors.toList());
        printTableInfo(tableName, idField, uniqueIndexes, nonUniqueIndexes);
    }

    private static void printTableInfo(String tableName, String idField, List<String> uniqueIndexes, List<String> nonUniqueIndexes) {
        System.out.println("Table: " + tableName);
        System.out.println("Id: " + idField);
        System.out.println("\tUnique Indexes");
        uniqueIndexes.forEach(System.out::println);
        System.out.println("\tNon Unique Indexes");
        nonUniqueIndexes.forEach(System.out::println);
    }
}
