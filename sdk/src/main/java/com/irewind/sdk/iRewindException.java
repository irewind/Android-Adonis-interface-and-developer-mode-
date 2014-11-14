package com.irewind.sdk;

/**
 * Represents an error condition specific to the iRewindException SDK for Android.
 */
public class iRewindException extends RuntimeException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a new iRewindException.
     */
    public iRewindException() {
        super();
    }

    /**
     * Constructs a new iRewindException.
     *
     * @param message
     *            the detail message of this exception
     */
    public iRewindException(String message) {
        super(message);
    }

    /**
     * Constructs a new iRewindException.
     *
     * @param message
     *            the detail message of this exception
     * @param throwable
     *            the cause of this exception
     */
    public iRewindException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new iRewindException.
     *
     * @param throwable
     *            the cause of this exception
     */
    public iRewindException(Throwable throwable) {
        super(throwable);
    }
}
