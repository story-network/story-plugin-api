package com.storycraft.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflect {
    public static <T>T getField(Object obj, String name) {
        return getField(obj.getClass(), obj, name);
    }

    public static <T>T getField(Class c, String name) {
        return getField(c, null, name);
    }

    public static <T>T getField(Class c, Object obj, String name) {
        try {
            Field field = c.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println(name + " in " + obj.getClass().getName() + " not found");
        }

        return null;
    }

    public static <T>T invokeMethod(Object obj, String name, Object... params) {
        return invokeMethod(obj.getClass(), obj, name, params);
    }

    public static <T>T invokeMethod(Class c, String name, Object... params) {
        return invokeMethod(c, null, name, params);
    }

    public static <T>T invokeMethod(Class c, Object obj, String name, Object... params) {
        try {
            Class[] classes = new Class[params.length];

            for (int i = 0; i < params.length; i++){
                classes[i] = params.getClass();
            }

            Method method = c.getDeclaredMethod(name, classes);
            method.setAccessible(true);

            return (T) method.invoke(obj, params);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println(name + " in " + obj.getClass().getName() + " not found");
        }

        return null;
    }
}
