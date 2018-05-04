package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.game.Player;

public class RevealRoleFragment extends AbstractFragment {

    private Player mPlayer;

    public static RevealRoleFragment newInstance(Player player) {
        RevealRoleFragment fragment = new RevealRoleFragment();
        fragment.mPlayer = player;
        fragment.setLayout(R.layout.fragment_game_revealrole);
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
