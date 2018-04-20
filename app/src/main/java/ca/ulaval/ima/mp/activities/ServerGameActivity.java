package ca.ulaval.ima.mp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import ca.ulaval.ima.mp.bluetooth.BluetoothService;
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
                LobbyFragment lobbyFrag;
                String mConnectedDeviceName;
                String mConnectedDeviceAddress;

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
                        // TODO this should display the same thing as in the remote Activity
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        // TODO create custom object for commands : -> page to go to, -> list of players
                        String writeMessage = new String(writeBuf);
                        // mConversationArrayAdapter.add("Me:  " + writeMessage);
                        break;
                    case MESSAGE_READ:
                        // TODO this should interpret the response of the client
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        // TODO create custom object for responses : -> player selected
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        // mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                        break;
                    case MESSAGE_DEVICE_NAME:
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        mConnectedDeviceAddress = msg.getData().getString(DEVICE_ADDRESS);
                        Log.d("TEST", "Connected to " + mConnectedDeviceName);
                        Toast.makeText(ServerGameActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                        remotes.put(mConnectedDeviceAddress, mConnectedDeviceName);
                        lobbyFrag = (LobbyFragment) getSupportFragmentManager().findFragmentById(mFragment.getId());
                        if (lobbyFrag != null) {
                            lobbyFrag.updateRemoteList(remotes.values());
                        }
                        break;
                    case LOST_DEVICE:
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        mConnectedDeviceAddress = msg.getData().getString(DEVICE_ADDRESS);
                        Log.d("TEST", "Disconnected to " + mConnectedDeviceName);
                        Toast.makeText(ServerGameActivity.this, "Disconnected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                        remotes.remove(mConnectedDeviceAddress);
                        lobbyFrag = (LobbyFragment) getSupportFragmentManager().findFragmentById(mFragment.getId());
                        if (lobbyFrag != null) {
                            lobbyFrag.updateRemoteList(remotes.values());
                        }
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
}
