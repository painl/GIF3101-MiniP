package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.game.roles.Role;

public class WinFragment extends AbstractFragment {

    private Role.Side mWinner;

    public static WinFragment newInstance(Role.Side winner)
    {
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
        switch (mWinner)
        {
            case WEREWOLF:
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wolf));
                textYouAre.setText(textYouAre.getText().toString().replace("...", "Les Loups-Garou"));
                break;
            default:
                imgRole.setImageDrawable(mContext.getResources().getDrawable(R.drawable.villager));
                textYouAre.setText(textYouAre.getText().toString().replace("...", "Les Villageois"));
                break ;
        }
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}
