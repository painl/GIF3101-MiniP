package ca.ulaval.ima.mp.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.fragments.RemoteLobbyFragment;

public class RemoteGameActivity extends GameActivity {

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.ttsMuted = false;
        super.onCreate(savedInstanceState);

        mBluetoothService = new BluetoothService(this, mHandler);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_availaible, Toast.LENGTH_LONG).show();
            this.finish();
        }

        mFragment = findViewById(android.R.id.content);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mFragment.getId(), RemoteLobbyFragment.newInstance());
        ft.commit();
    }

    @Override
    protected void remoteConnected(Message msg) {
        String mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
        Toast.makeText(this, getString(R.string.connect_to) + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void remoteDisconnected(Message msg) {
        String mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
        Toast.makeText(this, getString(R.string.disconnect_from) + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
    }
}
