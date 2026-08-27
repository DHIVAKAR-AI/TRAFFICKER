package com.example.traffiker;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Random;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private boolean isSosActive = false;
    private Handler handler = new Handler();
    private Vibrator vibrator;
    private Random random = new Random();
    private int uptimeSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Components
        viewPager = findViewById(R.id.viewPager);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // --- SETUP VIEW PAGER ADAPTER ---
        viewPager.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                int layoutId = (viewType == 0) ? R.layout.fragment_dashboard :
                        (viewType == 1) ? R.layout.fragment_map : R.layout.fragment_sos;
                View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (position == 0) setupDashboardPage(holder.itemView);
                else if (position == 1) setupMapPage(holder.itemView);
                else if (position == 2) setupSosPage(holder.itemView);
            }

            @Override
            public int getItemCount() { return 3; }
            @Override
            public int getItemViewType(int position) { return position; }
        });

        // --- SYNC NAVIGATION ---
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) viewPager.setCurrentItem(0);
            else if (id == R.id.nav_map) viewPager.setCurrentItem(1);
            else if (id == R.id.nav_emergency) viewPager.setCurrentItem(2);
            return true;
        });
    }

    // --- DASHBOARD: LIVE TERMINAL & STATS ---
    private void setupDashboardPage(View root) {
        TextView hotspots = root.findViewById(R.id.hotspotText);
        TextView uptime = root.findViewById(R.id.uptimeText);
        ProgressBar peakBar = root.findViewById(R.id.peakBar);
        TextView peakPercent = root.findViewById(R.id.peakPercent);
        SwitchCompat themeSwitch = root.findViewById(R.id.themeSwitch);
        View bg = root.findViewById(R.id.dashboardRoot);

        String[] locs = {"Bus Stand", "Kovai Road", "Bypass", "Collectorate", "Gandhigram", "Five Road"};
        String[] stats = {"EXTREME", "HIGH", "CRITICAL", "HEAVY", "MODERATE"};

        // 1. Terminal Hotspot Randomizer (5 Seconds)
        handler.post(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    sb.append("📍 ").append(locs[random.nextInt(locs.length)])
                            .append(": ").append(stats[random.nextInt(stats.length)]).append("\n");
                }
                hotspots.setText(sb.toString().trim());

                // Randomize Peak Bar to match the "Live" feel
                int p = 60 + random.nextInt(35);
                peakBar.setProgress(p);
                peakPercent.setText(p + "% PEAK");
                handler.postDelayed(this, 5000);
            }
        });

        // 2. Live Uptime Counter
        handler.post(new Runnable() {
            @Override
            public void run() {
                uptimeSeconds++;
                int h = uptimeSeconds / 3600;
                int m = (uptimeSeconds % 3600) / 60;
                int s = uptimeSeconds % 60;
                uptime.setText(String.format("%02d:%02d:%02d", h, m, s));
                handler.postDelayed(this, 1000);
            }
        });

        // 3. Theme Toggle
        themeSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) bg.setBackgroundColor(Color.parseColor("#0F111A"));
            else bg.setBackgroundColor(Color.WHITE);
        });
    }

    // --- MAP: FULL SCREEN TRAFFIC ---
    private void setupMapPage(View root) {
        WebView map = root.findViewById(R.id.fullMap);
        if (map != null) {
            WebSettings ws = map.getSettings();
            ws.setJavaScriptEnabled(true);
            map.setWebViewClient(new WebViewClient());
            map.loadUrl("https://www.google.com/maps/@10.9601,78.0766,15z/data=!5m1!1e1");
        }
    }

    // --- SOS: VIBRATION & BREATHING ---
    private void setupSosPage(View root) {
        Button sosBtn = root.findViewById(R.id.sosTrigger);
        if (sosBtn != null) {
            sosBtn.setOnClickListener(v -> {
                if (!isSosActive) {
                    sosBtn.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    sosBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.breathing));
                    if (vibrator != null) {
                        long[] pattern = {0, 400, 400};
                        vibrator.vibrate(pattern, 0);
                    }
                    isSosActive = true;
                } else {
                    sosBtn.clearAnimation();
                    sosBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444")));
                    if (vibrator != null) vibrator.cancel();
                    isSosActive = false;
                }
            });
        }
    }
}