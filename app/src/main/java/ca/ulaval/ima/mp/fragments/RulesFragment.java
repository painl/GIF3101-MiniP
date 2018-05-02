package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import ca.ulaval.ima.mp.R;

public class RulesFragment extends AbstractFragment {

    public static RulesFragment newInstance()
    {
        RulesFragment rulesFragment = new RulesFragment();
        rulesFragment.setLayout(R.layout.fragment_rules);
        return rulesFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
