package com.example.snippets.backgroundwork;

/**
 * Placeholder exception used by wake lock docs.
 *
 * Existing wake lock code snippets inclde a method that throws "MyException", I need to define
 * it for the code snippets to use.
 */
public class MyException extends RuntimeException {
    public MyException(String message) {
        super(message);
    }
}
