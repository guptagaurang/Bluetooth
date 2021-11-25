package com.android.bluetoothmusic.legacy.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.database.PlaylistDatabaseHelper;
import com.android.bluetoothmusic.fragment.authenticate.LoginFragment;
import com.android.bluetoothmusic.models.BundleObserver;
import com.android.bluetoothmusic.session.SessionManager;
import com.android.bluetoothmusic.session.SessionPreferences;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ObserverFragment extends Fragment {

    private SessionManager sessionManager;
    private static FragmentActivity context;
    public boolean isWorking = false;
    public NavController dashboardNavController;
    public int navigatorHomeId;
    public PlaylistDatabaseHelper playlistDatabaseHelper;
    public String bundlePlaylistId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        playlistDatabaseHelper = new PlaylistDatabaseHelper(context);
        sessionManager = SessionManager.getInstance(context);
        VariableConstants.id = sessionManager.numberKey(SessionPreferences.USER_ID.name());
        VariableConstants.username = sessionManager.stringKey(SessionPreferences.USERNAME.name());
        VariableConstants.email = sessionManager.stringKey(SessionPreferences.EMAIL.name());

        bundlePlaylistId = BundleObserver.PLAYLIST_ID.name();
    }

    protected View bindView(View view, int id) {
        return view.findViewById(id);
    }

    protected String resourceString(int id) {
        return getString(id);
    }

    protected void toast(View layoutContainer, String message){
        Snackbar.make(layoutContainer, message, Snackbar.LENGTH_SHORT).show();

    }

    public String initializePrettyPrintJson(Object object) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }
    public String initializePrettyPrint(Object object) {
        String jsonString = new Gson().toJson(object);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        String prettyJson = gson.toJson(jsonObject);

        return prettyJson;
    }

    public void loginObject(JSONObject loginObject) throws JSONException {
        sessionManager.save(SessionPreferences.LOGIN.name(), true);
        sessionManager.save(SessionPreferences.USER_ID.name(), loginObject.getInt("user_id"));
        sessionManager.save(SessionPreferences.USERNAME.name(), loginObject.getString("username"));
        sessionManager.save(SessionPreferences.EMAIL.name(), loginObject.getString("email"));
        sessionManager.save(SessionPreferences.PHONE_NUMBER.name(), loginObject.getString("phone_number"));

        VariableConstants.username = sessionManager.stringKey(SessionPreferences.USERNAME.name());
        VariableConstants.email = sessionManager.stringKey(SessionPreferences.EMAIL.name());

    }

    public boolean isLogin() {
        boolean isLogin = sessionManager.booleanKey(SessionPreferences.LOGIN.name());
        return isLogin;
    }

    public void logout() {
        sessionManager.clear();

        NavOptions.Builder builder = new NavOptions.Builder();
        builder.setEnterAnim(R.anim.animation_translate_enter_slide);
        builder.setExitAnim(R.anim.animation_translate_exit_slide);
        builder.setPopEnterAnim(R.anim.animation_translate_pop_enter_slide);
        builder.setPopExitAnim(R.anim.animation_translate_pop_exit_slide);
        builder.setPopUpTo(R.id.dashboardFragment, true);

        NavOptions options = builder.build();
        Bundle bundle = new Bundle();

        /**
         * {@link LoginFragment}
         */
        NavHostFragment.findNavController(ObserverFragment.this).navigate(R.id.loginFragment, bundle, options);
    }
}
