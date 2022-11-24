package com.example.spynetwork.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import com.example.spynetwork.components.MessagesAdapter;
import com.example.spynetwork.models.Device;
import com.example.spynetwork.tasks.FetchDataTask;
import com.example.spynetwork.Config;
import com.example.spynetwork.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private SharedPreferences settings;

    private ImageButton refreshButton;
    private ScrollView registerPage;
    private EditText registerNameInput;
    private Button registerButton;
    private ListView messagesPage;
    private MessagesAdapter messagesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences("settings", Context.MODE_PRIVATE);

        refreshButton = findViewById(R.id.main_refresh_button);
        registerPage = findViewById(R.id.main_register_page);
        registerNameInput = findViewById(R.id.main_register_name_input);
        registerButton = findViewById(R.id.main_register_button);
        messagesPage = findViewById(R.id.main_messages_page);

        // Messages page
        refreshButton.setOnClickListener((View v) -> {
            loadDevices();
        });
        messagesPage.setAdapter(messagesAdapter = new MessagesAdapter(this));

        // Register page
        if (settings.getLong("device_id", -1) == -1) {
            registerButton.setOnClickListener((View v) -> {
                String name = registerNameInput.getText().toString();
                if (name.length() >= 2) registerDevice(name);
            });

            refreshButton.setVisibility(View.GONE);
            registerPage.setVisibility(View.VISIBLE);
            messagesPage.setVisibility(View.GONE);
        } else {
            loadDevices();
        }
    }

    private void registerDevice(String name) {
        try {
            JSONObject newDeviceJson = new JSONObject();
            newDeviceJson.put("name", name);

            FetchDataTask.with(this)
                .method("POST")
                .load(Config.API_URL + "/devices")
                .header("apikey", Config.API_KEY)
                .header("Authorization", "Bearer " + Config.API_KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .body(newDeviceJson.toString())
            .then(data -> {
                try {
                    JSONArray devicesJson = new JSONArray(data);
                    JSONObject deviceJson = devicesJson.getJSONObject(0);
                    SharedPreferences.Editor settingsEditor = settings.edit();
                    settingsEditor.putLong("device_id", deviceJson.getLong("id"));
                    settingsEditor.apply();

                    JSONObject newDeviceMessageJson = new JSONObject();
                    newDeviceMessageJson.put("device_id", deviceJson.getLong("id"));
                    newDeviceMessageJson.put("sender", "System");
                    newDeviceMessageJson.put("message", "Line is secure, when you get messages you will see them here");

                    FetchDataTask.with(this)
                        .method("POST")
                        .load(Config.API_URL + "/device_messages")
                        .header("apikey", Config.API_KEY)
                        .header("Authorization", "Bearer " + Config.API_KEY)
                        .header("Content-Type", "application/json")
                        .body(newDeviceMessageJson.toString())
                    .then(data2 -> {
                        refreshButton.setVisibility(View.VISIBLE);
                        registerPage.setVisibility(View.GONE);
                        messagesPage.setVisibility(View.VISIBLE);
                        loadDevices();
                    }).fetch();
                } catch (Exception exception) {
                    Log.e(Config.LOG_TAG, "", exception);
                }
            }).fetch();
        } catch (Exception exception) {
            Log.e(Config.LOG_TAG, "", exception);
        }
    }

    private void loadDevices() {
        FetchDataTask.with(this)
            .load(Config.API_URL + "/devices?id=eq." + String.valueOf(settings.getLong("device_id", -1)) + "&select=*,device_messages(*)")
            .header("apikey", Config.API_KEY)
            .header("Authorization", "Bearer " + Config.API_KEY)
        .then(data -> {
            try {
                messagesAdapter.clear();
                JSONArray devicesJson = new JSONArray(data);
                JSONObject deviceJson = devicesJson.getJSONObject(0);
                Device device = Device.fromJson(deviceJson);
                if (device.messages.size() > 0) {
                    for (int i = device.messages.size() - 1; i >= 0; i--) {
                        messagesAdapter.add(device.messages.get(i));
                    }
                }
            } catch (Exception exception) {
                Log.e(Config.LOG_TAG, "", exception);
            }
        }).fetch();
    }
}
