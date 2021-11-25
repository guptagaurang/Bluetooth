package com.android.bluetoothmusic.fragment.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.adapter.BaseHolder;
import com.android.bluetoothmusic.adapter.DeviceRecyclerViewAdapter;
import com.android.bluetoothmusic.adapter.GenericRecordsAdapter;
import com.android.bluetoothmusic.connector.BluetoothServerSocketObserver;
import com.android.bluetoothmusic.fragment.dashboard.DashboardFragment;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.legacy.observer.ListInteractionListener;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.utility.BluetoothSocketClient;
import com.android.bluetoothmusic.utility.BluetoothSocketReceiver;
import com.android.bluetoothmusic.utility.Constants;
import com.android.bluetoothmusic.utility.DrawableUtils;
import com.android.bluetoothmusic.utility.EmptyProgressRecyclerView;
import com.android.bluetoothmusic.utility.spinner.SpinnerDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothFragment extends AttachmentViewFragment {

    public static FragmentActivity context;
    private EmptyProgressRecyclerView emptyProgressRecyclerView;

    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_SENDER_CONNECTED = 3;
    public static final int STATE_RECEIVER_CONNECTED = 4;
    public static final int STATE_CONNECTION_FAILED = 5;
    public static final int STATE_MESSAGE_TRANSFER = 6;
    public static final int STATE_MESSAGE_RECEIVED = 7;
    public static final int STATE_DISCONNECTED = 8;

    private static BluetoothAdapter bluetoothAdapter;
    public static BluetoothDevice boundingDevice;
    public DeviceRecyclerViewAdapter deviceRecyclerViewAdapter;
    private ProgressDialog bondingProgressDialog;
    private BluetoothSocketClient bluetoothSocketClient;
    private View layoutContainer;
    private BluetoothServerSocketObserver bluetoothServerSocketObserver;
    private static BluetoothDevice bluetoothDeviceConnected;

    private static BluetoothFragment bluetoothFragment = new BluetoothFragment();

    public static BluetoothFragment instance() {
        return bluetoothFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        context = getActivity();
        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        emptyProgressRecyclerView = (EmptyProgressRecyclerView) view.findViewById(R.id.emptyProgressRecyclerView);
        emptyProgressRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        layoutContainer = getActivity().findViewById(android.R.id.content);

        initializeEvent();
    }

    @Override
    protected void initializeEvent() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        deviceRecyclerViewAdapter = new DeviceRecyclerViewAdapter(bluetoothAdapter, new ListInteractionListener<BluetoothDevice>() {
            @Override
            public void onItemClick(BluetoothDevice device) {
                toast(layoutContainer, "Item clicked : " + "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]");
                if (bluetoothAdapter.getBondedDevices().contains(device)) {
                    String address = device.getAddress();
                    toast(layoutContainer, "This device is already paired! : " + address);
                    try {
                        if (bluetoothAdapter != null) {
                            bluetoothServerSocketObserver = new BluetoothServerSocketObserver(handlerBluetoothMusic, bluetoothAdapter, Constants.uuid);
                            bluetoothServerSocketObserver.start();
                        }
                    } catch (Exception e) {
                        Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                e);
                    }

                } else {
                    toast(layoutContainer, "Device not paired. Pairing.");
                    boolean outcome = pair(device);
                    Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            outcome);

                    // Prints a message to the user.
                    String deviceName = getDeviceName(device);
                    Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            deviceName);
                    if (outcome) {
                        // The pairing has started, shows a progress dialog.
                        toast(layoutContainer, "Pairing with device " + deviceName + "...");
                        bondingProgressDialog = ProgressDialog.show(context, "", "Pairing with device " + deviceName + "...", true, false);
                    } else {
                        toast(layoutContainer, "Error while pairing with device " + deviceName + "!");
                    }
                }
            }

            @Override
            public void startLoading() {
                emptyProgressRecyclerView.startLoading();

                //layoutContainer.getBluetoothImageView().setImageResource(R.drawable.ic_bluetooth_searching_white_24dp);
            }

            @Override
            public void endLoading(boolean partialResults) {

            }

            @Override
            public void endLoadingWithDialog(boolean error, BluetoothDevice element) {
                if (bondingProgressDialog != null && element != null) {
                    String message;
                    String deviceName = getDeviceName(element);

                    // Gets the message to print.
                    if (error) {
                        message = "Failed pairing with device " + deviceName + "!";
                    } else {
                        message = "Successfully paired with device " + deviceName + "!";
                    }

                    // Dismisses the progress dialog and prints a message to the user.
                    bondingProgressDialog.dismiss();
                    toast(layoutContainer, message);

                    // Cleans up state.
                    bondingProgressDialog = null;
                } else {
                    if (bondingProgressDialog != null) {
                        // Dismisses the progress dialog and prints a message to the user.
                        bondingProgressDialog.dismiss();
                        // Cleans up state.
                        bondingProgressDialog = null;
                    }
                }

            }
        });

        bluetooth();

        emptyProgressRecyclerView.setAdapter(deviceRecyclerViewAdapter);

        DashboardFragment.setOnDashboardObserver(new DashboardFragment.OnDashboardObserver() {
            @Override
            public void onBluetooth() {
                /*Message message = Message.obtain();
                message.what = STATE_MESSAGE_TRANSFER;
                handlerBluetoothMusic.sendMessage(message);*/
            }

            @Override
            public void onBluetoothDevice() {
                List<BluetoothDevice> bluetoothBondedDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());

                SpinnerDialog<BluetoothDevice> dialog = new SpinnerDialog<>(context);
                dialog.show();
                dialog.setVisibilitySearch(View.GONE);
                dialog.setAllowButton("Ok");
                dialog.setDismissButton("Cancel");
                dialog.setTitleText("Bluetooth Device");
                dialog.titleSpinnerDialog.setBackground(DrawableUtils.listDrawable(context, 0f));
                dialog.allowButton.setBackground(DrawableUtils.listDrawable(context));
                dialog.dismissButton.setBackground(DrawableUtils.listDrawable(context));
                dialog.allowButton.setTextColor(DrawableUtils.white(context));
                dialog.dismissButton.setTextColor(DrawableUtils.white(context));

                adapterBluetoothDeviceConnector.addItemsObserver(bluetoothBondedDevices);
                dialog.spinnerRecyclerView.setAdapter(adapterBluetoothDeviceConnector);
                adapterBluetoothDeviceConnector.setOnClickObserver(new BluetoothDeviceConnectorObserver() {
                    @Override
                    public void onConnecting(int adapterPosition) throws Exception {
                        BluetoothDevice objectObserver = adapterBluetoothDeviceConnector.getRecord(adapterPosition);
                        Logger.log(LoggerMessage.RECENTLY_PLAYLIST, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                initializePrettyPrintJson(objectObserver),
                                objectObserver.getName(),
                                objectObserver.getAddress()
                        );

                        bluetoothDeviceConnected = objectObserver;

                    }

                });
                dialog.setSpinnerDialogObserver(new SpinnerDialog.SpinnerDialogObserver<BluetoothDevice>() {
                    @Override
                    public void filter(String text) {

                    }

                    @Override
                    public void selectedObserver(BluetoothDevice bluetoothDevice) {

                    }

                    @Override
                    public void selectedAdapterPosition(BluetoothDevice bluetoothDevice, List<BluetoothDevice> bluetoothDevices, int adapterPosition) {

                    }

                    @Override
                    public void success() {
                        if (bluetoothDeviceConnected != null) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    bluetoothDeviceConnected.getName(),
                                    bluetoothDeviceConnected.getAddress()
                            );

                            bluetoothSocketClient = new BluetoothSocketClient(handlerBluetoothMusic, bluetoothDeviceConnected, Constants.uuid);
                            bluetoothSocketClient.start();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Bluetooth device not found!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure() {

                    }
                });

            }
        });

    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {

    }

    public static int getPairingDeviceStatus() {
        if (boundingDevice == null) {
            throw new IllegalStateException("No device currently bounding");
        }
        int bondState = boundingDevice.getBondState();
        // If the new state is not BOND_BONDING, the pairing is finished, cleans up the state.
        if (bondState != BluetoothDevice.BOND_BONDING) {
            boundingDevice = null;
        }
        return bondState;
    }

    public static String getDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null) {
            deviceName = device.getAddress();
        }
        return deviceName;
    }

    public boolean pair(BluetoothDevice device) {
        Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                device.getName());
        // Stops the discovery and then creates the pairing.
        if (bluetoothAdapter.isDiscovering()) {
            toast(layoutContainer, "Bluetooth cancelling discovery.");
            bluetoothAdapter.cancelDiscovery();
        }
        toast(layoutContainer, "Bluetooth bonding with device: " + "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]");
        boolean outcome = device.createBond();
        Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                outcome);

        toast(layoutContainer, "Bounding outcome : " + outcome);

        // If the outcome is true, we are bounding with this device.
        if (outcome) {
            boundingDevice = device;
        }
        return outcome;
    }

    public static Handler handlerBluetoothMusic = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "BLUETOOTH_DEVICE");
            switch (message.what) {
                case STATE_LISTENING:
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            STATE_LISTENING,
                            "Listening");
                    break;
                case STATE_CONNECTING:
                    Snackbar.make(context.findViewById(android.R.id.content), "Connecting", Snackbar.LENGTH_SHORT).show();
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            STATE_CONNECTING,
                            "Connecting");
                    break;
                case STATE_SENDER_CONNECTED:
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                    builderSingle.setIcon(R.drawable.ic_bluetooth_black_24dp);
                    builderSingle.setTitle("Device Connected : " + bluetoothDeviceConnected.getName());

                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderSingle.show();
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            "Sender Connected");
                    break;
                case STATE_RECEIVER_CONNECTED:
                    builderSingle = new AlertDialog.Builder(context);
                    builderSingle.setIcon(R.drawable.ic_bluetooth_black_24dp);
                    builderSingle.setTitle("Device Connected : " + bluetoothAdapter.getName());

                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderSingle.show();
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            "Receiver Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    Snackbar.make(context.findViewById(android.R.id.content), "Connection Failed", Snackbar.LENGTH_SHORT).show();
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            STATE_CONNECTION_FAILED,
                            "Connection Failed");
                    break;
                case STATE_MESSAGE_TRANSFER:
                    if (DashboardObserverActivity.bluetoothSocketReceiver != null) {
                        try {
                            //File f = new File("/storage/emulated/0/Bharat/Hanuman Ji Sab Gunma Rauye Me Bharal Ba(KundanWap.Com).mp3");
                            byte[] buffer = new byte[(int) DashboardObserverActivity.fileBluetoothMusic.length()];
                            FileInputStream fis = new FileInputStream(DashboardObserverActivity.fileBluetoothMusic);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(buffer, 0, buffer.length);
                            DashboardObserverActivity.bluetoothSocketReceiver.write(buffer, 0, buffer.length);

                        } catch (IOException e) {
                            Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                    e);
                        }

                    } else {
                        Snackbar.make(context.findViewById(android.R.id.content), "Please Start Client Server", Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case STATE_MESSAGE_RECEIVED:

                    byte[] bytes = (byte[]) message.obj;
                    String stopBytes = new String(bytes, 0, message.arg1);

                    Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            bytes.length);

                    playMp3(bytes);

                    break;
                case STATE_DISCONNECTED:
                    Snackbar.make(context.findViewById(android.R.id.content), "Disconnected Server", Snackbar.LENGTH_SHORT).show();

                    break;
            }
            return true;
        }
    });

    private static MediaPlayer mediaPlayer = new MediaPlayer();

    private static void playMp3(byte[] mp3SoundByteArray) {
        try {
            File tempMp3 = File.createTempFile("kurchina", "mp3", context.getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            mediaPlayer.reset();

            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    private void bluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            // If another discovery is in progress, cancels it before starting the new one.
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            IntentFilter filter = new IntentFilter();

            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            context.registerReceiver(mReceiver, filter);

            boolean startDiscovery = bluetoothAdapter.startDiscovery();

            if (!startDiscovery) {
                Snackbar.make(layoutContainer, "StartDiscovery returned false. Maybe Bluetooth isn't on?", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(layoutContainer, "Turning on Bluetooth and looking for devices&#8230;", Snackbar.LENGTH_SHORT).show();
            deviceRecyclerViewAdapter.onDeviceDiscoveryStarted();
            bluetoothAdapter.enable();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Snackbar.make(layoutContainer, "Bluetooth successfully enabled, starting discovery", Snackbar.LENGTH_SHORT).show();

                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Snackbar.make(layoutContainer, "StartDiscovery returned false. Maybe Bluetooth isn't on?", Snackbar.LENGTH_SHORT).show();

                //discovery finishes, dismiss progress dialog
                deviceRecyclerViewAdapter.onDeviceDiscoveryEnd();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Snackbar.make(layoutContainer, "Found device " + device.getName(), Snackbar.LENGTH_SHORT).show();

                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        device.getName(),
                        device.getAddress());
                deviceRecyclerViewAdapter.onDeviceDiscovered(device);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int bluetoothState = bluetoothAdapter.getState();
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        // Bluetooth is ON.
                        Snackbar.make(layoutContainer, "Bluetooth successfully enabled, starting discovery", Snackbar.LENGTH_SHORT).show();

                        // If another discovery is in progress, cancels it before starting the new one.
                        if (bluetoothAdapter.isDiscovering()) {
                            bluetoothAdapter.cancelDiscovery();
                        }

                        IntentFilter filter = new IntentFilter();

                        filter.addAction(BluetoothDevice.ACTION_FOUND);
                        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                        context.registerReceiver(mReceiver, filter);

                        bluetoothAdapter.startDiscovery();

                        if (!bluetoothAdapter.startDiscovery()) {
                            Snackbar.make(layoutContainer, "StartDiscovery returned false. Maybe Bluetooth isn't on?", Snackbar.LENGTH_SHORT).show();
                        }

                        break;
                    case BluetoothAdapter.STATE_OFF:
                        // Bluetooth is OFF.
                        Snackbar.make(layoutContainer, "Error while turning Bluetooth on.", Snackbar.LENGTH_SHORT).show();
                        break;
                    default:
                        // Bluetooth is turning ON or OFF. Ignore.
                        break;
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                toast(layoutContainer, "Bluetooth bonding state changed.");
                Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        BluetoothDevice.ACTION_BOND_STATE_CHANGED);

                deviceRecyclerViewAdapter.onDevicePairingEnded();
            }
        }
    };


    GenericRecordsAdapter<BluetoothDevice, BluetoothDeviceConnectorObserver> adapterBluetoothDeviceConnector = new GenericRecordsAdapter<BluetoothDevice, BluetoothDeviceConnectorObserver>() {
        private BluetoothDeviceConnectorObserver observer;
        private int selectedPosition = -1;

        @Override
        public BaseHolder<BluetoothDevice, BluetoothDeviceConnectorObserver> setViewHolder(ViewGroup parent, int typeOfView, BluetoothDeviceConnectorObserver onRecentlyPlaylistObserver) {
            int layout_bluetooth_device = R.layout.layout_bluetooth_device;
            return new BluetoothDeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_bluetooth_device, parent, false));
        }

        @Override
        protected void onBindData(BaseHolder<BluetoothDevice, BluetoothDeviceConnectorObserver> holder, int position, BluetoothDevice objectObserver, List<BluetoothDevice> objectObservers, BluetoothDeviceConnectorObserver observer) {
            this.observer = observer;
        }

        class BluetoothDeviceViewHolder extends BaseHolder<BluetoothDevice, BluetoothDeviceConnectorObserver> {

            private AppCompatTextView titleSongPlaylist;
            private BluetoothDeviceConnectorObserver observer;

            public BluetoothDeviceViewHolder(View itemView) {
                super(itemView);

                titleSongPlaylist = itemView.findViewById(R.id.titleSongPlaylist);
            }

            @Override
            public void onBind(int position, BluetoothDevice objectObserver, List<BluetoothDevice> records, BluetoothDeviceConnectorObserver observer) {
                this.observer = observer;
                String device = objectObserver.getName() + "\n" + objectObserver.getAddress();
                titleSongPlaylist.setText(device);
                Logger.log(LoggerMessage.RECENTLY_PLAYLIST, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        initializePrettyPrintJson(objectObserver),
                        device
                );

                if (selectedPosition == position) {
                    titleSongPlaylist.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSeaGreen));
                } else {
                    titleSongPlaylist.setBackgroundColor(ContextCompat.getColor(context, R.color.colorIndianRed));
                }

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                try {
                    selectedPosition = getAdapterPosition();
                    observer.onConnecting(getAdapterPosition());
                    notifyDataSetChanged();
                } catch (Exception e) {
                    Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            e);
                }
            }
        }

    };

    public interface BluetoothDeviceConnectorObserver {

        void onConnecting(int adapterPosition) throws Exception;
    }

}
