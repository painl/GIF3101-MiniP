package ca.ulaval.ima.mp.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import ca.ulaval.ima.mp.activities.GameActivity;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService iBT";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "i BT";

    // Unique UUID for this application
    private static UUID MY_UUID;

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private StateType mState;

    private ArrayMap<String, ConnectedThread> mConnThreads;

    // Constants that indicate the current connection state
    public enum StateType {
        STATE_NONE,  // we're doing nothing
        STATE_LISTEN, // now listening for incoming
        STATE_CONNECTING, // now initiating an outgoing
        STATE_CONNECTED // now connected to a remote
    }

    public enum EventType {
        MESSAGE_STATE_CHANGE,
        MESSAGE_READ,
        MESSAGE_WRITE,
        MESSAGE_DEVICE_NAME,
        MESSAGE_TOAST,
        LOST_DEVICE
    }

    public static final String TOAST = "toast";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param handler
     *            A Handler to send messages back to the UI Activity
     */
    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = StateType.STATE_NONE;
        mHandler = handler;
        mConnThreads = new ArrayMap<>();
    }

    private static UUID getMY_UUID() {
        return MY_UUID;
    }

    private static void setMY_UUID(UUID mY_UUID) {
        MY_UUID = mY_UUID;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state
     *            An integer defining the current connection state
     */
    private synchronized void setState(StateType state) {
        if (D)
            Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(EventType.MESSAGE_STATE_CHANGE.ordinal(), state.ordinal(), -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized StateType getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        if (D)
            Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(StateType.STATE_LISTEN);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device
     *            The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (mConnThreads.get(device.getAddress()) == null) {
            if (D)
                Log.d(TAG, "connect to: " + device);

            // Cancel any thread attempting to make a connection
            if (mState == StateType.STATE_CONNECTING) {
                if (mConnectThread != null) {
                    mConnectThread.cancel();
                    mConnectThread = null;
                }
            }

            try {

                // String
                // s="00001101-0000-1000-8000"+device.getAddress().split(":");
                ConnectThread mConnectThread = new ConnectThread(device,
                        UUID.fromString("00001101-0000-1000-8000-" + device.getAddress().replace(":", "")));
                Log.i(TAG, "uuid-string at server side"
                        + ("00001101-0000-1000-8000" + device.getAddress()
                        .replace(":", "")));
                mConnectThread.start();
                setState(StateType.STATE_CONNECTING);
            } catch (Exception e) {
            }
        } else {
            Message msg = mHandler
                    .obtainMessage(EventType.MESSAGE_TOAST.ordinal());
            Bundle bundle = new Bundle();
            bundle.putString(TOAST,
                    "This device " + device.getName() + " Already Connected");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket
     *            The BluetoothSocket on which the connection was made
     * @param device
     *            The BluetoothDevice that has been connected
     */
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D)
            Log.d(TAG, "connected");

        // Start the thread to manage the connection and perform transmissions
        ConnectedThread mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        // Add each connected thread to an array
        mConnThreads.put(device.getAddress(), mConnectedThread);

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(EventType.MESSAGE_DEVICE_NAME.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        bundle.putString(DEVICE_ADDRESS, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(StateType.STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D)
            Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        for (Map.Entry<String, ConnectedThread> entry : mConnThreads.entrySet()) {
            entry.getValue().cancel();
        }

        mConnThreads.clear();

        setState(StateType.STATE_NONE);
    }

    /**
     * Write to all ConnectedThreads in an unsynchronized manner
     *
     * @param outMessage
     *            The message to write
     * @see ConnectedThread#write(BluetoothMessage)
     */
    public void write(BluetoothMessage outMessage) {
        mHandler.obtainMessage(EventType.MESSAGE_WRITE.ordinal(), -1, -1, outMessage).sendToTarget();

        byte out[] = outMessage.serialize();

        // When writing, try to write out to all connected threads
        for (Map.Entry<String, ConnectedThread> pair : mConnThreads.entrySet()) {
            try {
                Log.i(TAG, "SENDING TO THREAD");
                // Create temporary object
                ConnectedThread r;
                // Synchronize a copy of the ConnectedThread
                synchronized (this) {
                    if (mState != StateType.STATE_CONNECTED)
                        return;
                    r = pair.getValue();
                }
                // Perform the write unsynchronized
                r.write(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(StateType.STATE_LISTEN);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(EventType.MESSAGE_TOAST.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost(BluetoothDevice device) {
        mConnThreads.remove(device.getAddress());

        // Send the name of the disconnected device back to the UI Activity
        Message msg = mHandler.obtainMessage(EventType.LOST_DEVICE.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        bundle.putString(DEVICE_ADDRESS, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted (or
     * until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {

                if (mAdapter.isEnabled()) {
                    BluetoothService.setMY_UUID(UUID
                            .fromString("00001101-0000-1000-8000-"
                                    + mAdapter.getAddress().replace(":", "")));
                }
                Log.i(TAG, "MY_UUID.toString()=="
                        + BluetoothService.getMY_UUID().toString());

                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,
                        BluetoothService.getMY_UUID());
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D)
                Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket;
            Log.i(TAG, "mState in acceptThread==" + mState);
            // Listen to the server socket if we're not connected
            while (mState != StateType.STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D)
                Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D)
                Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device, UUID uuidToTry) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuidToTry);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG,
                            "unable to close() socket during connection failure",
                            e2);
                }
                // Start the service over to restart listening mode
                BluetoothService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device. It handles all
     * incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[4];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    int messageLength = ByteBuffer.wrap(buffer).getInt();
                    byte[] content = new byte[messageLength];

                    bytes = mmInStream.read(content);

                    // Send the obtained message to the UI Activity
                    if (bytes > 0) {
                        mHandler.obtainMessage(
                                EventType.MESSAGE_READ.ordinal(),
                                bytes,
                                0,
                                BluetoothMessage.unserialize(content, bytes)).sendToTarget();
                        Log.i("**********read", "read Called........");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost(mmSocket.getRemoteDevice());
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
