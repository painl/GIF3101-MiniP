package ca.ulaval.ima.mp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;
import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.bluetooth.messages.RoleDispatchMessage;
import ca.ulaval.ima.mp.fragments.LobbyFragment;
import ca.ulaval.ima.mp.fragments.RemoteLobbyFragment;

public class ServerGameActivity extends GameActivity {

    public HashMap<String, String>      remotes;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    /*case MESSAGE_STATE_CHANGE:
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
                        break;*/
                    case MESSAGE_WRITE:
                        interpretMessage((BluetoothMessage) msg.obj);
                        break;
                    case MESSAGE_READ:
                        interpretMessage((BluetoothMessage) msg.obj);
                        break;
                    case MESSAGE_DEVICE_NAME:
                        remoteConnected(msg);
                        break;
                    case LOST_DEVICE:
                        remoteDisconnected(msg);
                        break;
                    case MESSAGE_TOAST:
                        Toast.makeText(ServerGameActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        super.onCreate(savedInstanceState);

        remotes = new HashMap<>();
        mBluetoothService = new BluetoothService(this, mHandler);
        mFragment = findViewById(android.R.id.content);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mFragment.getId(), LobbyFragment.newInstance());
        ft.commit();
    }

    private void remoteConnected(Message msg) {
        String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
        String mConnectedDeviceAddress = msg.getData().getString(DEVICE_ADDRESS);
        Toast.makeText(ServerGameActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        remotes.put(mConnectedDeviceAddress, mConnectedDeviceName);
        ServerGameActivity.this.updateRemoteList();
    }

    private void remoteDisconnected(Message msg) {
        String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
        String mConnectedDeviceAddress = msg.getData().getString(DEVICE_ADDRESS);
        Toast.makeText(ServerGameActivity.this, "Disconnected from " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        remotes.remove(mConnectedDeviceAddress);
        ServerGameActivity.this.updateRemoteList();
    }

    private void updateRemoteList() {
        LobbyFragment lobbyFrag = (LobbyFragment) getSupportFragmentManager()
                .findFragmentById(mFragment.getId());
        if (lobbyFrag != null) {
            lobbyFrag.updateRemoteList(remotes.values());
        }
    }

    private void interpretMessage(BluetoothMessage message) {
        // TODO according to the class stored, do stuff
        Log.d("MESSAGE", "TYPE : "  + message.type);
        switch (message.type) {
            case ROLE_DISPATCH:
                // TODO display all players with roles hidden except on click
                Log.d("MESSAGE", ((RoleDispatchMessage)message.content).roles.toString());
                break;
        }
    }
}
