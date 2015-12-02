package edu.northwestern.cbits.xsi.google;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import java.util.ArrayList;
import java.util.List;

public class GoogleApi {

    private static final String TAG = GoogleApi.class.getName();

    private static GoogleApiClient _client;

    /**
     * Builds a Google API client that utilizes GoogleSignInOptions.
     * @param activity The fragment activity associated with the connection.
     * @param signInScopes A list of Scopes to be associated with the client.
     * @param apis A list of APIs to be utilized by the client.
     * @return a client built using the provided scopes and apis.
     */
    public GoogleApiClient buildClient(final FragmentActivity activity,
                                       final List<String> signInScopes,
                                       final List<Api> apis) {
        if(signInScopes == null || signInScopes.size() < 1) {
            throw new IllegalArgumentException("Must include at least one sign in scope.");
        }

        final List<Scope> scopes = new ArrayList<>();
        for(String scope : signInScopes) {
            scopes.add(new Scope(scope));
        }

        final GoogleSignInOptions.Builder signInBuilder =
            new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN);

        if(signInScopes.size() == 1) {
            signInBuilder.requestScopes(scopes.get(0));
        } else {
            signInBuilder.requestScopes(
                    scopes.get(0), scopes.subList(1, scopes.size())
                            .toArray(new Scope[scopes.size() - 1]));
        }
        final GoogleSignInOptions gso = signInBuilder.build();

        final GoogleApiClient.Builder clientBuilder =
            new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        _client = null;
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso);

        for(Api api : apis) {
            clientBuilder.addApi(api);
        }

        clientBuilder.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.d(TAG, "Google API client connected.");
            }
            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API client suspended.");
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.d(TAG, "Google API client connection failed: " + connectionResult.getErrorMessage());
            }
        });

        _client = clientBuilder.build();
        return _client;
    }

    public GoogleApiClient getClient() {
        return _client;
    }
}
