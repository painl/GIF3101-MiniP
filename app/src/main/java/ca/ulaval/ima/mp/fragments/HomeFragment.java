package ca.ulaval.ima.mp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.activities.MainActivity;

public class HomeFragment extends AbstractFragment {

    public static HomeFragment newInstance()
    {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setLayout(R.layout.fragment_home);
        return homeFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = mView.findViewById(R.id.img);
        Picasso.get().load(R.drawable.girl).into(imageView);
        Button authBtn = mView.findViewById(R.id.btn_auth);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).fragmentTransit(AuthFragment.newInstance(), true);
            }
        });

        Button playBtn = mView.findViewById(R.id.btn_play);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GameActivity.class);
                mContext.startActivity(intent);
            }
        });
    }
}
