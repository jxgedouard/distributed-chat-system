package messages;

import java.time.format.DateTimeFormatter;

public class SystemMessage extends Message {
    
    public SystemMessage(String content) {
        super("SYSTEM", content);
    }

    @Override
    public String format() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = getTimestamp().format(formatter);
        return "[" + time + "] [SYSTEM] " + getContent();
    }
    
}
