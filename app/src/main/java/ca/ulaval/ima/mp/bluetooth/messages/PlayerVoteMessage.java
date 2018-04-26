package ca.ulaval.ima.mp.bluetooth.messages;

import java.nio.ByteBuffer;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;

public class PlayerVoteMessage implements IGameMessage {

    public int playerId;
    public int targetId;

    public PlayerVoteMessage(int playerId, int targetId) {
        this.playerId = playerId;
        this.targetId = targetId;
    }

    public static PlayerVoteMessage unserialize(byte[] array, int length) {
        ByteBuffer wrapped = ByteBuffer.wrap(array);

        return new PlayerVoteMessage(wrapped.getInt(),  wrapped.getInt());
    }

    public byte[] serialize() {
        ByteBuffer dbuf = ByteBuffer.allocate(Integer.SIZE / 8 * 2);

        dbuf.putInt(playerId);
        dbuf.putInt(targetId);
        return dbuf.array();
    }

    public BluetoothMessage.MessageType getType(){
        return BluetoothMessage.MessageType.PLAYER_VOTE;
    }
}
