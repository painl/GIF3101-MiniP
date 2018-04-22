package ca.ulaval.ima.mp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.ulaval.ima.mp.bluetooth.BluetoothService;
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
    public SeenState mSeenState;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int LOST_DEVICE = 6;
    public static final String TOAST = "toast";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";

    public class SeenState
    {
        public ArrayList<Player> validates = new ArrayList<>();
        int nbPlayers;
        public int playerIndex = -1;
        SeenState(int nb)
        {
            nbPlayers = nb;
            for (int i=0; i < nb; i++)
                validates.add(null);
        }

        public int getRemaining()
        {
            int i = 0;
            for (Player p : validates)
                if (p == null)
                    i++;
            return i;
        }

        public Player getTargetVote()
        {
            return validates.get(playerIndex);
        }
    }

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

    public void startRolesStep(List<Player> players)
    {
        mSeenState = new SeenState(players.size());
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
        this.mSeenState = new SeenState(wolves.size());
        GameDuoFragment wolvesFragment = GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.WOLVES, wolves);
        wolvesFragment.setTargets(meals);
        this.fragmentTransit(wolvesFragment, false);
    }

    public void startVotesStep(List<Player> votes)
    {
        this.mSeenState = new SeenState(votes.size());
        GameDuoFragment fragment = GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.VOTES, votes);
        fragment.setTargets(votes);
        this.fragmentTransit(fragment, false);
    }

    public void startTargetStep(String name, List<Player> targets, TargetFragment.TARGET_MODE mode)
    {
        this.fragmentTransit(TargetFragment.newInstance(name, targets, mode), true);
    }

    public void startDeathStep(List<Player> deads, Game.Time time)
    {
        Stack<Player> stack = new Stack<>();
        stack.addAll(deads);
        if (stack.size() > 0)
            this.fragmentTransit(DeathFragment.newInstance(stack, time), false);
        else {
            if (time == Game.Time.NIGHT)
                getGame().play(1);
            else
                getGame().play(3);
        }
    }

    public void startWinStep(Role.Side winner)
    {
        this.fragmentTransit(WinFragment.newInstance(winner), false);
    }

    public void passStep()
    {
        this.getGame().nextState();
    }

    public Game getGame()
    {
        return mGame;
    }
}
