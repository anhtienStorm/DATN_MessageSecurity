package com.example.datn;

public class Conversation {

    String date;
    String reply_path_present;
    String body;
    String type;
    String thread_id;
    String locked;
    String date_send;
    String read;
    String address;
    String sub_id;
    String service_center;
    String error_code;
    String _id;
    String status;

    public Conversation(String date, String reply_path_present, String body, String type,
                        String thread_id, String locked, String date_send, String read,
                        String address, String sub_id, String service_center, String error_code,
                        String _id, String status) {
        this.date = date;
        this.reply_path_present = reply_path_present;
        this.body = body;
        this.type = type;
        this.thread_id = thread_id;
        this.locked = locked;
        this.date_send = date_send;
        this.read = read;
        this.address = address;
        this.sub_id = sub_id;
        this.service_center = service_center;
        this.error_code = error_code;
        this._id = _id;
        this.status = status;
    }
}
