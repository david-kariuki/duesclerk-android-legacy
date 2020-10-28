package custom.custom_views.dialog_fragments.bottom_sheets;

import android.annotation.SuppressLint;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

@SuppressWarnings("rawtypes")
@SuppressLint("ValidFragment")
public class LogoutFragment extends BottomSheetDialogFragment {

    /*private final Context mContext;
    private final SQLiteDB database;
    private final SessionManager sessionManager;
    private final String profilePictureUrl;
    private Interface_MainActivity interfaceMainActivity;
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    public LogoutFragment(Context mContext, String profilePictureUrl) {
        this.mContext = mContext; // Get context
        this.sessionManager = new SessionManager(mContext); // Initialize SessionManager object
        this.database = new SQLiteDB(mContext); // Initialize database
        this.profilePictureUrl = profilePictureUrl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog)
                super.onCreateDialog(savedInstanceState);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_logout, null);
        TextView tvCancel = contentView.findViewById(R.id.textBSLogout_Cancel);
        TextView tvLogout = contentView.findViewById(R.id.textBSLogout_Logout);
        TextView tvLogoutMessage = contentView.findViewById(R.id.textBSLogout_Message);
        CircleImageView imageProfilePicture =
                contentView.findViewById(R.id.imageBSLogout_ProfilePicture);

        // Load profile picture
        ViewsUtils.loadImageView(mContext, profilePictureUrl, imageProfilePicture);

        // Check for user information
        if (!DataUtils.isEmptyArrayList(database.getUserAccountInfo())) {
            // Set logout message
            tvLogoutMessage.setText(DataUtils.getStringResource(mContext,
                    R.string.msg_logout, database.getUserAccountInfo().get(0).getFirstName()));
        }
        // Dismiss dialog
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvLogout.setOnClickListener(v -> {
            // Check if user is logged in
            if (sessionManager.isLoggedIn()) {
                sessionManager.setLogin(false); // Delete session

                // Check for user information
                if (!DataUtils.isEmptyArrayList(database.getUserAccountInfo())) {
                    // Delete user details from SQLite database
                    if (database.deleteUserAccountInfo((database.getUserAccountInfo().get(0)
                            .getUserId()))) {
                        // Set MainActivity notification count
                        this.interfaceMainActivity.setAlertsCount(0);
                    }
                }
            }

            dialog.dismiss(); // Dismiss dialog
        });

        // Set BottomSheet callback
        this.bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
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

        // Set Custom View To Dialog
        dialog.setContentView(contentView);

        // Set BottomSheet behaviour
        this.bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());

        ((View) contentView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        return dialog;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.interfaceMainActivity = (Interface_MainActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        this.bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
    }*/
}
