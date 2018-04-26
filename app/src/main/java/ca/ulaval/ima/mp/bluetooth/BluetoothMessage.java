package ca.ulaval.ima.mp.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import ca.ulaval.ima.mp.bluetooth.messages.IGameMessage;
import ca.ulaval.ima.mp.bluetooth.messages.PlayerVoteMessage;
import ca.ulaval.ima.mp.bluetooth.messages.RoleDispatchMessage;
import ca.ulaval.ima.mp.bluetooth.messages.StepChangeMessage;

public class BluetoothMessage<M extends IGameMessage> {

    public enum MessageType {
        ROLE_DISPATCH,
        STEP_CHANGE,
        PLAYER_VOTE
    }

    public M content;

    public MessageType type;

    public BluetoothMessage(M message) {
        this.content = message;
        this.type = message.getType();
    }

    byte[] serialize() {

        byte[] message = this.content.serialize();

        byte[] messageLength = ByteBuffer.allocate(Integer.SIZE / 8)
                .putInt(message.length + 1).array();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(messageLength);
            outputStream.write((byte) type.ordinal());
            outputStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    static BluetoothMessage unserialize(byte[] array, int length) {
        byte[] content = Arrays.copyOfRange(array, 1, array.length);

        switch ((int) array[0]) {
            case 0:
                return new BluetoothMessage<>(RoleDispatchMessage.unserialize(content, length));
            case 1:
                return new BluetoothMessage<>(StepChangeMessage.unserialize(content, length));
            case 2:
                return new BluetoothMessage<>(PlayerVoteMessage.unserialize(content, length));
        }
        return null;
    }
}
