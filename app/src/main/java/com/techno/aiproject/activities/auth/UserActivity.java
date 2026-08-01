package com.techno.aiproject.activities.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techno.aiproject.R;
import com.techno.aiproject.api.ApiClient;
import com.techno.aiproject.models.FrogRequest;
import com.techno.aiproject.models.PublicRes;
import com.techno.aiproject.utils.CheckConnection;
import com.techno.aiproject.utils.PrefManager;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    private Button submit;
    private TextView display;
    private PrefManager prefManager;
    private IntlPhoneInput phoneInputView;
    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        phoneInputView = findViewById(R.id.my_phone_input);
        submit = findViewById(R.id.submitCode);
        display = findViewById(R.id.tvStatus);

        prefManager = new PrefManager(UserActivity.this);
        display.setVisibility(View.GONE);

        phoneInputView.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
            @Override
            public void done(View view, boolean isValid) {
                if (isValid) {
                    Toast.makeText(UserActivity.this, "Phone number is valid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserActivity.this, "Phone number is not valid", Toast.LENGTH_SHORT).show();
                }
                countryCode = phoneInputView.getSelectedCountry().getIso() + "-" + phoneInputView.getSelectedCountry().getDialCode();
            }
        });

        submit.setOnClickListener(view -> {
            if (CheckConnection.isConnected(UserActivity.this)) {
                String ph = phoneInputView.getNumber();
                if (ph != null && !ph.isEmpty()) {
                    sendCode();
                    submit.setEnabled(false);
                } else {
                    display.setVisibility(View.VISIBLE);
                    display.setText("Please Enter a Valid Phone Number");
                    display.setTextColor(Color.parseColor("#FF0000"));
                    submit.setEnabled(true);
                }
            } else {
                display.setVisibility(View.VISIBLE);
                display.setText("Please Connect to Internet");
                display.setTextColor(Color.parseColor("#FF0000"));
                submit.setEnabled(true);
            }
        });
    }

    public void sendCode() {
        submit.setEnabled(false);
        FrogRequest req = new FrogRequest();
        req.setPhone(phoneInputView.getNumber());
        
        ApiClient.getCheckPhoneNumber().SendCode(req).enqueue(new Callback<PublicRes>() {
            @Override
            public void onResponse(Call<PublicRes> call, Response<PublicRes> response) {
                submit.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    PublicRes respo = response.body();
                    if (respo.isResult()) {
                        prefManager.setLoggedIn(false, respo.getUser_id());
                        Intent intent = new Intent(UserActivity.this, ForgetPasswordActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        display.setVisibility(View.VISIBLE);
                        display.setText(respo.getMessage());
                        display.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
            }

            @Override
            public void onFailure(Call<PublicRes> call, Throwable t) {
                submit.setEnabled(true);
                if (t instanceof IOException) {
                    Toast.makeText(UserActivity.this, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserActivity.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
