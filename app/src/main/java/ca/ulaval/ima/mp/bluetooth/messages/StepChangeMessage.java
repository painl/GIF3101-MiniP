package ca.ulaval.ima.mp.bluetooth.messages;

import java.nio.ByteBuffer;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;

public class StepChangeMessage implements IGameMessage {

    public int stepId;

    public StepChangeMessage(int stepId) {
        this.stepId = stepId;
    }

    public static StepChangeMessage unserialize(byte[] array, int length) {
        ByteBuffer wrapped = ByteBuffer.wrap(array); // big-endian by default

        int stepId = wrapped.getInt();
        return new StepChangeMessage(stepId);
    }

    public byte[] serialize() {
        ByteBuffer dbuf = ByteBuffer.allocate(4);

        dbuf.putInt(stepId);
        return dbuf.array();
    }

    public BluetoothMessage.MessageType getType(){
        return BluetoothMessage.MessageType.STEP_CHANGE;
    }
}
