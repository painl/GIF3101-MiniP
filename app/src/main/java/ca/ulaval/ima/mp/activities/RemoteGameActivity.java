package ca.ulaval.ima.mp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.HashMap;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;
import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.bluetooth.DeviceListActivity;
import ca.ulaval.ima.mp.fragments.LobbyFragment;
import ca.ulaval.ima.mp.fragments.RemoteLobbyFragment;

public class RemoteGameActivity extends GameActivity {

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mBluetoothAdapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothService.STATE_CONNECTED:
                                // setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                                // mConversationArrayAdapter.clear();
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                // setStatus(R.string.title_connecting);
                                break;
                            case BluetoothService.STATE_LISTEN:
                            case BluetoothService.STATE_NONE:
                                // setStatus(R.string.title_not_connected);
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        // construct a message from the buffer
                        BluetoothMessage writeMessage = (BluetoothMessage) msg.obj;
                        Log.d("TEST", "wrote " + writeMessage.type);
                        break;
                    case MESSAGE_READ:
                        // TODO this should interpret the request of the server
                        BluetoothMessage readMessage = (BluetoothMessage) msg.obj;
                        Log.d("TEST", "read " + readMessage.type);
                    case MESSAGE_DEVICE_NAME:
                        String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        String mConnectedDeviceAddress = msg.getData().getString(DEVICE_ADDRESS);
                        Log.d("TEST", "Connected to " + mConnectedDeviceName);
                        Toast.makeText(RemoteGameActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                        // remotes.put(mConnectedDeviceAddress, mConnectedDeviceName);
                        break;
                    case MESSAGE_TOAST:
                        Toast.makeText(RemoteGameActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        super.onCreate(savedInstanceState);

        mBluetoothService = new BluetoothService(this, mHandler);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        mFragment = findViewById(android.R.id.content);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mFragment.getId(), RemoteLobbyFragment.newInstance());
        ft.commit();
    }
}
