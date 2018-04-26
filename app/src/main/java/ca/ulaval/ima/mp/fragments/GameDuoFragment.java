package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    private List<Player> mTargets;
    private ListView mListView;

    public static GameDuoFragment newInstance(CHOICE_MODE choice, List<Player> players)
    {
        GameDuoFragment fragment = new GameDuoFragment();
        fragment.mChoice = choice;
        fragment.mPlayers = players;
        fragment.setLayout(R.layout.fragment_game_pickname);
        return fragment;
    }

    public void setTargets(List<Player> targets)
    {
        this.mTargets = targets;
    }

    public List<Player> getTargets()
    {
        return mTargets;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = mView.findViewById(R.id.list_names);
        if (mChoice != CHOICE_MODE.DEBATE) {
            DuoNamesListAdapter dla = new DuoNamesListAdapter(mContext, mPlayers);
            mListView.setAdapter(dla);
            Utils.justifyListViewHeightBasedOnChildren(mListView);
        }
        ImageView icone = mView.findViewById(R.id.icn);
        TextView text = mView.findViewById(R.id.text);
        AppCompatButton btn = mView.findViewById(R.id.btn_next);
        if (mChoice != CHOICE_MODE.DEBATE && ((GameActivity) mContext).getGame().getVotes().size() != mPlayers.size())
            btn.setVisibility(View.GONE);
        switch (mChoice) {
            case ROLES:
                Picasso.get().load(R.drawable.girl).into(icone);
                btn.setText("Commencer");
                text.setText("Attribution des rôles.\nQui êtes vous ?");
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ((GameActivity)mContext).startRevealStep(mPlayers.get(i));
                        ((GameActivity)mContext).playerVote(mPlayers.get(i).getId(), mPlayers.get(i).getId());
                    }
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GameActivity) mContext).prepareStep(1);
                    }
                });
                break ;
            case VOTES:
                Picasso.get().load(R.drawable.villager).into(icone);
                btn.setText("Continuer");
                text.setText("C'est le moment des votes.");
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ((GameActivity)mContext).startTargetStep(mPlayers.get(i), mTargets, TargetFragment.TARGET_MODE.VOTE);
                    }
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GameActivity)mContext).prepareStep(5);
                    }
                });
                break ;
            case WOLVES:
                Picasso.get().load(R.drawable.wolf).into(icone);
                btn.setText("Continuer");
                text.setText("C'est la nuit. Loups-Garou, \naction !");
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ((GameActivity)mContext).startTargetStep(mPlayers.get(i), mTargets, TargetFragment.TARGET_MODE.WOLF);
                    }
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GameActivity) mContext).prepareStep(6);
                    }
                });
                break ;
            case DEBATE:
                Picasso.get().load(R.drawable.villager).into(icone);
                btn.setText("Continuer");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GameActivity)mContext).prepareStep(2);
                    }
                });
                text.setText("Débat...");
                mListView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBluetoothResponse() {
        AppCompatButton btn = mView.findViewById(R.id.btn_next);

        if (mChoice == CHOICE_MODE.DEBATE ||
                ((GameActivity) mContext).getGame().getVotes().size() == mPlayers.size())
            btn.setVisibility(View.VISIBLE);
        if (mChoice != CHOICE_MODE.DEBATE) {
            DuoNamesListAdapter dla = new DuoNamesListAdapter(mContext, mPlayers);
            mListView.setAdapter(dla);
            Utils.justifyListViewHeightBasedOnChildren(mListView);
        }
    }
}
