package com.android.bluetoothmusic.fragment.authenticate;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.database.BluetoothDatabaseHelper;
import com.android.bluetoothmusic.database.UserDatabaseHelper;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.widgets.EditorView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterFragment extends AttachmentViewFragment implements TextWatcher {

    private Context context;
    private EditorView usernameEditor;
    private EditorView emailEditor;
    private EditorView passwordEditor;
    private EditorView confirmPasswordEditor;
    private EditorView phoneNumberEditor;
    private EditorView addressEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_register, container, false);
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
        passwordEditor = (EditorView) view.findViewById(R.id.passwordEditor);
        confirmPasswordEditor = (EditorView) view.findViewById(R.id.confirmPasswordEditor);
        phoneNumberEditor = (EditorView) view.findViewById(R.id.phoneNumberEditor);
        addressEditor = (EditorView) view.findViewById(R.id.addressEditor);
        initializeClicker(view, R.id.signUpButton);
        initializeClicker(view, R.id.loginButton);

        confirmPasswordEditor.getEditText().addTextChangedListener(this);
    }

    @Override
    protected void initializeEvent() {

    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginButton) {
            NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
        }
        if (view.getId() == R.id.signUpButton) {
            String username = usernameEditor.getEditorText();
            String email = emailEditor.getEditorText();
            String password = passwordEditor.getEditorText();
            String confirm_password = confirmPasswordEditor.getEditorText();
            String phoneNumber = TextUtils.isEmpty(phoneNumberEditor.getEditorText()) ? "123" : "123";
            String address = TextUtils.isEmpty(addressEditor.getEditorText()) ? "123" : "123";

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

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(context, resourceString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(confirm_password)) {
                Toast.makeText(context, resourceString(R.string.enter_confirm_password), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!confirm_password.equals(password)) {
                Toast.makeText(context, resourceString(R.string.password_is_not_match), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(context, resourceString(R.string.enter_phone_number), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(address)) {
                Toast.makeText(context, resourceString(R.string.enter_address), Toast.LENGTH_SHORT).show();
                return;
            }

            String createDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH).format(new Date());

            try {
                UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(context);
                JSONObject createSuccessful = userDatabaseHelper.registration(username, email, password, phoneNumber, address, "0", createDate, createDate);
                Toast.makeText(context, createSuccessful.getString("message"), Toast.LENGTH_SHORT).show();
                if (createSuccessful.getBoolean("success")) {
                    NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
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
        String password = passwordEditor.getEditorText();
        if (s.length() > 0 && password.length() > 0 && !s.toString().equals(password)) {
            confirmPasswordEditor.setErrorTextView(resourceString(R.string.please_enter_valid_password));
        } else {
            confirmPasswordEditor.setErrorTextView("");
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
