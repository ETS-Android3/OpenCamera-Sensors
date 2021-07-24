package net.sourceforge.opencamera.ui;

import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.MyDebug;
import net.sourceforge.opencamera.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;

import com.googleresearch.capturesync.SoftwareSyncController;

/**
 * This defines the UI for the "recSync" button, that provides quick access to a
 * range of options.
 *
 * Requires {@link SoftwareSyncController} to be initialized (i.e. RecSync to be started)
 */

public class RecSyncView extends LinearLayout {
    private static final String TAG = "RecSyncView";
    public static final float ALPHA_BUTTON_SELECTED = 1.0f;
    public static final float ALPHA_BUTTON = 0.6f; // 0.4f tends to be hard to see in bright light

    private static final float title_text_size_dip = 17.0f;
    private static final float standard_text_size_dip = 16.0f;

    private final float scale = getResources().getDisplayMetrics().density;
    private int total_width_dp;

    public RecSyncView(Context context) {
        super(context);

        if (MyDebug.LOG) {
            Log.d(TAG, "new RecSyncView: " + this);
        }

        this.setOrientation(LinearLayout.VERTICAL);

        final MainActivity mainActivity = (MainActivity) this.getContext();
        final SoftwareSyncController softwareSyncController = mainActivity.getApplicationInterface().getSoftwareSyncController();

        if (softwareSyncController == null) {
            throw new IllegalStateException("SoftwareSyncController is not initialized");
        }
        if (!softwareSyncController.isLeader()) {
            throw new IllegalStateException("RecSyncView should only be shown to a leader");
        }

        boolean small_screen = false;
        total_width_dp = 280;
        int max_width_dp = mainActivity.getMainUI().getMaxHeightDp(false);
        if (total_width_dp > max_width_dp) {
            total_width_dp = max_width_dp;
            small_screen = true;
        }
        if (MyDebug.LOG) {
            Log.d(TAG, "max_width_dp: " + max_width_dp);
            Log.d(TAG, "total_width_dp: " + total_width_dp);
            Log.d(TAG, "small_screen: " + small_screen);
        }

        // button sync settings
        addButton(R.string.sync_settings_unlocked, view -> {
            if (MyDebug.LOG) {
                Log.d(TAG, "clicked to buttonSyncSettings");
            }

            mainActivity.clickedSyncSettings();

            ((Button) view).setText(softwareSyncController.isSettingsLocked() ? R.string.sync_settings_locked : R.string.sync_settings_unlocked);
        });

        // button sync devices
        addButton(R.string.sync_devices, view -> {
            if (MyDebug.LOG) {
                Log.d(TAG, "clicked to buttonSyncDevices");
            }

            mainActivity.clickedSyncDevices();
        });
    }

    public int getTotalWidth() {
        return (int) (total_width_dp * scale + 0.5f); // convert dps to pixels;
    }

    private void addCheckbox(MainActivity context, @StringRes int text, boolean isChecked, CompoundButton.OnCheckedChangeListener listener) {
        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(getResources().getString(text));
        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, standard_text_size_dip);
        checkBox.setTextColor(Color.WHITE);

        {
            // align the checkbox a bit better
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );
            final int left_padding = (int) (10 * scale + 0.5f); // convert dps to pixels
            final int right_padding = (int) (20 * scale + 0.5f); // convert dps to pixels
            params.setMargins(left_padding, 0, right_padding, 0);
            checkBox.setLayoutParams(params);
        }

        if (isChecked) {
            checkBox.setChecked(isChecked);
        }

        checkBox.setOnCheckedChangeListener(listener);

        this.addView(checkBox);
    }

    private void addButton(@StringRes int text, View.OnClickListener listener) {
        final Button buttonSyncDevices = new Button(this.getContext());
        buttonSyncDevices.setBackgroundColor(Color.TRANSPARENT);
        buttonSyncDevices.setText(text);
        buttonSyncDevices.setAllCaps(false);
        buttonSyncDevices.setTextSize(TypedValue.COMPLEX_UNIT_DIP, title_text_size_dip);
        this.addView(buttonSyncDevices);

        buttonSyncDevices.setOnClickListener(listener);
    }
}