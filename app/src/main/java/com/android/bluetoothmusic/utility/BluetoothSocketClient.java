package com.android.bluetoothmusic.utility;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.fragment.bluetooth.BluetoothFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;

import java.util.UUID;

public class BluetoothSocketClient extends Thread {

    private Handler handler;
    private BluetoothDevice device;
    private BluetoothSocket bluetoothSocket;

    public BluetoothSocketClient(Handler handler, BluetoothDevice device, UUID uuid) {
        try {
            this.handler = handler;
            this.device = device;

            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    e);
        }
    }

    @Override
    public void run() {
        try {
            bluetoothSocket.connect();
            Message message = Message.obtain();
            message.what = BluetoothFragment.STATE_SENDER_CONNECTED;
            handler.sendMessage(message);
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    BluetoothFragment.STATE_SENDER_CONNECTED);

            DashboardObserverActivity.bluetoothSocketReceiver = new BluetoothSocketReceiver(handler, bluetoothSocket);
            DashboardObserverActivity.bluetoothSocketReceiver.start();

        } catch (Exception e) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    e);
            Message message = Message.obtain();
            message.what = BluetoothFragment.STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
        }
    }

}
