package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.adapters.TargetsListAdapter;
import ca.ulaval.ima.mp.game.Player;
import ca.ulaval.ima.mp.utils.Utils;

public class TargetFragment extends AbstractFragment {

    private List<Player> mTargets;
    private Player targeted;
    private String mName;
    private int mId;
    private TARGET_MODE mMode;

    public static TargetFragment newInstance(Player player, List<Player> targets, TARGET_MODE mode) {
        TargetFragment fragment = new TargetFragment();
        fragment.mTargets = targets;
        fragment.mName = player.getName();
        fragment.mId = player.getId();
        fragment.mMode = mode;
        fragment.setLayout(R.layout.fragment_game_pickname);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listView = mView.findViewById(R.id.list_names);
        ArrayList<String> names = new ArrayList<>();
        for (Player p : mTargets)
            names.add(p.getName());
        final TargetsListAdapter tla = new TargetsListAdapter(mContext, names);
        listView.setAdapter(tla);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        Utils.justifyListViewHeightBasedOnChildren(listView);
        final ImageView icone = mView.findViewById(R.id.icn);
        final TextView text = mView.findViewById(R.id.text);
        final AppCompatButton btn = mView.findViewById(R.id.btn_next);
        btn.setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                targeted = mTargets.get(i);
                tla.setSelectedIndex(i);
                tla.notifyDataSetChanged();
                if (btn.getVisibility() == View.GONE)
                    btn.setVisibility(View.VISIBLE);
            }
        });
        if (mMode == TARGET_MODE.WOLF) {
            Picasso.get().load(R.drawable.wolf).into(icone);
            text.setText(String.format("%s %s", mName, getString(R.string.kill_someone)));
        }
        if (mMode == TARGET_MODE.VOTE) {
            Picasso.get().load(R.drawable.villager).into(icone);
            text.setText(String.format("%s %s", mName, getString(R.string.vote_someone)));
        }
        btn.setText(getString(R.string.ok));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (targeted != null) {
                    ((GameActivity) mContext).playerVote(mId, targeted.getId());
                }
                ((GameActivity) mContext).onBackPressed();
            }
        });
    }

    public enum TARGET_MODE {WOLF, VOTE}
}
