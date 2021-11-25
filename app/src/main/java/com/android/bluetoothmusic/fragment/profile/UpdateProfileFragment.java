package com.android.bluetoothmusic.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.database.UserDatabaseHelper;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.notification.NotificationObserver;
import com.android.bluetoothmusic.notification.RemoteMessage;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.android.bluetoothmusic.widgets.EditorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateProfileFragment extends AttachmentViewFragment {

    private Context context;
    private EditorView emailEditor;
    private EditorView usernameEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_update_profile, container, false);
        context = getActivity();
        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        usernameEditor = (EditorView) view.findViewById(R.id.usernameEditor);
        emailEditor = (EditorView) view.findViewById(R.id.emailEditor);

        usernameEditor.getEditText().setText(VariableConstants.username);
        emailEditor.getEditText().setText(VariableConstants.email);

        initializeClicker(view, R.id.updateProfileButton);

    }

    @Override
    protected void initializeEvent() {

    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.updateProfileButton) {
            String email = emailEditor.getEditorText();
            String username = usernameEditor.getEditorText();
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(context, resourceString(R.string.enter_username), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(context, resourceString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, resourceString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                return;
            }

            UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(context);
            try {
                String createDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH).format(new Date());

                JSONObject loginObject = userDatabaseHelper.updateProfile(VariableConstants.id, username, email, "0", createDate);
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        VariableConstants.id,
                        initializePrettyPrintJson(loginObject));

                Toast.makeText(context, loginObject.getString("message"), Toast.LENGTH_SHORT).show();

                if (loginObject.getBoolean("success")) {
                    try {
                        RemoteMessage remoteMessage = new RemoteMessage();
                        remoteMessage.setTitle("Hii, " + loginObject.getString("username"));
                        remoteMessage.setBody(loginObject.getString("message"));

                        NotificationObserver.showNotification(context, remoteMessage);
                    } catch (JSONException exception) {
                        Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                exception);
                    } finally {
                        loginObject(loginObject);
                    }
                }
            } catch (Exception e) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        e);
            }

        }

    }
}
