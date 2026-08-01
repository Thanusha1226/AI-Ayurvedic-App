package com.techno.aiproject.activities.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.techno.aiproject.BuildConfig;
import com.techno.aiproject.R;
import com.techno.aiproject.activities.HomeActivity;
import com.techno.aiproject.utils.CheckConnection;
import com.techno.aiproject.utils.PrefManager;

import net.rimoto.intlphoneinput.IntlPhoneInput;

public class RegisterActivity extends AppCompatActivity {
    private EditText fname, Email, pass, Repass;
    private TextView Display;
    private Button Reg;
    private PrefManager prefManager;
    private FirebaseAuth auth;
    private IntlPhoneInput phoneInputView;
    private String countryCode, deviceId;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_details);

        fname = findViewById(R.id.editfname);
        Email = findViewById(R.id.editEmail);
        pass = findViewById(R.id.Editpass);
        Repass = findViewById(R.id.Repass);
        Display = findViewById(R.id.tvStatus);
        Reg = findViewById(R.id.nextbuttn);
        phoneInputView = findViewById(R.id.my_phone_input);
        tvLoginLink = findViewById(R.id.tvLogin);

        prefManager = new PrefManager(RegisterActivity.this);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        phoneInputView.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
            @Override
            public void done(View view, boolean isValid) {
                if (isValid) {
                    Toast.makeText(RegisterActivity.this, "Phone number is valid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Phone number is not valid", Toast.LENGTH_SHORT).show();
                }
                countryCode = phoneInputView.getSelectedCountry().getIso() + "-" + phoneInputView.getSelectedCountry().getDialCode();
            }
        });

        Display.setVisibility(View.GONE);

        fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Display.setText("");
                Display.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Display.setVisibility(View.GONE);
                Display.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailValidator(Email);
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Reg.setOnClickListener(view -> {
            if (!CheckConnection.isConnected(RegisterActivity.this)) {
                Display.setVisibility(View.VISIBLE);
                Display.setText("Please connect to the internet.");
                Display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            String fullName = fname.getText().toString().trim();
            String email = Email.getText().toString().trim();
            String password = pass.getText().toString();
            String confirmPassword = Repass.getText().toString();
            String phone = phoneInputView.getNumber();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                    || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone)) {
                Display.setVisibility(View.VISIBLE);
                Display.setText("All fields are required.");
                Display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Display.setVisibility(View.VISIBLE);
                Display.setText("Please enter a valid email.");
                Display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            if (!password.equals(confirmPassword)) {
                Display.setVisibility(View.VISIBLE);
                Display.setText("Passwords do not match.");
                Display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            if (password.length() < 6) {
                Display.setVisibility(View.VISIBLE);
                Display.setText("Password must be at least 6 characters.");
                Display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            registerUser(fullName, email, password, phone);
        });
    }

    public void emailValidator(EditText etMail) {
        String emailToText = etMail.getText().toString();
        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            Display.setVisibility(View.VISIBLE);
            Display.setText("Email Valid !");
            Display.setTextColor(Color.parseColor("#00FF00"));
        } else {
            Display.setVisibility(View.VISIBLE);
            Display.setText("Email Invalid Please Check Again !");
            Display.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private void registerUser(String fullName, String email, String password, String phone) {
        Reg.setEnabled(false);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Reg.setEnabled(true);
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        firebaseUser.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build());
                        prefManager.setLoggedIn(true, firebaseUser.getUid());
                        prefManager.setUserDetails(firebaseUser.getUid(), fullName, email, phone, deviceId);
                        Toast.makeText(RegisterActivity.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = getAuthErrorMessage(task.getException());
                        Display.setVisibility(View.VISIBLE);
                        Display.setText(message);
                        Display.setTextColor(Color.parseColor("#FF0000"));
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

            switch (code) {
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "This email address is already registered.";
                case "ERROR_INVALID_EMAIL":
                    return "Please enter a valid email address.";
                case "ERROR_WEAK_PASSWORD":
                    return "Please choose a stronger password.";
                default:
                    return message != null ? message : "Registration failed. Please try again.";
            }
        }

        return exception != null ? exception.getMessage() : "Registration failed. Please try again.";
    }
}
