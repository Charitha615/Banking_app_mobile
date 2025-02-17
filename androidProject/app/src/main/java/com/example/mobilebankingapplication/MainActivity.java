package com.example.mobilebankingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import pl.droidsonroids.gif.GifImageView;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private GifImageView gifImageView;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifImageView = findViewById(R.id.gifImageView);
        welcomeText = findViewById(R.id.welcomeText);

        // Delay for 1.5 seconds
        new Handler().postDelayed(() -> {
            // Show the welcome text
            welcomeText.setVisibility(View.VISIBLE);

            // Delay for another 1 second before navigating to the next page
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
                finish(); // Close the current activity
            }, 2000); // 1 second delay
        }, 4500); // 1.5 seconds delay
    }
}