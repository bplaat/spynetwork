package com.example.spynetwork.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.time.format.DateTimeFormatter;
import com.example.spynetwork.models.DeviceMessage;
import com.example.spynetwork.R;

public class MessagesAdapter extends ArrayAdapter<DeviceMessage>{
    private static class ViewHolder {
        public TextView senderLabel;
        public TextView createdAtLabel;
        public TextView messageLabel;
    }

    public MessagesAdapter(Context context) {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.senderLabel = convertView.findViewById(R.id.item_message_sender_label);
            viewHolder.createdAtLabel = convertView.findViewById(R.id.item_message_created_at_label);
            viewHolder.messageLabel = convertView.findViewById(R.id.item_message_message_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        DeviceMessage message = getItem(position);
        viewHolder.senderLabel.setText(message.sender);
        viewHolder.createdAtLabel.setText(DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm:ss").format(message.createdAt));
        viewHolder.messageLabel.setText(message.message);

        return convertView;
    }
}
