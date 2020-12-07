package com.example.testeappzoom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MainActivity extends AppCompatActivity {
    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        @Override
        public void onZoomSDKLoginResult(long result) {
            if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(MainActivity.this);
            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) {
        }

        @Override
        public void onZoomIdentityExpired() {
        }

        @Override
        public void onZoomAuthIdentityExpired() {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button joinButton = findViewById(R.id.join_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJoinMeetingDialog();
            }
        });
        Button loginJoinButton = findViewById(R.id.login_button);
        loginJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ZoomSDK.getInstance().isLoggedIn()) {
                    startMeeting(MainActivity.this);
                } else {
                    createLoginDialog();
                }
            }
        });
        initializeSdk(this);
    }

    private void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: For the purpose of this demo app, we are storing the credentials in the client app itself. However, you should not use hard-coded values for your key/secret in your app in production.
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "shXaYisE6XXLcTxcjUMCE5b5RhTD44KVnGPx"; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = "BB7fi6TnRr8ncI2emBsFLLj6uLY7rtTwbLd9"; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "zoom.us";
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
            }

            @Override
            public void onZoomAuthIdentityExpired() {
            }
        };
        sdk.initialize(context, listener, params);
    }

    private void joinMeeting(Context context, String meetingNumber, String password) {

        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        // Check if the zoom SDK is initialized
        if (!zoomSDK.isInitialized()) {
            Toast.makeText(this, "Service has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }
        MeetingService meetingService = zoomSDK.getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = ""; // TODO: Enter your name
        params.meetingNo = meetingNumber;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);
    }

    private void login(String username, String password) {
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {

            // 2. After request is executed, listen for the authentication result prior to starting a meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener);
        }
    }

    // 3. Write the startMeeting function
    private void startMeeting(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);
        }
    }

    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(this).setView(R.layout.fragment_join).setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog dialog = (AlertDialog) dialogInterface;
                TextInputEditText numberInput = dialog.findViewById(R.id.meeting_no_input);
                TextInputEditText passwordInput = dialog.findViewById(R.id.password_input);
                if (numberInput != null && numberInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                    String meetingNumber = numberInput.getText().toString();
                    String password = passwordInput.getText().toString();
                    if (meetingNumber.trim().length() > 0 && password.trim().length() > 0) {
                        joinMeeting(MainActivity.this, meetingNumber, password);
                    }
                }
            }
        }).show();
    }

    private void createLoginDialog() {
        new AlertDialog.Builder(this).setView(R.layout.fragment_login_join).setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog dialog = (AlertDialog) dialogInterface;
                TextInputEditText emailInput = dialog.findViewById(R.id.email_input);
                TextInputEditText passwordInput = dialog.findViewById(R.id.pw_input);
                if (emailInput != null && emailInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                    String email = emailInput.getText().toString();
                    String password = passwordInput.getText().toString();
                    if (email.trim().length() > 0 && password.trim().length() > 0) {
                        login(email, password);
                    }
                }
            }
        }).show();
    }


}