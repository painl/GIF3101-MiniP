package ca.ulaval.ima.mp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.List;

import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.game.Game;

abstract public class GameActivity extends AppCompatActivity {

    protected FrameLayout mFragment;
    protected Game mGame;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int LOST_DEVICE = 6;
    public static final String TOAST = "toast";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";

    /**
     * Member object for the service
     */
    public BluetoothService mBluetoothService = null;

    /**
     * Member object for the handler
     */
    public Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothService = new BluetoothService(this, mHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService.getState() != BluetoothService.STATE_NONE) {
            mBluetoothService.stop();
        }
    }

    public BluetoothService getBluetoothService() {
        return mBluetoothService;
    }

    public void startGame(List<String> playerNames) {
        mGame = new Game(this, playerNames);
    }

    public boolean fragmentTransit(Fragment transit, boolean toBackStack)
    {
        if (transit != null)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(mFragment.getId(), transit, transit.getClass().getSimpleName());
            if (toBackStack)
                transaction.addToBackStack(transit.getClass().getSimpleName());
            transaction.commit();
            return true;
        }
        return false;
    }
}
