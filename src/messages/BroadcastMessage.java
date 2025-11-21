package messages;

import java.time.format.DateTimeFormatter;

public class BroadcastMessage extends Message {
    
    public BroadcastMessage(String sender, String content) {
        super(sender, content);
    }

    @Override
    public String format() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = getTimestamp().format(formatter);
        return "[" + time + "] " + getSender() + ": " + getContent();
    }
}
