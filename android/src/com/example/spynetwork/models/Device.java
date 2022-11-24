package com.example.spynetwork.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Device {
    public long id;
    public String name;
    public ZonedDateTime createdAt;
    public List<DeviceMessage> messages = new ArrayList<DeviceMessage>();

    private Device() {}

    public static Device fromJson(JSONObject jsonDevice) throws JSONException {
        Device device = new Device();
        device.id = jsonDevice.getLong("id");
        device.name = jsonDevice.getString("name");
        device.createdAt = ZonedDateTime.parse(jsonDevice.getString("created_at"));

        JSONArray jsonDeviceMessages = jsonDevice.getJSONArray("device_messages");
        for (int j = 0; j < jsonDeviceMessages.length(); j++) {
            device.messages.add(DeviceMessage.fromJson(jsonDeviceMessages.getJSONObject(j)));
        }
        return device;
    }
}
