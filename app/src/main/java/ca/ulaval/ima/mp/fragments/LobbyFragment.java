package ca.ulaval.ima.mp.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.activities.ServerGameActivity;
import ca.ulaval.ima.mp.adapters.LobbyListAdapter;
import ca.ulaval.ima.mp.adapters.RemoteListAdapter;
import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.bluetooth.DeviceListActivity;

public class LobbyFragment extends AbstractFragment {

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the service
     */
    public BluetoothService mBluetoothService = null;

    private ListView remotesListView;

    private LobbyListAdapter playerListAdapter;
    private RemoteListAdapter remoteListAdapter;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    public static LobbyFragment newInstance() {
        LobbyFragment fragment = new LobbyFragment();
        fragment.setLayout(R.layout.fragment_lobby);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView playerListView = mView.findViewById(R.id.player_list);
        playerListAdapter = new LobbyListAdapter(mContext, new ArrayList<String>());
        remoteListAdapter = new RemoteListAdapter(mContext, new ArrayList<String>());
        playerListAdapter.add("Raf");
        playerListAdapter.add("Louis");
        playerListAdapter.add("Antoine");
        playerListAdapter.add("Babou");
        playerListView.setAdapter(playerListAdapter);
        remotesListView = mView.findViewById(R.id.remotes_list);
        Button addPlayerButton = view.findViewById(R.id.btn_add_player);
        Button remoteButton = view.findViewById(R.id.btn_add_remote);
        Button playBtn = view.findViewById(R.id.btn_launch_game);
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final View dialogView = inflater.inflate(R.layout.dialog_add_player, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(LobbyFragment.this.getActivity());
                builder.setTitle("Rentrer le nom du joueur")
                        .setView(dialogView)
                        .setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText valueView = dialogView.findViewById(R.id.username);
                                if (valueView != null) {
                                    playerListAdapter.add(valueView.getText().toString());
                                    dialog.dismiss();
                                }
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                alert.setCanceledOnTouchOutside(true);
                final EditText inputField = dialogView.findViewById(R.id.username);
                final Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setEnabled(false);
                inputField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (inputField.getText().length() > 0 && inputField.getText().length() <= 25) {
                            button.setEnabled(true);
                        } else {
                            button.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }
        });

        remoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mBluetoothService != null) {
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                }
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerListAdapter.getData().size() > 3) {
                    ((ServerGameActivity)mContext).prepareGame(playerListAdapter.getData());
                } else {
                    Toast.makeText(getActivity(), R.string.four_players_needed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else if (mBluetoothService == null) {
                mBluetoothService = ((GameActivity) getActivity()).getBluetoothService();
            }
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
                mBluetoothService.start();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so get service
                    mBluetoothService = ((GameActivity)mContext).getBluetoothService();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d("LobbyFragment", "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBluetoothService.connect(device);
    }

    public void updateRemoteList(ArrayList<String> remoteList) {
        RemoteListAdapter lba2 = new RemoteListAdapter(mContext, remoteList);
        remotesListView.setAdapter(lba2);
    }
}
