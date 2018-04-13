package ca.sheridan.research.protocol;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class Packet {

    private String username;
    private String message;

    public Packet(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public void writeString(ByteBuf buf, String s) {
        try {
            byte[] bytes = s.getBytes("UTF-8");

            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void write(ByteBuf bb) {
        writeString(bb, username);
        writeString(bb, message);
    }

    public static Packet read(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        try {
            String username = new String(bytes, "UTF-8");
            bytes = new byte[buf.readInt()];
            buf.readBytes(bytes);
            String message = new String(bytes, "UTF-8");
            return new Packet(username, message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
