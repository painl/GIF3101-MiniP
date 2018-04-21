package ca.ulaval.ima.mp.bluetooth.messages;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;

public interface IGameMessage {
    public byte[] serialize();
    public BluetoothMessage.MessageType getType();
}
