package heartsapp;

import heartsapp.Message;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/message")
    public Message getMessage() {
        return new Message("Hello from the backend!");
    }
}