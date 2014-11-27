package com.irewind.sdk.api.request;

public class VoteRequest {
    public final static String VOTE_TYPE_LIKE = "LIKE";
    public final static String VOTE_TYPE_DISLIKE = "DISLIKE";

    public String video;
    public String voteType;
}
