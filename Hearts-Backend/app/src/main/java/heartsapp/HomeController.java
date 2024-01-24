package heartsapp;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/")
    public String home() {
        return "Hello from Backend!";
    }

}