package com.mafaa.mobile.support;

public final class TestCredentials {

    private TestCredentials() {
    }

    public static String required(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            value = System.getenv(name);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required test configuration is missing: " + name
                    + ". Use the documented environment variable or Maven property; never commit its value.");
        }
        return value;
    }
}
