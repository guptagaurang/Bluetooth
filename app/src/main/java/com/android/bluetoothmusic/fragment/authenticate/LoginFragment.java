package com.android.bluetoothmusic.fragment.authenticate;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.database.UserDatabaseHelper;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.notification.NotificationObserver;
import com.android.bluetoothmusic.notification.RemoteMessage;
import com.android.bluetoothmusic.widgets.EditorView;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends AttachmentViewFragment {

    private Context context;
    private EditorView emailEditor;
    private EditorView passwordEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_login, container, false);
        context = getActivity();
        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        emailEditor = (EditorView) view.findViewById(R.id.emailEditor);
        passwordEditor = (EditorView) view.findViewById(R.id.passwordEditor);
        initializeClicker(view, R.id.loginButton);
        initializeClicker(view, R.id.signUpButton);
        initializeClicker(view, R.id.forgotPasswordButton);

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
            String email = emailEditor.getEditorText();
            String password = passwordEditor.getEditorText();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(context, resourceString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(context, resourceString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                return;
            }

            UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(context);
            try {
                JSONObject loginObject = userDatabaseHelper.login(email, password);
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
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

                        NavOptions.Builder builder = new NavOptions.Builder();
                        builder.setEnterAnim(R.anim.animation_translate_enter_slide);
                        builder.setExitAnim(R.anim.animation_translate_exit_slide);
                        builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
                        builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);
                        builder.setPopUpTo(R.id.loginFragment, true);

                        NavOptions options = builder.build();
                        Bundle bundle = new Bundle();

                        /**
                         * {@link com.android.bluetoothmusic.fragment.dashboard.DashboardFragment}
                         */
                        NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.dashboardFragment, bundle, options);

                    }
                }
            } catch (JSONException e) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        e);
            }

        }

        if (view.getId() == R.id.forgotPasswordButton) {
            NavOptions.Builder builder = new NavOptions.Builder();
            builder.setEnterAnim(R.anim.animation_translate_enter_slide);
            builder.setExitAnim(R.anim.animation_translate_exit_slide);
            builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
            builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);
            builder.setPopUpTo(R.id.splashScreenFragment, true);

            NavOptions options = builder.build();
            Bundle bundle = new Bundle();

            /**
             * {@link ForgotPasswordFragment}
             */
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.forgotPasswordFragment, bundle, options);
        }

        if (view.getId() == R.id.signUpButton) {
            NavOptions.Builder builder = new NavOptions.Builder();
            builder.setEnterAnim(R.anim.animation_translate_enter_slide);
            builder.setExitAnim(R.anim.animation_translate_exit_slide);
            builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
            builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);

            NavOptions options = builder.build();
            Bundle bundle = new Bundle();

            /**
             * {@link RegisterFragment}
             */
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.registerFragment, bundle, options);
        }
    }


}
