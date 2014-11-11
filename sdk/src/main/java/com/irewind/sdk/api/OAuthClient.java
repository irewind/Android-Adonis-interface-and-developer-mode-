package com.irewind.sdk.api;

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
     * Replaces the header value with the new one in the request.
     * @param request Input request to modify
     * @param headerName header name to modify
     * @param headerValue header value to modify
     * @return An other request with the same parameters, but the replaced header
     */
    private Request replaceHeaderInRequest(Request request, String headerName, String headerValue){
        List<Header> tempHeaders = request.getHeaders(); // this one is an unmodifiable list.
        List<Header> headers = new ArrayList<Header>();
        headers.addAll(tempHeaders); // this one is modifiable
        Iterator<Header> iter = headers.iterator();
        boolean hadHeader = false;
        // we check if there was a header in the original request
        while(iter.hasNext()){
            Header h = iter.next();
            if (h.getName().equals(headerName)){
                iter.remove();
                hadHeader = true;
            }
        }
        // if there was a header, replace it with another one containing the new value.
        if (hadHeader){
            headers.add(new Header(headerName, headerValue));
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
                Request newRequest = replaceHeaderInRequest(request, "Authorization", "Bearer " + accessToken.getCurrentToken());
                return super.execute(newRequest);
            }
            return response;
        } else {
            return response;
        }
    }
}
