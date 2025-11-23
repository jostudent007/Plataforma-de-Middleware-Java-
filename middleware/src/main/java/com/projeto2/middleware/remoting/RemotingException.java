package com.projeto2.middleware.remoting;

public class RemotingException extends RuntimeException {
    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}