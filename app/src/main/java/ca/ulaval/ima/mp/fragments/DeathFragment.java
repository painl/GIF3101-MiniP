package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.game.Game;
import ca.ulaval.ima.mp.game.Player;

public class DeathFragment extends AbstractFragment {

    private Player mPlayer;
    private Game.Time mTime;

    public static DeathFragment newInstance(Player dead, Game.Time time)
    {
        DeathFragment fragment = new DeathFragment();
        fragment.mPlayer = dead;
        fragment.mTime = time;
        fragment.setLayout(R.layout.fragment_game_death);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imgRole = mView.findViewById(R.id.img_role);
        TextView textYouAre = mView.findViewById(R.id.txt_you_are);
        textYouAre.setText(textYouAre.getText().toString().replace("...", mPlayer.getName()));
        TextView textRole = mView.findViewById(R.id.txt_role);
        textRole.setText(mPlayer.getRole().getName());
        switch (mPlayer.getRole().getPhotoName())
        {
            case "wolf.png":
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wolf));
                break;
            default:
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.villager));
                break ;
        }
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)mContext).endDeathStep(mTime);
            }
        });
    }
}
