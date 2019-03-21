package ru.atom.chat.client;

import okhttp3.Response;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.atom.chat.client.ChatClient;
import ru.atom.chat.server.ChatApplication;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChatApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatClientTest {
    private static String MY_NAME_IN_CHAT = "I_AM_STUPID";
    private static String MY_SECOND_NAME_IN_CHAT = "I_AM_NOT_STUPID";
    private static String MY_MESSAGE_TO_CHAT = "SOMEONE_KILL_ME";
    private static String MY_MESSAGE_TO_CHAT_WITH_SMILE = "What is it? lol";

    @Test
    public void login() throws IOException {
        Response response = ChatClient.login(MY_NAME_IN_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        System.out.println(body);
        Assert.assertEquals("Welcome, \"" + MY_NAME_IN_CHAT + "\"!", body);
    }

    @Test
    public void logout() throws IOException {
        Response response = ChatClient.logout(MY_NAME_IN_CHAT);
        System.out.println("[" + response + "]");
        String body = response.body().string();
        System.out.println(body);
        Assert.assertEquals("Invalid user name or that user is not online", body);
    }

    @Test
    public void viewChat() throws IOException {
        Response response = ChatClient.viewChat();
        String body = response.body().string();
        System.out.println("[" + response + "]");
        System.out.println(body);
        Assert.assertNotEquals("", body);
    }


    @Test
    public void viewOnline() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        Response response = ChatClient.viewOnline();
        ChatClient.logout(MY_NAME_IN_CHAT);
        String body = response.body().string();
        System.out.println("[" + response + "]");
        System.out.println(body);
        Assert.assertNotEquals("", body);
    }

    @Test
    public void say() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        Response response = ChatClient.say(MY_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT);
        ChatClient.logout(MY_NAME_IN_CHAT);
        String body = response.body().string();
        System.out.println("[" + response + "]");
        System.out.println(body);
        Assert.assertNotEquals("", body);
    }

    @Test
    public void sayWithSmile() throws IOException {
        ChatClient.login(MY_NAME_IN_CHAT);
        Response response = ChatClient.say(MY_NAME_IN_CHAT, MY_MESSAGE_TO_CHAT_WITH_SMILE);
        ChatClient.logout(MY_NAME_IN_CHAT);
        String body = response.body().string();
        System.out.println("[" + response + "]");
        System.out.println(body);
        Assert.assertNotEquals("", body);
    }

    @Test
    public void getHelp() throws IOException {
        String helpString = "You can use following commands:\n"
            + "help: look for help;\n"
            + "logout: quit from chat;\n"
            + "rename: change another name for user;\n"
            + "say: say something in chat;\n"
            + "login: sing in to chat;\n";
        Response response = ChatClient.viewHelp();
        String body = response.body().string();
        System.out.println("[" + response + "]");
        System.out.println(body + "\n");
        System.out.println(helpString);
        Assert.assertEquals(helpString, body);
    }

    @Test
    public void setNewName() throws IOException {
        Response response = ChatClient.rename(MY_SECOND_NAME_IN_CHAT, MY_NAME_IN_CHAT);
        String body = response.body().string();
        System.out.println("[" + response + "]");
        System.out.println(body);
        Assert.assertEquals("Well, know you are \"" + MY_SECOND_NAME_IN_CHAT + "\"!", body);
    }
}
