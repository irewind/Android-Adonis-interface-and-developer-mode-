package com.irewind.sdk.model;

/**
 * <p>
 * Identifies the state of a Session.
 * </p>
 * <p>
 * Session objects implement a state machine that controls their lifecycle. This
 * enum represents the states of the state machine.
 * </p>
 */
public enum SessionState {
    /**
     * Indicates that the Session has not yet been opened and has no cached
     * token. Opening a Session in this state will involve user interaction.
     */
    CREATED(Category.CREATED_CATEGORY),

    /**
     * <p>
     * Indicates that the Session has not yet been opened and has a cached
     * token. Opening a Session in this state will not involve user interaction.
     * </p>
     * <p>
     * If you are using Session from an Android Service, you must provide a
     * TokenCachingStrategy implementation that contains a valid token to the Session
     * constructor. The resulting Session will be created in this state, and you
     * can then safely call open, passing null for the Activity.
     * </p>
     */
    CREATED_TOKEN_LOADED(Category.CREATED_CATEGORY),

    /**
     * Indicates that the Session is in the process of opening.
     */
    OPENING(Category.CREATED_CATEGORY),

    /**
     * Indicates that the Session is opened.
     */
    OPENED(Category.OPENED_CATEGORY),

    /**
     * <p>
     * Indicates that the Session is opened and that the token has changed.
     * </p>
     * <p>
     * Every time the token is updated, {@link Session.StatusCallback
     * StatusCallback} is called with this value.
     * </p>
     */
    OPENED_TOKEN_UPDATED(Category.OPENED_CATEGORY),

    /**
     * Indicates that the Session is closed, and that it was not closed
     * normally. Typically this means that the open call failed, and the
     * Exception parameter to {@link Session.StatusCallback StatusCallback} will
     * be non-null.
     */
    CLOSED_LOGIN_FAILED(Category.CLOSED_CATEGORY),

    /**
     * Indicates that the Session was closed normally.
     */
    CLOSED(Category.CLOSED_CATEGORY);

    private final Category category;

    SessionState(Category category) {
        this.category = category;
    }

    /**
     * Returns a boolean indicating whether the state represents a successfully
     * opened state.
     *
     * @return a boolean indicating whether the state represents a successfully
     *         opened state.
     */
    public boolean isOpened() {
        return this.category == Category.OPENED_CATEGORY;
    }

    /**
     * Returns a boolean indicating whether the state represents a closed
     * Session that can no longer be used.
     *
     * @return a boolean indicating whether the state represents a closed
     * Session that can no longer be used.
     */
    public boolean isClosed() {
        return this.category == Category.CLOSED_CATEGORY;
    }

    private enum Category {
        CREATED_CATEGORY, OPENED_CATEGORY, CLOSED_CATEGORY
    }
}
