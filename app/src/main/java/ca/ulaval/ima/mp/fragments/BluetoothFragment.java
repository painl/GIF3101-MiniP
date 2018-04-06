package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;

import ca.ulaval.ima.mp.R;

public class BluetoothFragment extends AbstractFragment {

    public static BluetoothFragment newInstance() {
        BluetoothFragment fragment = new BluetoothFragment();
        fragment.setLayout(R.layout.fragment_bluetooth);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        start();
    }

    private void start() {
        // BlueGarouApplication.getInstance().getWebService().GETRequest("urldelapi", onGetObj());
    }
}
