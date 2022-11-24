package com.example.spynetwork.models;

import java.time.ZonedDateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceMessage {
    public long id;
    public String sender;
    public String message;
    public ZonedDateTime createdAt;

    private DeviceMessage() {}

    public static DeviceMessage fromJson(JSONObject jsonDeviceMessage) throws JSONException {
        DeviceMessage deviceMessage = new DeviceMessage();
        deviceMessage.id = jsonDeviceMessage.getLong("id");
        deviceMessage.sender = jsonDeviceMessage.getString("sender");
        deviceMessage.message = jsonDeviceMessage.getString("message");
        deviceMessage.createdAt = ZonedDateTime.parse(jsonDeviceMessage.getString("created_at"));
        return deviceMessage;
    }
}
