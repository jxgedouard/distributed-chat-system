package messages;

import java.time.format.DateTimeFormatter;

public class PrivateMessage extends Message{
    private String recipient;

    public PrivateMessage(String sender, String recipient, String content) {
        super(sender, content);
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    @Override
    public String format() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = getTimestamp().format(formatter);
        return "[" + time + "] [MP de " + getSender() + " à " + getRecipient() + "] " + getContent();
    }

}
