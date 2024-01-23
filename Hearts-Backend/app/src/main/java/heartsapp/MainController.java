package heartsapp;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/data")
    public Message getData() {
        return new Message("Hello from the backend!");
    }

    // ... other code ...
}

class Message {
    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



   /* private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/startGame")
    public void startGame() {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        Player player3 = new Player("Player 3");
        Player player4 = new Player("Player 4");

        player1.next = player2;
        player2.next = player3;
        player3.next = player4;
        player4.next = player1;

        GameService.Play_Hearts(player1, player2, player3, player4);
    } */

    // Add more methods as needed
