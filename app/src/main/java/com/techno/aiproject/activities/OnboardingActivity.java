package com.techno.aiproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.techno.aiproject.R;
import com.techno.aiproject.activities.auth.LoginActivity;
import com.techno.aiproject.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button btnGetStarted;
    private TextView tvSkip;
    private List<OnboardingItem> items;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        prefManager = new PrefManager(this);
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        tvSkip = findViewById(R.id.tvSkip);

        setupItems();
        viewPager.setAdapter(new OnboardingAdapter(items));
        setupDots();
        setCurrentDot(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentDot(position);
                if (position == items.size() - 1) {
                    btnGetStarted.setText("Get Started");
                } else {
                    btnGetStarted.setText("Next");
                }
            }
        });

        btnGetStarted.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < items.size() - 1) {
                viewPager.setCurrentItem(current + 1);
            } else {
                finishOnboarding();
            }
        });

        tvSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupItems() {
        items = new ArrayList<>();
        items.add(new OnboardingItem(
                "Capture or Upload",
                "Take a photo of any herb or upload from your gallery using your camera or gallery launcher.",
                R.drawable.newlogo
        ));
        items.add(new OnboardingItem(
                "Multi-AI Pipeline",
                "HerbalEyes validates images using PlantNet and Google Gemini to identify plants.",
                R.drawable.ic_plant_placeholder
        ));
        items.add(new OnboardingItem(
                "Ayurvedic Explanations",
                "Generate complete Ayurvedic guides with descriptions, uses, and warnings in Sinhala, Tamil, or English.",
                R.drawable.newlogo
        ));
    }

    private void setupDots() {
        ImageView[] dots = new ImageView[items.size()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(android.R.drawable.presence_invisible);
            dots[i].setBackgroundResource(android.R.drawable.btn_default);
            dotsLayout.addView(dots[i], params);
        }
    }

    private void setCurrentDot(int index) {
        int childCount = dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View dotView = dotsLayout.getChildAt(i);
            if (i == index) {
                dotView.setAlpha(1.0f);
            } else {
                dotView.setAlpha(0.4f);
            }
        }
    }

    private void finishOnboarding() {
        prefManager.setOnboardingComplete(true);
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private static class OnboardingItem {
        final String title;
        final String desc;
        final int imageResId;

        OnboardingItem(String title, String desc, int imageResId) {
            this.title = title;
            this.desc = desc;
            this.imageResId = imageResId;
        }
    }

    private static class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.PageViewHolder> {
        private final List<OnboardingItem> items;

        OnboardingAdapter(List<OnboardingItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_onboarding_page, parent, false);
            return new PageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
            OnboardingItem item = items.get(position);
            holder.tvTitle.setText(item.title);
            holder.tvDesc.setText(item.desc);
            holder.ivImage.setImageResource(item.imageResId);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class PageViewHolder extends RecyclerView.ViewHolder {
            final ImageView ivImage;
            final TextView tvTitle;
            final TextView tvDesc;

            PageViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.ivOnboardingImage);
                tvTitle = itemView.findViewById(R.id.tvOnboardingTitle);
                tvDesc = itemView.findViewById(R.id.tvOnboardingDesc);
            }
        }
    }
}
