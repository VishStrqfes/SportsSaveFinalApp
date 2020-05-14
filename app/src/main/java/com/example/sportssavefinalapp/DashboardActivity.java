package com.example.sportssavefinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button joinGame, profile, badges, messages;
    String sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        joinGame = (Button) findViewById(R.id.joinGameButton);
        profile = (Button) findViewById(R.id.profileButton);
        badges = (Button) findViewById(R.id.badgesButton);
        messages = (Button) findViewById(R.id.messagesButton);

        badges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fklc = new Intent(DashboardActivity.this, BadgesActivity.class);
                startActivity(fklc);

            }
        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Messaging feauture is not released yet! Check back soon!", Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sportDialogBox();
            }
        });

    }
    private void sportDialogBox() {
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sport_dialog_box);
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerItems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        final Button doneButton = (Button) dialog.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(DashboardActivity.this, MapsActivity.class);
                go.putExtra("Sport", sport);
                startActivity(go);

            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sport = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, sport, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
