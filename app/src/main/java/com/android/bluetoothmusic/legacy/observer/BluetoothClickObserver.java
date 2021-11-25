package com.android.bluetoothmusic.legacy.observer;

import java.util.List;

public interface BluetoothClickObserver<T> {

    void click();

    void click(T objects);

    void click(List<T> objects);

}
