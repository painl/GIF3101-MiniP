package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ca.ulaval.ima.bluegarou.R;

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
        TextView t = mView.findViewById(R.id.message);
        t.setText("lolololol");
    }
}
