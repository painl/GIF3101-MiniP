package ca.ulaval.ima.mp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;
import ca.ulaval.ima.mp.bluetooth.BluetoothService;
import ca.ulaval.ima.mp.bluetooth.messages.PlayerVoteMessage;
import ca.ulaval.ima.mp.bluetooth.messages.RoleDispatchMessage;
import ca.ulaval.ima.mp.bluetooth.messages.StepChangeMessage;
import ca.ulaval.ima.mp.fragments.AbstractFragment;
import ca.ulaval.ima.mp.fragments.DeathFragment;
import ca.ulaval.ima.mp.fragments.GameDuoFragment;
import ca.ulaval.ima.mp.fragments.RevealRoleFragment;
import ca.ulaval.ima.mp.fragments.TargetFragment;
import ca.ulaval.ima.mp.fragments.WinFragment;
import ca.ulaval.ima.mp.game.Game;
import ca.ulaval.ima.mp.game.Player;
import ca.ulaval.ima.mp.game.roles.Role;

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

    public void startGame(LinkedHashMap<String, Role.Type> roleMap) {
        mGame = new Game(this, roleMap);
        startRolesStep(mGame.getPlayers());
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

    public void prepareStep(int stepId) {
        BluetoothMessage<StepChangeMessage> message =
                new BluetoothMessage<>(new StepChangeMessage(stepId));
        mBluetoothService.write(message);
    }

    public void playerVote(int playerId, int targetId) {
        BluetoothMessage<PlayerVoteMessage> message =
                new BluetoothMessage<>(new PlayerVoteMessage(playerId, targetId));
        mBluetoothService.write(message);
    }

    public void startRolesStep(List<Player> players)
    {
        this.fragmentTransit(GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.ROLES, players), false);
    }

    public void startRevealStep(Player player)
    {
        this.fragmentTransit(RevealRoleFragment.newInstance(player), true);
    }

    public void startDebateStep()
    {
        this.fragmentTransit(GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.DEBATE, null), false);
    }

    public void startWolvesStep(List<Player> wolves, List<Player> meals)
    {
        GameDuoFragment wolvesFragment = GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.WOLVES, wolves);
        wolvesFragment.setTargets(meals);
        this.fragmentTransit(wolvesFragment, false);
    }

    public void startVotesStep(List<Player> votes)
    {
        GameDuoFragment fragment = GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.VOTES, votes);
        fragment.setTargets(votes);
        this.fragmentTransit(fragment, false);
    }

    public void startTargetStep(Player name, List<Player> targets, TargetFragment.TARGET_MODE mode)
    {
        this.fragmentTransit(TargetFragment.newInstance(name, targets, mode), true);
    }

    public void startDeathStep(List<Player> deads, Game.Time time)
    {
        this.fragmentTransit(DeathFragment.newInstance(deads.get(0), time), false);
    }

    public void endDeathStep(Game.Time time) {
        if (time == Game.Time.NIGHT)
            prepareStep(1);
        else
            prepareStep(3);
    }

    public void startWinStep(Role.Side winner)
    {
        this.fragmentTransit(WinFragment.newInstance(winner), false);
    }

    public Game getGame()
    {
        return mGame;
    }

    protected void interpretMessage(BluetoothMessage message) {
        Log.d("MESSAGE", "TYPE : "  + message.type);
        switch (message.type) {
            case ROLE_DISPATCH:
                RoleDispatchMessage dispatchMessage = (RoleDispatchMessage)message.content;
                Log.d("ROLE DISPATCH", "ROLES : " + dispatchMessage.roles);
                startGame(dispatchMessage.roles);
                break;
            case STEP_CHANGE:
                StepChangeMessage stepChangeMessage = (StepChangeMessage)message.content;
                Log.d("STEP CHANGE", "STEP : "  + stepChangeMessage.stepId);
                mGame.play(stepChangeMessage.stepId);
                break;
            case PLAYER_VOTE:
                PlayerVoteMessage voteMessage = (PlayerVoteMessage)message.content;
                Log.d("PLAYER VOTE", "FROM : "  + voteMessage.playerId + " TO : " + voteMessage.targetId);
                mGame.playerVote(voteMessage.playerId, voteMessage.targetId);
                ((AbstractFragment)getSupportFragmentManager()
                        .findFragmentById(mFragment.getId())).onBluetoothResponse();
                break;
        }
    }
}
