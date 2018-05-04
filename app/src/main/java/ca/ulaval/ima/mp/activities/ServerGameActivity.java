package ca.ulaval.ima.mp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;
import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.bluetooth.messages.PlayerVoteMessage;
import ca.ulaval.ima.mp.bluetooth.messages.RoleDispatchMessage;
import ca.ulaval.ima.mp.bluetooth.messages.StepChangeMessage;
import ca.ulaval.ima.mp.fragments.AbstractFragment;
import ca.ulaval.ima.mp.fragments.LobbyFragment;
import ca.ulaval.ima.mp.fragments.RemoteLobbyFragment;
import ca.ulaval.ima.mp.game.Referee;
import ca.ulaval.ima.mp.game.roles.Role;

public class ServerGameActivity extends GameActivity {

    public HashMap<String, String> remotes;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.ttsMuted = true;
        super.onCreate(savedInstanceState);

        remotes = new HashMap<>();
        mBluetoothService = new BluetoothService(this, mHandler);
        mFragment = findViewById(android.R.id.content);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mFragment.getId(), LobbyFragment.newInstance());
        ft.commit();
    }

    @Override
    protected void remoteConnected(Message msg) {
        String mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
        String mConnectedDeviceAddress = msg.getData().getString(BluetoothService.DEVICE_ADDRESS);
        Toast.makeText(ServerGameActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        remotes.put(mConnectedDeviceAddress, mConnectedDeviceName);
        updateRemoteList();
    }

    @Override
    protected void remoteDisconnected(Message msg) {
        String mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
        String mConnectedDeviceAddress = msg.getData().getString(BluetoothService.DEVICE_ADDRESS);
        Toast.makeText(ServerGameActivity.this, "Disconnected from " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
        remotes.remove(mConnectedDeviceAddress);
        updateRemoteList();
    }

    public void prepareGame(List<String> playerNames) {
        BluetoothMessage<RoleDispatchMessage> message =
                new BluetoothMessage<>(new RoleDispatchMessage(Referee.assignRoles(playerNames)));
        mBluetoothService.write(message);
    }

    private void updateRemoteList() {
        try {
            LobbyFragment lobbyFrag = (LobbyFragment) getSupportFragmentManager()
                    .findFragmentById(mFragment.getId());
            if (lobbyFrag != null) {
                lobbyFrag.updateRemoteList(new ArrayList<>(remotes.values()));
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
