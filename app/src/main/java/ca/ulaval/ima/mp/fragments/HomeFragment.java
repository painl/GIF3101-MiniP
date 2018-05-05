package ca.ulaval.ima.mp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.MainActivity;
import ca.ulaval.ima.mp.activities.RemoteGameActivity;
import ca.ulaval.ima.mp.activities.ServerGameActivity;
import ca.ulaval.ima.mp.application.BlueGarouApplication;

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
        final Button authBtn = mView.findViewById(R.id.btn_auth);
        if (BlueGarouApplication.getInstance().getAuth())
            authBtn.setText(R.string.logout);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BlueGarouApplication.getInstance().getAuth())
                {
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("BLUEGAROU", Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove("access_token").commit();
                    BlueGarouApplication.getInstance().setAuth(false);
                    authBtn.setText(R.string.log_in);
                }
                else
                    ((MainActivity)mContext).fragmentTransit(AuthFragment.newInstance(AuthFragment.AUTH.LOGIN), true);
            }
        });

        Button playBtn = mView.findViewById(R.id.btn_play);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ServerGameActivity.class);
                mContext.startActivity(intent);
            }
        });

        Button joinBtn = mView.findViewById(R.id.btn_join);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RemoteGameActivity.class);
                mContext.startActivity(intent);
            }
        });
    }
}
