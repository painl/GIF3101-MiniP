package ca.ulaval.ima.bluegarou.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.ulaval.ima.bluegarou.R;

public class AbstractFragment extends Fragment {
    private View mView;
    private Context mContext;
    private int mLayout;

    public static AbstractFragment newInstance(int layout) {
        AbstractFragment abstractFragment = new AbstractFragment();
        abstractFragment.mLayout = layout;
        return abstractFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(mLayout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view.findViewById(R.id.fragment_root);
        mContext = getContext();
    }
}

