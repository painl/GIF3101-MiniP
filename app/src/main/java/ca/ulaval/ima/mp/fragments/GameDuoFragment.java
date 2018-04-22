package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.adapters.DuoNamesListAdapter;
import ca.ulaval.ima.mp.game.Player;
import ca.ulaval.ima.mp.utils.Utils;

public class GameDuoFragment extends AbstractFragment {

    public enum CHOICE_MODE {
        ROLES,
        VOTES,
        WOLVES,
        DEBATE
    }

    private CHOICE_MODE mChoice;
    private List<Player> mPlayers;

    public static GameDuoFragment newInstance(CHOICE_MODE choice, List<Player> players)
    {
        GameDuoFragment fragment = new GameDuoFragment();
        fragment.mChoice = choice;
        fragment.mPlayers = players;
        fragment.setLayout(R.layout.fragment_game_pickname);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = mView.findViewById(R.id.list_names);
        ArrayList<String> names = new ArrayList<>();
        for (Player p : mPlayers)
            names.add(p.getName());
        DuoNamesListAdapter dla = new DuoNamesListAdapter(mContext, names, ((GameActivity)mContext).mSeenState.validates);
        listView.setAdapter(dla);
        Utils.justifyListViewHeightBasedOnChildren(listView);
        ImageView icone = mView.findViewById(R.id.icn);
        TextView text = mView.findViewById(R.id.text);
        AppCompatButton btn = mView.findViewById(R.id.btn_next);
        if (((GameActivity)mContext).mSeenState.getRemaining() != 0 && mChoice != CHOICE_MODE.DEBATE)
            btn.setVisibility(View.GONE);
        switch (mChoice) {
            case ROLES:
                Picasso.get().load(R.drawable.girl).into(icone);
                btn.setText("Commencer");
                text.setText("Attribution des rôles.\nQui êtes vous ?");
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ((GameActivity)mContext).startRevealStep(mPlayers.get(i));
                        ((GameActivity)mContext).mSeenState.validates.set(i, mPlayers.get(i));
                    }
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("tag", "gogogo");
                        ((GameActivity)mContext).getGame().play();
                    }
                });
                break ;
            case VOTES:
                Picasso.get().load(R.drawable.villager).into(icone);
                btn.setText("Continuer");
                text.setText("C'est le moment des votes.");
                break ;
            case WOLVES:
                Picasso.get().load(R.drawable.wolf).into(icone);
                btn.setText("Continuer");
                text.setText("Loups-Garou, \nAction !");
                break ;
            case DEBATE:
                Picasso.get().load(R.drawable.villager).into(icone);
                btn.setText("Continuer");
                text.setText("Débat...");
                listView.setVisibility(View.GONE);
        }
    }
}
