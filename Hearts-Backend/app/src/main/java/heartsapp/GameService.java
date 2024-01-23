package heartsapp;
import heartsapp.Player;
import heartsapp.Card;
import heartsapp.Main;



import org.springframework.stereotype.Service;

@Service
public class GameService {

    //Plays the game
    public static void Play_Hearts(Player p1, Player p2, Player p3, Player p4){

        Card[] deck = new Card[52];

        int round_number = 0;
        boolean game_over = false;
      /*   print_scoreboard(round_number, p1, p2, p3, p4);
        while(p1.total<100 && p2.total<100 && p3.total<100 && p4.total<100){
            make_deck(deck);
            shuffle_and_deal(deck, p1, p2, p3, p4);

            Play_hand(p1, p2, p3, p4, round_number);
            print_scoreboard(round_number, p1, p2, p3, p4);

            round_number +=1;
        }

        System.out.println("The game is over!!"); */

    } 

    // Add more methods to handle other game actions
}