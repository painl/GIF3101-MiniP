package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ca.ulaval.ima.mp.R;
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
        Button test = mView.findViewById(R.id.btn_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).fragmentTransit(AuthFragment.newInstance(), true);
            }
        });
    }
}
