package ru.atom.chat.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Controller
@RequestMapping("chat")
public class ChatController {
    private Queue<String> messages = new ConcurrentLinkedQueue<>();
    private Map<String, String> usersOnline = new ConcurrentHashMap<>();
    private final Map<String, String> smileDict = new HashMap<String, String>() {
        {
            put("lol", "XD");
            put("ahaha", ":D");
        }
    };
    private final Map<String, String> commandDict = new HashMap<String, String>() {
        {
            put("login", ": sing in to chat");
            put("logout", ": quit from chat");
            put("say", ": say something in chat");
            put("rename", ": change another name for user");
            put("help", ": look for help");
        }
    };

    /**
     * curl -X POST -i localhost:8080/chat/login -d "name=I_AM_STUPID"
     */
    @RequestMapping(
        path = "login",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name) {
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name, sorry :(");
        }
        if (name.length() > 20) {
            return ResponseEntity.badRequest().body("Too long name, sorry :(");
        }
        if (usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("Already logged in :(");
        }
        usersOnline.put(name, name);
        messages.add(name + " logged in");
        return ResponseEntity.ok().body("Welcome, \"" + name + "\"!");
    }

    /**
     * curl -i localhost:8080/chat/online
     */
    @RequestMapping(
        path = "online",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
        String responseBody = String.join("\n", usersOnline.keySet().stream().sorted().collect(Collectors.toList()));
        return ResponseEntity.ok(responseBody);
    }

    /**
     * curl -X POST -i localhost:8080/chat/logout -d "name=I_AM_STUPID"
     */
    @RequestMapping(
        path = "logout",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestParam("name") String name) {
        if (usersOnline.containsKey(name)) {
            usersOnline.remove(name);
            messages.add(name + " logout");
            return ResponseEntity.ok().body("Goodbye! See you next time ;)");
        }
        return ResponseEntity.badRequest().body("Invalid user name or that user is not online");
    }

    /**
     * curl -X POST -i localhost:8080/chat/say -d "name=I_AM_STUPID&msg=Hello everyone in this chat"
     */
    @RequestMapping(
        path = "say",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("name") String name, @RequestParam("msg") String msg) {
        if (!usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("Invalid user name or that user is not online");
        }
        if (msg.trim().equals("")) {
            return ResponseEntity.ok().build();
        }
        if (msg.length() < 256) {
            messages.add(name + ": " + swapSmileToWordinMsg(msg));
            return ResponseEntity.ok().body("Message send");
        }
        return ResponseEntity.badRequest().body("Your message is too long");
    }

    /**
     * curl -i localhost:8080/chat/help
     */
    @RequestMapping(
        path = "help",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> help() {
        String msg = "You can use following commands:\n";
        for (Map.Entry<String, String> entry : commandDict.entrySet()) {
            msg = msg.concat(entry.getKey());
            msg = msg.concat(entry.getValue() + ";\n");
        }
        return ResponseEntity.ok().body(msg);
    }

    /**
     * curl -X POST -i localhost:8080/chat/rename -d "new_name=NEW_NAME&name=YOUR_NAME
     */
    @RequestMapping(
        path = "rename",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> rename(@RequestParam("new_name") String newName, @RequestParam("name") String name) {
        if (usersOnline.containsKey(newName)) {
            ResponseEntity.badRequest().body("That name is already using");
        }
        if (!usersOnline.containsKey(name)) {
            ResponseEntity.badRequest().body("That user is not online");
        }
        if (newName.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name, sorry :(");
        }
        if (newName.length() > 20) {
            return ResponseEntity.badRequest().body("Too long name, sorry :(");
        }
        usersOnline.remove(name);
        usersOnline.put(newName, newName);
        messages.add(name + " change name to " + newName);
        return ResponseEntity.ok().body("Well, know you are \"" + newName + "\"!");
    }


    /**
     * curl -i localhost:8080/chat/chat
     */
    @RequestMapping(
        path = "chat",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity chat() {
        String responseBody = String.join("\n", messages);
        return ResponseEntity.ok().body(responseBody);
    }


    String swapSmileToWordinMsg(String msg) {
        for (Map.Entry<String, String> entry : smileDict.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue());
        }
        return msg;
    }
}
