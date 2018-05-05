package ca.ulaval.ima.mp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.application.BlueGarouApplication;
import ca.ulaval.ima.mp.game.Game;
import ca.ulaval.ima.mp.game.Player;
import ca.ulaval.ima.mp.game.roles.Role;
import ca.ulaval.ima.mp.network.WSResponse;

public class WinFragment extends AbstractFragment {

    private Role.Side mWinner;

    public static WinFragment newInstance(Role.Side winner) {
        WinFragment fragment = new WinFragment();
        fragment.mWinner = winner;
        fragment.setLayout(R.layout.fragment_game_win);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imgRole = mView.findViewById(R.id.img_role);
        TextView textYouAre = mView.findViewById(R.id.txt_you_are);
        switch (mWinner) {
            case WEREWOLF:
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wolf));
                textYouAre.setText(textYouAre.getText().toString().replace("...", getString(R.string.the_werewolves)));
                break;
            default:
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.villager));
                textYouAre.setText(textYouAre.getText().toString().replace("...", getString(R.string.the_villagers)));
                break ;
        }
        sendStats();
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)mContext).onBackPressed();
            }
        });
    }

    private void sendStats()
    {
        Game game = ((GameActivity)mContext).getGame();
        JSONArray villagers = new JSONArray();
        JSONArray werewolves = new JSONArray();
        for (Player p : game.getPlayers())
        {
            if (p.getRole().getSide() == Role.Side.VILLAGER)
                villagers.put(p.getName());
            else if (p.getRole().getSide() == Role.Side.WEREWOLF)
                werewolves.put(p.getName());
        }
        JSONObject params = new JSONObject();
        try {
            params.put("winningside", mWinner == Role.Side.VILLAGER ? "VILLAGERS" : "WEREWOLVES");
            params.put("villagers", villagers);
            params.put("werewolves", werewolves);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = BlueGarouApplication.getInstance().getSharedPreferences("BLUEGAROU", Context.MODE_PRIVATE);
        BlueGarouApplication.getInstance().getWebService().POSTRequestWithAuth("stats", onStats(), params, sharedPreferences.getString("access_token", ""));
    }

    private WSResponse<JSONObject> onStats() {
        return new WSResponse<>(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.d("GET_OBJX", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET_OBJX", error.toString());
            }
        });
    }
}
