package com.projetoa2.model;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private long timestamp;

    public Message(int id, int senderId, int receiverId, String content, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    public String serialize() {
        return id + "|" + senderId + "|" + receiverId + "|" + content.replace("|"," ") + "|" + timestamp;
    }

    public static Message deserialize(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split("\\|");
        try {
            int id = Integer.parseInt(parts[0]);
            int s = Integer.parseInt(parts[1]);
            int r = Integer.parseInt(parts[2]);
            String content = parts[3];
            long ts = Long.parseLong(parts[4]);
            return new Message(id, s, r, content, ts);
        } catch (Exception e) {
            return null;
        }
    }
}
