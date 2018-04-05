package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.application.BlueGarouApplication;
import ca.ulaval.ima.mp.network.WSResponse;

public class AuthFragment extends AbstractFragment {

    public static AuthFragment newInstance()
    {
        AuthFragment authFragment = new AuthFragment();
        authFragment.setLayout(R.layout.fragment_auth);
        return authFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        start();
    }

    private void start() {
        BlueGarouApplication.getInstance().getWebService().GETRequest("urldelapi", onGetObj());
    }

    private WSResponse<JSONObject> onGetObj() {
        return new WSResponse<>(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.d("GET_OBJX", response.toString());
   /*             try {
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
   */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET_OBJX", error.toString());
            }
        });
    }
}
