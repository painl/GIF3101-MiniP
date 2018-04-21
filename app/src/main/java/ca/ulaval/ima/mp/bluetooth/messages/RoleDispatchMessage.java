package ca.ulaval.ima.mp.bluetooth.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import ca.ulaval.ima.mp.game.roles.Role;

public class RoleDispatchMessage implements IGameMessage{

    public Map<String, Role.Type> roles;

    public RoleDispatchMessage(Map<String, Role.Type> roles) {
        this.roles = roles;
    }

    public static RoleDispatchMessage unserialize(byte[] array, int length) {

        ByteArrayInputStream byteIn = new ByteArrayInputStream(array);
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(byteIn);
            Map<String, Role.Type> roles = (Map<String, Role.Type>) in.readObject();
            return new RoleDispatchMessage(roles);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] serialize() {
        ObjectOutputStream outputStream;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try {
            outputStream = new ObjectOutputStream(byteOut);
            outputStream.writeObject( roles );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }
}
