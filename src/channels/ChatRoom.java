package channels;

import java.util.concurrent.CopyOnWriteArrayList;
import server.Server.ClientHandler;

public class ChatRoom {
    private String name;
    private CopyOnWriteArrayList<ClientHandler> members;

    public ChatRoom(String name) {
        this.name = name;
        this.members = new CopyOnWriteArrayList<>();

    }

    public void addMember(ClientHandler client) {
        members.add(client);
    }

    public void removeMember(ClientHandler client) {
        members.remove(client);
    }

    public void broadcast(String message) {
        for (ClientHandler client : members) {
            client.sendMessage(message);
        }
    }

    public String getName() {
        return name;
    }

}
