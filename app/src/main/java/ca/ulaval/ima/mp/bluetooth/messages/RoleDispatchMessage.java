package ca.ulaval.ima.mp.bluetooth.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

import ca.ulaval.ima.mp.bluetooth.BluetoothMessage;
import ca.ulaval.ima.mp.game.roles.Role;

public class RoleDispatchMessage implements IGameMessage {

    public LinkedHashMap<String, Role.Type> roles;

    public RoleDispatchMessage(LinkedHashMap<String, Role.Type> roles) {
        this.roles = roles;
    }

    public static RoleDispatchMessage unserialize(byte[] array, int length) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(array);
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(byteIn);
            LinkedHashMap<String, Role.Type> roles = (LinkedHashMap<String, Role.Type>) in.readObject();
            return new RoleDispatchMessage(roles);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] serialize() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(byteOut);
            outputStream.writeObject(roles);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    public BluetoothMessage.MessageType getType() {
        return BluetoothMessage.MessageType.ROLE_DISPATCH;
    }
}
