package com.android.bluetoothmusic.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.bluetoothmusic.R;

public class TwoHorizontalTextView extends LinearLayout {

    private boolean showSecondText;
    private TextView secondTextView;
    private String textFirstText;
    private String textSecondText;
    private int maxLengthFirstText;
    private int maxLengthSecondText;
    private int textSizeFirstText;
    private int textSizeSecondText;
    private TextView firstTextView;
    private int textColorFirstText;
    private int textColorSecondText;

    public TwoHorizontalTextView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public TwoHorizontalTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TwoHorizontalTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TwoHorizontalTextView, 0, 0);

        try {

            setOrientation(HORIZONTAL);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            showSecondText = a.getBoolean(R.styleable.TwoHorizontalTextView_showSecondText, false);
            textFirstText = a.getString(R.styleable.TwoHorizontalTextView_textFirstText);
            textSecondText = a.getString(R.styleable.TwoHorizontalTextView_textSecondText);
            maxLengthFirstText = a.getInteger(R.styleable.TwoHorizontalTextView_maxLengthFirstText, 0);
            maxLengthSecondText = a.getInteger(R.styleable.TwoHorizontalTextView_maxLengthSecondText, 0);
            textSizeFirstText = a.getDimensionPixelSize(R.styleable.TwoHorizontalTextView_textSizeFirstText, 0);
            textSizeSecondText = a.getDimensionPixelSize(R.styleable.TwoHorizontalTextView_textSizeSecondText, 0);
            textColorFirstText = a.getColor(R.styleable.TwoHorizontalTextView_textColorFirstText, 0);
            textColorSecondText = a.getColor(R.styleable.TwoHorizontalTextView_textColorSecondText, 0);

            firstTextView = new TextView(context);
            firstTextView.setSingleLine();
            firstTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
            firstTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTransparent));
            firstTextView.setTextColor(textColorFirstText);
            firstTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeFirstText);
            firstTextView.setText(textFirstText);
            firstTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthFirstText)});

            firstTextView.setLayoutParams(layoutParams());

            secondTextView = new TextView(context);
            secondTextView.setText(textSecondText);
            secondTextView.setGravity(Gravity.END);
            secondTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTransparent));
            secondTextView.setSingleLine();
            secondTextView.setTextColor(textColorSecondText);
            secondTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSecondText);
            secondTextView.setLayoutParams(layoutParams());

            addView(firstTextView);
            addView(secondTextView);

        } finally {
            a.recycle();
        }

    }

    private ViewGroup.LayoutParams layoutParams() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        layoutParams.weight = 1;
        return layoutParams;
    }

    public TextView getFirstTextView() {
        return firstTextView;
    }

    public TextView getSecondTextView() {
        return secondTextView;
    }
}
