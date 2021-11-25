package com.android.bluetoothmusic.fragment.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class ChangePasswordFragment extends AttachmentViewFragment implements TextWatcher {

    private Context context;
    private TextView submitButton;
    private EditorView yourPasswordEditorView;
    private EditorView newPasswordEditorView;
    private EditorView confirmPasswordEditorView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_change_password, container, false);

        context = getActivity();

        initializeView(inflateView);

        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        yourPasswordEditorView = (EditorView) view.findViewById(R.id.yourPasswordEditorView);
        newPasswordEditorView = (EditorView) view.findViewById(R.id.newPasswordEditorView);
        confirmPasswordEditorView = (EditorView) view.findViewById(R.id.confirmPasswordEditorView);
        submitButton = (TextView) view.findViewById(R.id.submitButton);

        initializeClicker(submitButton);
        confirmPasswordEditorView.getEditText().addTextChangedListener(this);
    }

    @Override
    protected void initializeEvent() {

    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {
        if (view == submitButton) {
            String oldPassword = yourPasswordEditorView.getEditorText();
            String newPassword = newPasswordEditorView.getEditorText();
            String confirmPassword = confirmPasswordEditorView.getEditorText();
            if (TextUtils.isEmpty(oldPassword)) {
                Toast.makeText(context, resourceString(R.string.enter_old_password), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(newPassword)) {
                Toast.makeText(context, resourceString(R.string.enter_new_password), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(context, resourceString(R.string.enter_confirm_password), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!confirmPassword.equals(newPassword)) {
                Toast.makeText(context, resourceString(R.string.please_enter_valid_password), Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(context);
                JSONObject loginObject = userDatabaseHelper.changePassword(VariableConstants.id, oldPassword, confirmPassword);

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
                        yourPasswordEditorView.getEditText().setText("");
                        newPasswordEditorView.getEditText().setText("");
                        confirmPasswordEditorView.getEditText().setText("");
                    }
                }

            } catch (Exception e) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        e);
            }


        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String password = newPasswordEditorView.getEditorText();
        if (s.length() > 0 && password.length() > 0 && !s.toString().equals(password)) {
            confirmPasswordEditorView.setErrorTextView(resourceString(R.string.please_enter_valid_password));
        } else {
            confirmPasswordEditorView.setErrorTextView("");
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
