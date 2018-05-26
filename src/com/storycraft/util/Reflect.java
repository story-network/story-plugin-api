package com.storycraft.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Reflect {

    private static Map<Class, Map<String, Field>> fieldMap;
    private static Map<Class, Map<String, Method>> methodMap;

    static {
        fieldMap = new HashMap<>();
        methodMap = new HashMap<>();
    }

    public static <T>T getField(Object obj, String name) {
        return getField(obj.getClass(), obj, name);
    }

    public static <T>T getField(Class<?> c, String name) {
        return getField(c, null, name);
    }

    public static <T>T getField(Class<?> c, Object obj, String name) {
        try {
            Field field;
            if (fieldMap.containsKey(c) && fieldMap.get(c).containsKey(name)) {
                field = fieldMap.get(c).get(name);
            }
            else {
                field = getDeclaredField(c, name);

                if (!fieldMap.containsKey(c))
                    fieldMap.put(c, new HashMap<>());

                fieldMap.get(c).put(name, field);
            }

            return (T) field.get(obj);
        } catch (NullPointerException | IllegalAccessException e) {
            System.out.println("Error to get " + name + " : " + e.getMessage());
        }

        return null;
    }

    public static void setField(Class<?> c, Object obj, String name, Object value) {
        try {
            Field field = getDeclaredField(c, name);

            field.set(obj, value);
        } catch (NullPointerException | IllegalAccessException e) {
            System.out.println("Error to set field " + name + " : " + e.getMessage());
        }
    }

    public static void setField(Object obj, String name, Object value) {
        setField(obj.getClass(), obj, name, value);
    }

    public static void setField(Class<?> c, String name, Object value) {
        setField(c, null, name, value);
    }

    private static Field getDeclaredField(Class<?> c, String name) {
        try {
            Field field = c.getDeclaredField(name);
            field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            System.out.println(name + " field in " + c.getName() + " not found");
        }

        return null;
    }

    public static <T>T invokeMethod(Object obj, String name, Object... params) {
        return invokeMethod(obj.getClass(), obj, name, params);
    }

    public static <T>T invokeMethod(Class<?> c, String name, Object... params) {
        return invokeMethod(c, null, name, params);
    }

    public static <T>T invokeMethod(Class<?> c, Object obj, String name, Object... params) {
        try {
            Class<?>[] classes = new Class<?>[params.length];

            for (int i = 0; i < params.length; i++){
                classes[i] = params[i].getClass();
            }

            Method method;
            if (methodMap.containsKey(c) && methodMap.get(c).containsKey(name)) {
                method = methodMap.get(c).get(name);
            }
            else {
                method = getDeclaredMethod(c, name, classes);

                if (!methodMap.containsKey(c))
                    methodMap.put(c, new HashMap<>());

                methodMap.get(c).put(name, method);
            }

            return (T) method.invoke(obj, params);

        } catch (NullPointerException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Error to invoke " + name + " : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static Method getDeclaredMethod(Class<?> c, String name, Class<?>... classes) {
        try {
            Method method = c.getDeclaredMethod(name, classes);
            method.setAccessible(true);

            return method;
        } catch (NoSuchMethodException e) {
            System.out.println(name + " method in " + c.getName() + " not found");
        }

        return null;
    }
}
