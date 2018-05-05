package ca.ulaval.ima.mp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.application.BlueGarouApplication;
import ca.ulaval.ima.mp.network.WSResponse;

public class StatsFragment extends AbstractFragment {
    private View AuthLayout;
    private View NoAuthLayout;

    public static StatsFragment newInstance() {
        StatsFragment statsFragment = new StatsFragment();
        statsFragment.setLayout(R.layout.fragment_stats);
        return statsFragment;
    }

    private void init()
    {
        AuthLayout.setVisibility(BlueGarouApplication.getInstance().getAuth() ? View.VISIBLE : View.GONE);
        NoAuthLayout.setVisibility(BlueGarouApplication.getInstance().getAuth() ? View.GONE : View.VISIBLE);
        SharedPreferences sharedPreferences = BlueGarouApplication.getInstance().getSharedPreferences("BLUEGAROU", Context.MODE_PRIVATE);
        BlueGarouApplication.getInstance().getWebService().GETRequestWithAuth("stats", onStats(), sharedPreferences.getString("access_token", ""));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AuthLayout = mView.findViewById(R.id.auth_layout);
        NoAuthLayout = mView.findViewById(R.id.no_auth_layout);
        init();
    }

    private void updateStats(int nbWins, int nbVWins, int nbWWins)
    {
        TextView games = AuthLayout.findViewById(R.id.games);
        TextView villagers = AuthLayout.findViewById(R.id.villagers);
        TextView wwolfs = AuthLayout.findViewById(R.id.wwolfs);
        games.setText(games.getText().toString().replace("--", String.valueOf(nbWins)));
        villagers.setText(villagers.getText().toString().replace("--", String.valueOf(nbVWins)));
        wwolfs.setText(wwolfs.getText().toString().replace("--", String.valueOf(nbWWins)));
    }

    private WSResponse<JSONObject> onStats() {
        return new WSResponse<>(new Response.Listener<JSONObject>() {
            int vWins = 0;
            int wWins = 0;
            @Override
            public void onResponse(final JSONObject response) {
                Log.d("GET_OBJX", response.toString());
                try {
                    JSONArray stats = response.getJSONArray("stats");
                    for (int i = 0; i < stats.length(); i++)
                    {
                        String winning = ((JSONObject)stats.get(i)).getString("winningside");
                        if (winning.equals("VILLAGERS"))
                            vWins++;
                        else
                            wWins++;
                    }
                    updateStats(stats.length(), vWins, wWins);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET_OBJX", error.toString());
            }
        });
    }
}
