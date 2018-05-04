package ca.ulaval.ima.mp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import ca.ulaval.ima.mp.R;
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

import static ca.ulaval.ima.mp.bluetooth.BluetoothService.TOAST;

abstract public class GameActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    /**
     * Member object for the service
     */
    public BluetoothService mBluetoothService = null;
    /**
     * Member object for the handler
     */
    public Handler mHandler = null;
    protected FrameLayout mFragment;
    protected Game mGame;
    protected TextToSpeech tts;
    protected boolean ttsMuted;
    private Menu menu = null;
    private BluetoothService.EventType[] eventTypeValues;
    private boolean paused;
    private Queue<BluetoothMessage> queuedMessages;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paused = false;
        eventTypeValues = BluetoothService.EventType.values();
        queuedMessages = new LinkedList<>();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (eventTypeValues[msg.what]) {
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
                        Toast.makeText(GameActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        mBluetoothService = new BluetoothService(this, mHandler);
        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_options, menu);
        this.menu = menu;
        this.toggleTTS();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mute_btn) { //or switch-case
            this.toggleTTS();
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleTTS() {
        MenuItem muteBtn = this.menu.findItem(R.id.mute_btn);
        if (this.ttsMuted) {
            Toast.makeText(this, "Voix artificielle activée", Toast.LENGTH_LONG).show();
            muteBtn.setTitle(R.string.mute);
        } else {
            Toast.makeText(this, "Voix artificielle désactivée", Toast.LENGTH_LONG).show();
            muteBtn.setTitle(R.string.unmute);
        }
        this.ttsMuted = !this.ttsMuted;
    }

    private void speak(String line) {
        if (!this.ttsMuted)
            this.tts.speak(line, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService.getState() != BluetoothService.StateType.STATE_NONE) {
            mBluetoothService.stop();
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    public BluetoothService getBluetoothService() {
        return mBluetoothService;
    }

    public void startGame(LinkedHashMap<String, Role.Type> roleMap) {
        mGame = new Game(this, roleMap);
        startRolesStep(mGame.getPlayers());
    }

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        paused = false;
        while (!queuedMessages.isEmpty())
            interpretMessage(queuedMessages.poll());
    }

    public boolean fragmentTransit(Fragment transit, boolean toBackStack) {
        if (transit != null) {
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

    public void startRolesStep(List<Player> players) {
        this.fragmentTransit(GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.ROLES, players), false);
    }

    public void startRevealStep(Player player) {
        this.fragmentTransit(RevealRoleFragment.newInstance(player), true);
    }

    public void startDebateStep(int turn) {
        String speech = "Le jour se lève sur le village.";
        speech += (turn == 0) ? "Vous sentez qu'un ou plusieurs loups-garous sont cachés parmi vous. Vous devez les trouver. Mais avant, faites connaissances." : "C'est l'heure du débat! Vous avez 10 minutes pour débusquez les intrus";
        this.fragmentTransit(GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.DEBATE, null), false);
        this.speak(speech);
    }

    public void startWolvesStep(List<Player> wolves, List<Player> meals) {
        GameDuoFragment wolvesFragment = GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.WOLVES, wolves);
        wolvesFragment.setTargets(meals);
        this.fragmentTransit(wolvesFragment, false);
        this.speak("La nuit tombe sur le village... Loups-garous, réveillez-vous et choisissez votre victime");
    }

    public void startVotesStep(List<Player> votes) {
        GameDuoFragment fragment = GameDuoFragment.newInstance(GameDuoFragment.CHOICE_MODE.VOTES, votes);
        fragment.setTargets(votes);
        this.fragmentTransit(fragment, false);
        this.speak("Qui sera pendu? Votez chacun votre tour");
    }

    public void startTargetStep(Player name, List<Player> targets, TargetFragment.TARGET_MODE mode) {
        this.fragmentTransit(TargetFragment.newInstance(name, targets, mode), true);
    }

    public void startDeathStep(List<Player> deads, Game.Time time) {
        Player dead = deads.get(0);
        String speech = dead.getName() + " est mort ! C'était un ";
        speech += (dead.getRole().getSide() == Role.Side.WEREWOLF) ? "loup-garou" : "villageois";
        this.fragmentTransit(DeathFragment.newInstance(deads.get(0), time), false);
        this.speak(speech);
    }

    public void endDeathStep(Game.Time time) {
        if (time == Game.Time.NIGHT)
            prepareStep(1);
        else
            prepareStep(3);
    }

    public void startWinStep(Role.Side winner) {
        String speech = (winner == Role.Side.WEREWOLF) ? "Les loups-garous ont gagnés!" : "Les villageois ont gagnés!";
        this.fragmentTransit(WinFragment.newInstance(winner), false);
        this.speak(speech);
    }

    public Game getGame() {
        return mGame;
    }

    protected void interpretMessage(BluetoothMessage message) {
        if (paused) queuedMessages.add(message);
        else {
            Log.d("MESSAGE", "TYPE : " + message.type);
            switch (message.type) {
                case ROLE_DISPATCH:
                    RoleDispatchMessage dispatchMessage = (RoleDispatchMessage) message.content;
                    Log.d("ROLE DISPATCH", "ROLES : " + dispatchMessage.roles);
                    startGame(dispatchMessage.roles);
                    break;
                case STEP_CHANGE:
                    StepChangeMessage stepChangeMessage = (StepChangeMessage) message.content;
                    Log.d("STEP CHANGE", "STEP : " + stepChangeMessage.stepId);
                    mGame.play(stepChangeMessage.stepId);
                    break;
                case PLAYER_VOTE:
                    PlayerVoteMessage voteMessage = (PlayerVoteMessage) message.content;
                    Log.d("PLAYER VOTE", "FROM : " + voteMessage.playerId + " TO : " + voteMessage.targetId);
                    mGame.playerVote(voteMessage.playerId, voteMessage.targetId);
                    ((AbstractFragment) getSupportFragmentManager()
                            .findFragmentById(mFragment.getId())).onBluetoothResponse();
                    break;
            }
        }
    }

    protected abstract void remoteConnected(Message msg);

    protected abstract void remoteDisconnected(Message msg);
}