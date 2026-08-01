package com.techno.aiproject.activities.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.techno.aiproject.R;
import com.techno.aiproject.activities.HomeActivity;
import com.techno.aiproject.utils.CheckConnection;
import com.techno.aiproject.utils.PrefManager;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, password;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView tvRegister, displayMsg, tvForgotPass;
    private PrefManager prefManager;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        loginButton = findViewById(R.id.loginbutton);
        progressBar = findViewById(R.id.progressBarPass);
        tvRegister = findViewById(R.id.register);
        displayMsg = findViewById(R.id.displayMessage);
        tvForgotPass = findViewById(R.id.frogPass);

        progressBar.setVisibility(View.GONE);
        displayMsg.setVisibility(View.GONE);

        prefManager = new PrefManager(this);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode(java.util.Locale.getDefault().getLanguage());

        if (prefManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                displayMsg.setText("");
                displayMsg.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        loginButton.setOnClickListener(view -> {
            if (!CheckConnection.isConnected(LoginActivity.this)) {
                progressBar.setVisibility(View.GONE);
                displayMsg.setVisibility(View.VISIBLE);
                displayMsg.setText("Please connect to the internet.");
                displayMsg.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            String email = emailInput.getText().toString().trim();
            String pass = password.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                progressBar.setVisibility(View.GONE);
                displayMsg.setVisibility(View.VISIBLE);
                displayMsg.setText("Email and password are required.");
                displayMsg.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                progressBar.setVisibility(View.GONE);
                displayMsg.setVisibility(View.VISIBLE);
                displayMsg.setText("Please enter a valid email address.");
                displayMsg.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            requestLogin(email, pass);
        });

        tvRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPass.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, UserActivity.class)));
    }

    private void requestLogin(String email, String password) {
        loginButton.setEnabled(false);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                        prefManager.setLoggedIn(true, userId);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = getAuthErrorMessage(task.getException());
                        displayMsg.setVisibility(View.VISIBLE);
                        displayMsg.setText(message);
                        displayMsg.setTextColor(Color.parseColor("#FF0000"));
                    }
                });
    }

    private String getAuthErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            String code = authException.getErrorCode();
            String message = authException.getMessage();

            if ("ERROR_INTERNAL_ERROR".equals(code) && message != null && message.contains("CONFIGURATION_NOT_FOUND")) {
                return "Firebase Authentication is not configured correctly for this app. Enable Email/Password sign-in in Firebase Console, add the app's SHA-1/SHA-256 fingerprints, and download google-services.json again.";
            }

            if (exception instanceof FirebaseAuthInvalidUserException) {
                return "No account found with this email.";
            }
            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                return "Invalid email or password.";
            }

            return message != null ? message : "Authentication failed. Please check your credentials.";
        }

        return exception != null ? exception.getMessage() : "Authentication failed. Please check your credentials.";
    }

}
