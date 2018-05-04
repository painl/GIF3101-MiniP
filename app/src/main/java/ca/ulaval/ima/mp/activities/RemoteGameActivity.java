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
import ca.ulaval.ima.mp.bluetooth.messages.PlayerVoteMessage;
import ca.ulaval.ima.mp.bluetooth.messages.RoleDispatchMessage;
import ca.ulaval.ima.mp.bluetooth.messages.StepChangeMessage;
import ca.ulaval.ima.mp.fragments.AbstractFragment;
import ca.ulaval.ima.mp.fragments.LobbyFragment;
import ca.ulaval.ima.mp.fragments.RemoteLobbyFragment;
import ca.ulaval.ima.mp.game.roles.Role;

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
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void remoteDisconnected(Message msg) {
        String mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
        Toast.makeText(this, "Disconnected from " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
    }
}
