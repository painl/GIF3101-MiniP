package ca.ulaval.ima.mp.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.adapters.LobbyListAdapter;
import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.bluetooth.DeviceListActivity;

public class RemoteLobbyFragment extends AbstractFragment {

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the service
     */
    public BluetoothService mBluetoothService = null;

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    public static RemoteLobbyFragment newInstance() {
        RemoteLobbyFragment fragment = new RemoteLobbyFragment();
        fragment.setLayout(R.layout.fragment_remote_lobby);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            ((GameActivity)mContext).finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mBluetoothService == null) {
            mBluetoothService = ((GameActivity) getActivity()).getBluetoothService();
            setupClient();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == BluetoothService.StateType.STATE_NONE) {
                // Start the Bluetooth chat services
                setupClient();
            }
        }
    }

    private void setupClient() {
        mBluetoothService.start();
        ensureDiscoverable();
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupClient();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d("LobbyFragment", "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    ((AppCompatActivity)mContext).finish();
                }
        }
    }
}
