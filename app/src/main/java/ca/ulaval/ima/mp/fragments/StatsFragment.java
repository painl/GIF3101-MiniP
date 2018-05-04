package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;

import ca.ulaval.ima.mp.R;

public class StatsFragment extends AbstractFragment {

    public static StatsFragment newInstance() {
        StatsFragment statsFragment = new StatsFragment();
        statsFragment.setLayout(R.layout.fragment_stats);
        return statsFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
