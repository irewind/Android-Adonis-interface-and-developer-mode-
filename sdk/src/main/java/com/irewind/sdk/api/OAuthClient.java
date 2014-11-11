package com.irewind.sdk.api;

import com.irewind.sdk.Constants;
import com.irewind.sdk.model.AccessToken;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

/**
 * Extends the default Retrofit Http client with a check if the request got denied because the OAuth
 * token expired. In this case
 * To be entirely sure that there will be no StackOverFlowErrors using this implementation
 * Don't let your authentication model use this as a client.
 **/
public class OAuthClient extends OkClient {

    private AccessToken accessToken;
    private OAuthService authService;

    public OAuthClient(OkHttpClient okHttpClient, AccessToken accessToken, OAuthService authService){
        super(okHttpClient);

        this.accessToken = accessToken;
        this.authService = authService;
    }
    public OAuthClient(AccessToken accessToken, OAuthService authService){
        this.accessToken = accessToken;
        this.authService = authService;
    }

    /**
     * Replaces the access token with the new one in the request.
     * @param request Input request to modify
     * @return An other request with the same parameters, but the replaced authorization header
     */
    private Request changeTokenInRequest(Request request){
        List<Header> tempHeaders = request.getHeaders(); // this one is an unmodifiable list.
        List<Header> headers = new ArrayList<Header>();
        headers.addAll(tempHeaders); // this one is modifiable
        Iterator<Header> iter = headers.iterator();
        boolean hadAuthHeader = false;
        // we check if there was an authentication header in the original request
        while(iter.hasNext()){
            Header h = iter.next();
            if (h.getName().equals("Authorization")){
                iter.remove();
                hadAuthHeader = true;
            }
        }
        // if there was an authentication header, replace it with another one containing the new access token.
        if (hadAuthHeader){
            headers.add(new Header("Authorization", "Bearer " + accessToken.getAccessToken()));
        }
        // everything stays the same, except the headers
        return new Request(request.getMethod(), request.getUrl(), headers, request.getBody());
    }

    @Override
    public Response execute(Request request) throws IOException {
        Response response = super.execute(request);
        // 401: Forbidden, 403: Permission denied
        if (response.getStatus() == 401 || response.getStatus() == 403) {
            // the next call should be a synchronous call, otherwise it will immediately continue, and use the old token instead.
            this.accessToken = authService.refreshAccessToken(accessToken.getRefreshToken());
            if (accessToken.getError() == null) {
                // the headers should be modified because the access token changed
                Request newRequest = changeTokenInRequest(request);
                return super.execute(newRequest);
            }
            return response;
        } else {
            return response;
        }
    }
}
