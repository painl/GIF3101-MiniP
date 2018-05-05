package ca.ulaval.ima.mp.bluetooth.messages;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;

public interface IGameMessage {
    byte[] serialize();

    BluetoothMessage.MessageType getType();
}
