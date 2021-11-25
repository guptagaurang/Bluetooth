package com.android.bluetoothmusic.adapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.activity.DashboardObserverActivity;
import com.android.bluetoothmusic.fragment.bluetooth.BluetoothFragment;
import com.android.bluetoothmusic.legacy.observer.ListInteractionListener;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    /**
     * The devices shown in this {@link RecyclerView}.
     */
    private final List<BluetoothDevice> devices;

    /**
     * Callback for handling interaction events.
     */
    private final ListInteractionListener<BluetoothDevice> listener;

    private BluetoothAdapter bluetoothAdapter;

    public DeviceRecyclerViewAdapter(BluetoothAdapter bluetoothAdapter, ListInteractionListener<BluetoothDevice> listener) {
        this.devices = new ArrayList<>();
        this.bluetoothAdapter = bluetoothAdapter;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat, parent, false));
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = devices.get(position);

        Drawable drawable = ContextCompat.getDrawable(holder.context, getDeviceIcon(devices.get(position)));
        holder.imageSearchBluetooth.setColorFilter(ContextCompat.getColor(holder.context, R.color.teal_700), PorterDuff.Mode.MULTIPLY);
        holder.imageSearchBluetooth.setImageDrawable(drawable);

        holder.deviceNameChatBluetooth.setText(devices.get(position).getName());
        holder.deviceAddressChatBluetooth.setText(devices.get(position).getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onItemClick(holder.mItem);
                }
            }
        });
    }

    /**
     * Returns the icon shown on the left of the device inside the list.
     *
     * @param device the device for the icon to get.
     * @return a resource drawable id for the device icon.
     */
    private int getDeviceIcon(BluetoothDevice device) {
        if (bluetoothAdapter.getBondedDevices().contains(device)) {
            return R.drawable.ic_bluetooth_connected_black_24dp;
        } else {
            return R.drawable.ic_bluetooth_searching_white_24dp;
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void onDeviceDiscovered(BluetoothDevice device) {
        listener.endLoading(true);
        devices.add(device);
        //shows the list of devices
        HashSet<BluetoothDevice> bluetoothDevices = new HashSet<>(devices);
        devices.clear();
        devices.addAll(bluetoothDevices);

        notifyDataSetChanged();
    }
    //listens the discovery of the devices nearby
    public void onDeviceDiscoveryStarted() {
        cleanView();
        listener.startLoading();
    }

    /**
     * Cleans the view.
     */
    public void cleanView() {
        devices.clear();
        notifyDataSetChanged();
    }

    public void onDeviceDiscoveryEnd() {
        listener.endLoading(false);
    }

    public void onDevicePairingEnded() {
        if (BluetoothFragment.boundingDevice != null) {
            Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                    "Pairing End....");

            switch (BluetoothFragment.getPairingDeviceStatus()) {
                case BluetoothDevice.BOND_BONDING:
                    Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            "BOND_BONDING");
                    // Still pairing, do nothing.
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            "BOND_BONDED");
                    // Successfully paired.
                    listener.endLoadingWithDialog(false, BluetoothFragment.boundingDevice);

                    // Updates the icon for this element.
                    notifyDataSetChanged();
                    break;
                case BluetoothDevice.BOND_NONE:
                    Logger.log(LoggerMessage.BLUETOOTH_DEVICE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                            "BOND_NONE");

                    // Failed pairing.
                    listener.endLoadingWithDialog(true, BluetoothFragment.boundingDevice);
                    break;
            }
        }
    }

    /**
     * ViewHolder for a BluetoothDevice.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The icon of the device.
         */
        private final ImageView imageSearchBluetooth;

        /**
         * The name of the device.
         */
        private final TextView deviceNameChatBluetooth;

        /**
         * The MAC address of the BluetoothDevice.
         */
        private final TextView deviceAddressChatBluetooth;

        /**
         * The item of this ViewHolder.
         */
        private BluetoothDevice mItem;
        private Context context;

        /**
         * Instantiates a new ViewHolder.
         *
         * @param itemView the inflated view of this ViewHolder.
         */
        public ViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();

            imageSearchBluetooth = (ImageView) itemView.findViewById(R.id.imageSearchBluetooth);
            deviceNameChatBluetooth = (TextView) itemView.findViewById(R.id.deviceNameChatBluetooth);
            deviceAddressChatBluetooth = (TextView) itemView.findViewById(R.id.deviceAddressChatBluetooth);


        }
    }
}
