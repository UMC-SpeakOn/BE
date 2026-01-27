package com.example.speakOn.global.ai.util;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}