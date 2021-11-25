package com.android.bluetoothmusic.fragment.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.android.bluetoothmusic.R;
import com.android.bluetoothmusic.database.PlaylistDatabaseHelper;
import com.android.bluetoothmusic.legacy.fragment.AttachmentViewFragment;
import com.android.bluetoothmusic.loggers.Logger;
import com.android.bluetoothmusic.loggers.LoggerMessage;
import com.android.bluetoothmusic.models.ObjectObserver;
import com.android.bluetoothmusic.notification.NotificationObserver;
import com.android.bluetoothmusic.notification.RemoteMessage;
import com.android.bluetoothmusic.utility.ImageFilePath;
import com.android.bluetoothmusic.utility.VariableConstants;
import com.android.bluetoothmusic.widgets.EditorView;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdatePlaylistFragment extends AttachmentViewFragment {

    private static final int PICK_IMAGE_REQUEST = 2001;
    private Context context;
    private EditorView createPlaylistName;
    private TextView createPlaylistButton;
    private TextView cancelButton;
    private AppCompatImageView logoImageView;
    private String baseImage;
    private String playlistId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_update_playlist, container, false);
        context = getActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            playlistId = bundle.getString(bundlePlaylistId);
        }

        initializeView(inflateView);
        return inflateView;
    }

    @Override
    protected void initializeSession() {

    }

    @Override
    protected void initializeView(View view) {
        logoImageView = (AppCompatImageView) bindView(view, R.id.logoImageView);
        createPlaylistName = (EditorView) bindView(view, R.id.createPlaylistName);
        createPlaylistButton = (TextView) bindView(view, R.id.createPlaylistButton);
        cancelButton = (TextView) bindView(view, R.id.cancelButton);

        ObjectObserver objectObserver = playlistDatabaseHelper.showPlaylistById(VariableConstants.id, playlistId);
        Logger.log(LoggerMessage.NONE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                initializePrettyPrintJson(objectObserver));

        createPlaylistName.getEditText().setText(objectObserver.getSong().getPlaylistName());

        baseImage=objectObserver.getSong().getPlaylistImage();
        byte[] decodedString = Base64.decode(baseImage, Base64.DEFAULT);
        Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Glide.with(context).load(decodedString).into(logoImageView);

        initializeClicker(createPlaylistButton);
        initializeClicker(logoImageView);
        initializeClicker(cancelButton);

    }

    @Override
    protected void initializeEvent() {


    }

    @Override
    protected void toolbar() {

    }

    @Override
    public void onClick(View view) {
        if (view == createPlaylistButton) {
            String playlistName = createPlaylistName.getEditorText();

            if (TextUtils.isEmpty(playlistName)) {
                Toast.makeText(context, context.getString(R.string.enter_playlist_name), Toast.LENGTH_SHORT).show();
                return;
            }

            if (baseImage == null) {
                Toast.makeText(context, context.getString(R.string.select_image), Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String createDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH).format(new Date());
                String image = "IMG_" + Calendar.getInstance().getTime() + ".png";

                PlaylistDatabaseHelper playlistDatabaseHelper = new PlaylistDatabaseHelper(context);
                ObjectObserver playlistsObject = playlistDatabaseHelper.updatePlaylist(VariableConstants.id, playlistId, playlistName, baseImage, "0", createDate);

                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        playlistsObject.getMessage());

                Toast.makeText(context, playlistsObject.getMessage(), Toast.LENGTH_SHORT).show();

                if (playlistsObject.isSuccess()) {
                    try {
                        RemoteMessage remoteMessage = new RemoteMessage();
                        remoteMessage.setTitle("Hii, " + playlistsObject.getSongs().get(0).getPlaylistName());
                        remoteMessage.setBody(playlistsObject.getMessage());

                        NotificationObserver.showNotification(context, remoteMessage);
                    } catch (JSONException exception) {
                        Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                                exception);
                    } finally {
                        DashboardFragment.homeNavController.popBackStack();
                    }
                }
            } catch (Exception e) {
                Logger.log(LoggerMessage.THROWABLE, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        e);
            }

        }

        if (view == logoImageView) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }

        if (view == cancelButton) {
            DashboardFragment.homeNavController.popBackStack();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {

                String picturePath = ImageFilePath.getPath(context, selectedImage);
                Logger.logger(LoggerMessage.RECENTLY_PLAYLIST, true, Logger.getThread(Thread.currentThread().getStackTrace()[2]),
                        picturePath
                );

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();
                baseImage = Base64.encodeToString(bytes, 0);

                Glide.with(context).load(picturePath).into(logoImageView);
            }
        }

    }
}
