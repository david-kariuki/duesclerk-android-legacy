package com.duesclerk.custom.custom_views.dialog_fragments.bottom_sheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.duesclerk.R;
import com.duesclerk.activities.EmailVerificationActivity;
import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.storage_adapters.UserDatabase;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

@SuppressWarnings("rawtypes")
@SuppressLint("ValidFragment")
public class EmailNotVerifiedFragment extends BottomSheetDialogFragment {

    private final Context mContext;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private String usersName;
    private Animation animSwivel;

    public EmailNotVerifiedFragment(Context mContext) {
        this.mContext = mContext; // get context
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog)
                super.onCreateDialog(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_email_not_verified,
                null);

        TextView textGreetings = contentView.findViewById(R.id.textBSEmailNotVerified_Greetings);
        TextView textMessage = contentView.findViewById(R.id.textBSEmailNotVerified_Message);
        ImageView imageDismiss = contentView.findViewById(R.id.imageViewBSClose);
        ImageView imageArm = contentView.findViewById(R.id.ivBSEmailNotVerified_hand);
        LinearLayout llVerifyEmailAddress =
                contentView.findViewById(R.id.llBSEmailNotVerified_Verify);

        UserDatabase database = new UserDatabase(mContext); // Initialize Database  object

        // Initialize Animation
        animSwivel = AnimationUtils.loadAnimation(mContext, R.anim.anim_swivel);
        animSwivel.setRepeatCount(Animation.INFINITE);
        animSwivel.setDuration(900);
        imageArm.startAnimation(animSwivel); // Start animation

        textGreetings.setText(DataUtils.getStringResource(mContext, R.string.string_hello,
                usersName));

        // Set email not verified message
        textMessage.setText(DataUtils.getStringResource(mContext,
                R.string.error_email_address_not_verified_with_placeholder,
                database.getUserAccountInfo(null).get(0).getEmailAddress()));

        imageDismiss.setOnClickListener(v -> {
            dismiss(); // Dismiss
        });

        llVerifyEmailAddress.setOnClickListener(v -> {
            dismiss(); // Dismiss

            // Launch activity for email verification
            startActivity(new Intent(getActivity(), EmailVerificationActivity.class));
        });

        // Set BottomSheet callback
        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) bottomSheetBehavior
                        .setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE); // Remove window title

        // Set transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set com.duesclerk.custom view to dialog
        dialog.setContentView(contentView);

        // Set BottomSheet behaviour
        bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove BottomSheet callback
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback);

        // Cancel swivel animation
        animSwivel.cancel();
    }

    /**
     * Function to set greeting with users firstName
     * @param usersName - Users first Name or business name
     */
    public void setUsersName(String usersName){
        this.usersName = usersName; // Set hello text
    }
}
