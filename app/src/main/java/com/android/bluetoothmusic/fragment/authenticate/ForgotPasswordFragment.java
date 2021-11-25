package com.android.bluetoothmusic.fragment.authenticate;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.widgets.EditorView;

public class ForgotPasswordFragment extends AttachmentViewFragment {

    private Context context;
    private EditorView emailEditor;
    private TextView submitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        context = getActivity();
        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        emailEditor = (EditorView) bindView(view, R.id.emailEditor);
        submitButton = (TextView) bindView(view, R.id.submitButton);

        initializeClicker(submitButton);
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
            String email = emailEditor.getEditorText();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(context, resourceString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, resourceString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(context, email, Toast.LENGTH_SHORT).show();

        }

    }
}
