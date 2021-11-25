package com.android.bluetoothmusic.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EllipsizeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public EllipsizeTextView(@NonNull Context context) {
        super(context);
    }

    public EllipsizeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EllipsizeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setSingleLine();
        setClickable(false);
        setFocusable(false);
        setSelected(true);
    }
}
