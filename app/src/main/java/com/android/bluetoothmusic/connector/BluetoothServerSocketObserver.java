package com.android.bluetoothmusic.connector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.android.bluetoothmusic.BuildConfig;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.fragment.bluetooth.BluetoothFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.utility.BluetoothSocketReceiver;

import java.io.IOException;
import java.util.UUID;

/**
 * When connecting... or searching to connect via bluetooth socket.
 */
public class BluetoothServerSocketObserver extends Thread {

    private Handler handler;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothServerSocketObserver(Handler handler, BluetoothAdapter bluetoothAdapter, UUID uuid) {
        try {
            this.handler = handler;
            this.bluetoothAdapter = bluetoothAdapter;
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BuildConfig.APPLICATION_ID, uuid);
        } catch (Exception e) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    e);
        }
    }

    @Override
    public void run() {
        while (bluetoothSocket == null) {
            try {
                Message message = Message.obtain();
                message.what = BluetoothFragment.STATE_CONNECTING;
                handler.sendMessage(message);

                bluetoothSocket = serverSocket.accept();
            } catch (IOException e) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        e);
                Message message = Message.obtain();
                message.what = BluetoothFragment.STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }

            if (bluetoothSocket != null) {
                try {
                    Message message = Message.obtain();
                    message.what = BluetoothFragment.STATE_RECEIVER_CONNECTED;
                    handler.sendMessage(message);

                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            "STATE_CONNECTED : " +bluetoothAdapter.getName());

                    DashboardObserverActivity.bluetoothSocketReceiver = new BluetoothSocketReceiver(handler, bluetoothSocket);
                    DashboardObserverActivity.bluetoothSocketReceiver.start();
                } catch (Exception e) {
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            e);
                }
                break;
            }
        }
    }
}