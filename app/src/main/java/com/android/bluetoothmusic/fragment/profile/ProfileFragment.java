package com.android.bluetoothmusic.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.fragment.dashboard.DashboardFragment;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.legacy.observer.BluetoothClickObserver;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.ObjectClickObserver;
import com.android.bluetoothmusic.notification.NotificationObserver;
import com.android.bluetoothmusic.notification.RemoteMessage;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.android.bluetoothmusic.widgets.TextViewer;

import org.json.JSONException;

public class ProfileFragment extends AttachmentViewFragment {

    private Context context;
    private TextView logoutButton;
    private TextView updateProfileButton;
    private BluetoothClickObserver<ObjectClickObserver> bluetoothClickObserver;
    private View inflateView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflateView == null) {
            inflateView = inflater.inflate(R.layout.fragment_profile, container, false);
        }
        context = getActivity();
        return inflateView;
    }

    @Override
    public void onResume() {
        super.onResume();

        initializeView(inflateView);
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        TextViewer usernameTextViewer = (TextViewer) view.findViewById(R.id.usernameTextViewer);
        TextViewer emailTextViewer = (TextViewer) view.findViewById(R.id.emailTextViewer);
        logoutButton = (TextView) view.findViewById(R.id.logoutButton);
        updateProfileButton = (TextView) view.findViewById(R.id.updateProfileButton);

        usernameTextViewer.getTextView().setText(VariableConstants.username);
        emailTextViewer.getTextView().setText(VariableConstants.email);

        initializeClicker(logoutButton);
        initializeClicker(updateProfileButton);

    }

    @Override
    protected void initializeEvent() {

    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {
        if (view == logoutButton) {
            try {
                RemoteMessage remoteMessage = new RemoteMessage();
                remoteMessage.setTitle("Hii, " + VariableConstants.username);
                remoteMessage.setBody("Logout Successful");

                NotificationObserver.showNotification(context, remoteMessage);
            } catch (JSONException exception) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        exception);
            } finally {
                logout();
            }
        }

        if (view == updateProfileButton) {
            NavOptions.Builder builder = new NavOptions.Builder();
            builder.setEnterAnim(R.anim.animation_translate_enter_slide);
            builder.setExitAnim(R.anim.animation_translate_exit_slide);
            builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
            builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);

            NavOptions options = builder.build();
            Bundle bundle = new Bundle();
            if (navigatorHomeId != R.id.updateProfileFragment) {
                if (DashboardFragment.homeNavController != null) {
                    /**
                     * {@link com.android.bluetoothmusic.fragment.profile.UpdateProfileFragment}
                     */
                    DashboardFragment.homeNavController.navigate(R.id.updateProfileFragment, bundle, options);
                }
            }
        }
    }

    public void setProfileObserver(BluetoothClickObserver<ObjectClickObserver> bluetoothClickObserver) {
        this.bluetoothClickObserver = bluetoothClickObserver;
    }
}
