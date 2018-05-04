package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.game.Player;

public class ChoicePlayerFragment extends AbstractFragment {

    private Player mPlayer;

    public static ChoicePlayerFragment newInstance(Player player) {
        ChoicePlayerFragment fragment = new ChoicePlayerFragment();
        fragment.mPlayer = player;
        fragment.setLayout(R.layout.fragment_game_player_choice);
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
        switch (mPlayer.getRole().getPhotoName()) {
            case "wolf.png":
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wolf));
                break;
            default:
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.villager));
                break;
        }
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) mContext).onBackPressed();
            }
        });
    }
}
