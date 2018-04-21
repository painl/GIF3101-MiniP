package ca.ulaval.ima.mp.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import ca.ulaval.ima.mp.bluetooth.messages.IGameMessage;
import ca.ulaval.ima.mp.bluetooth.messages.RoleDispatchMessage;

public class BluetoothMessage<M extends IGameMessage> {

    public enum MessageType {
        ROLE_DISPATCH,
        WEREWOLF_TURN,
        PLAYER_KILLED,
        VOTING,
        WINNING,
        PLAYER_CHOSEN
    }

    public M content;

    public MessageType type;

    public BluetoothMessage(M message, MessageType type) {
        this.content = message;
        this.type = type;
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
                BluetoothMessage<RoleDispatchMessage> message =
                        new BluetoothMessage<>(RoleDispatchMessage.unserialize(content, length), MessageType.ROLE_DISPATCH);
                message.type = MessageType.ROLE_DISPATCH;
                return message;
        }
        return null;
    }
}
