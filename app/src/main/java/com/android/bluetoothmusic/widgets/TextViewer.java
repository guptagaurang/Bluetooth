package com.android.bluetoothmusic.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.bluetoothmusic.R;

public class TextViewer extends LinearLayout {

    private ImageView imageView;
    private TextView textView;
    private TextView errorTextView;

    public TextViewer(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public TextViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TextViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EditorView, 0, 0);

        try {
            String hintEditor = a.getString(R.styleable.EditorView_hintEditor);
            int backgroundImageColor = a.getColor(R.styleable.EditorView_backgroundImageColor, Colors.colorTransparent(context));
            int backgroundColorEditor = a.getColor(R.styleable.EditorView_backgroundColorEditor, Colors.colorTransparent(context));
            Drawable image = a.getDrawable(R.styleable.EditorView_image);
            Drawable backgroundEditor = a.getDrawable(R.styleable.EditorView_backgroundEditor);
            int symmetricPadding = a.getDimensionPixelSize(R.styleable.EditorView_symmetricPadding, 0);
            int imageSymmetricPadding = a.getDimensionPixelSize(R.styleable.EditorView_imageSymmetricPadding, 0);
            int editorSymmetricPadding = a.getDimensionPixelSize(R.styleable.EditorView_editorSymmetricPadding, 0);

            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            imageView.setBackgroundColor(backgroundImageColor);
            if (image != null) {
                imageView.setImageDrawable(image);
            } else {
                imageView.setImageBitmap(bitmap);
            }
            imageView.setPadding(imageSymmetricPadding, imageSymmetricPadding, imageSymmetricPadding, imageSymmetricPadding);
            imageView.setLayoutParams(layoutParams(0, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

            textView = new TextView(context);
            textView.setHint(hintEditor);
            textView.setSingleLine();
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setBackgroundColor(Colors.colorTransparent(context));
            textView.setPadding(editorSymmetricPadding, editorSymmetricPadding, editorSymmetricPadding, editorSymmetricPadding);
            textView.setLayoutParams(layoutParams());

            //setBackground(ContextCompat.getDrawable(context, R.drawable.ic_border));
            setBackgroundColor(backgroundColorEditor);
            setOrientation(LinearLayout.VERTICAL);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            setPadding(symmetricPadding, symmetricPadding, symmetricPadding, symmetricPadding);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setBackground(backgroundEditor);
            linearLayout.setOrientation(HORIZONTAL);
            linearLayout.setPadding(symmetricPadding, symmetricPadding, symmetricPadding, symmetricPadding);
            linearLayout.setLayoutParams(layoutParams(1, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);

            errorTextView = new TextView(context);
            errorTextView.setVisibility(GONE);
            addView(linearLayout);
            addView(errorTextView);

        } finally {
            a.recycle();
        }

    }

    public TextView getTextView() {
        return textView;
    }

    public String getEditorText() {
        return textView.getText().toString();
    }

    public TextView getErrorTextView() {
        return errorTextView;
    }

    public void setErrorTextView(String error) {
        this.errorTextView.setVisibility(TextUtils.isEmpty(error) ? GONE : VISIBLE);
        this.errorTextView.setText(error);
        this.errorTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorDarkRed));
        errorTextView.setTypeface(null, Typeface.BOLD);
    }

    private ViewGroup.LayoutParams layoutParams(int weight, int width, int height) {
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.setMargins(0, 0, 0, 0);
        layoutParams.weight = weight;
        return layoutParams;
    }

    private ViewGroup.LayoutParams layoutParams() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        layoutParams.weight = 1;
        return layoutParams;
    }
}
