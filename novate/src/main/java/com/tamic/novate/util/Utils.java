package com.tamic.novate.util;

/**
 * copy  Copyright (C) 2007 The Guava Authors by from retrofit2
 * @author Tamic(https://github.com/NeglectedByBoss)
 */
public class Utils {
    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
