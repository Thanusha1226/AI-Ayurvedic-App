package com.techno.aiproject.activities.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.techno.aiproject.R;
import com.techno.aiproject.utils.CheckConnection;

public class ForgetPasswordActivity extends AppCompatActivity {
    private TextView display;
    private EditText emailInput;
    private Button reset;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frog_pass);
        display = findViewById(R.id.tvStatus);
        emailInput = findViewById(R.id.editEmail);
        reset = findViewById(R.id.resetPassButtn);

        auth = FirebaseAuth.getInstance();
        display.setVisibility(View.GONE);

        reset.setOnClickListener(view -> {
            if (!CheckConnection.isConnected(ForgetPasswordActivity.this)) {
                display.setVisibility(View.VISIBLE);
                display.setText("Please connect to the internet.");
                display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            String email = emailInput.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                display.setVisibility(View.VISIBLE);
                display.setText("Please enter your registered email.");
                display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                display.setVisibility(View.VISIBLE);
                display.setText("Please enter a valid email address.");
                display.setTextColor(Color.parseColor("#FF0000"));
                return;
            }

            reset.setEnabled(false);
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        reset.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPasswordActivity.this, "Recovery email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            String message = "Unable to send recovery email.";
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                message = "No account is registered with this email.";
                            } else if (task.getException() != null) {
                                message = task.getException().getMessage();
                            }
                            display.setVisibility(View.VISIBLE);
                            display.setText(message);
                            display.setTextColor(Color.parseColor("#FF0000"));
                        }
                    });
        });
    }
}
