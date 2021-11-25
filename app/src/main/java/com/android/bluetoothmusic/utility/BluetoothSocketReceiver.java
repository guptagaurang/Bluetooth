package com.android.bluetoothmusic.utility;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.fragment.bluetooth.BluetoothFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothSocketReceiver extends Thread {

    private Handler handler;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;

    public BluetoothSocketReceiver(Handler handler, BluetoothSocket bluetoothSocket) {
        try {
            this.handler = handler;
            this.bluetoothSocket = bluetoothSocket;
            inputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    e);
        }
    }

    @Override
    public void run() {

        try {
            byte[] buffer = new byte[1024];
            byte[] imgBuffer = new byte[1024 * 1024 * 20];

            int totalBytes = 0;

            while (true) {

                    Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            imgBuffer.length,
                            totalBytes + " position");
                    int bytes = inputStream.read(buffer);
                    System.arraycopy(buffer, 0, imgBuffer, totalBytes, bytes);
                    totalBytes += bytes;
                handler.obtainMessage(BluetoothFragment.STATE_MESSAGE_RECEIVED, totalBytes, -1, imgBuffer).sendToTarget();
            }


        } catch (Exception e) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    e);
        }

    }

    public void write(byte[] bytesReceived, int element, int length) {
        try {
            if (bluetoothSocket.isConnected()) {
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(bytesReceived, element, length);
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        bytesReceived.length);

                //outputStream.flush();
                //outputStream.close();
            } else {
                Message message = Message.obtain();
                message.what = BluetoothFragment.STATE_DISCONNECTED;
                handler.sendMessage(message);
            }
        } catch (IOException e) {
            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    e);
        }
    }

}