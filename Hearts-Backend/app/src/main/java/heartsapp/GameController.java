package heartsapp;



import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/startGame")
    public String[] startNewGame() {

        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");
        Player p3 = new Player("Player 3");
        Player p4 = new Player("Player 4");
        p1.next = p2;
        p2.next = p3;
        p3.next = p4;
        p4.next = p1;


        Card[] deck = new Card[52];

        //int round_number = 0;
        
        //print_scoreboard(round_number, p1, p2, p3, p4);
        //while(p1.total<100 && p2.total<100 && p3.total<100 && p4.total<100){
            make_deck(deck);
            shuffle_and_deal(deck, p1, p2, p3, p4);

            //Play_hand(p1, p2, p3, p4, round_number);
            //print_scoreboard(round_number, p1, p2, p3, p4);

           // round_number +=1;
        //}

        //System.out.println("The game is over!!");




        return p1.getImageUrls();
    }
    public static void make_deck(Card[] deck){
        HashMap<Integer, String> key = make_key();
        
        for(int i =13;i<17;i++){
            for(int j =0; j<13; j++){
                StringBuilder sig = new StringBuilder();
                Card now = new Card(j+2,i, 0 );
                sig.append(key.get(j));
                sig.append(key.get(i));
                now.add_sig(sig);
                if(i==16){
                    now.add_point(1);
                }
                deck[((i-13)*13)+j] = now;
            }
        }
        deck[23].point_value = 13;


        return;
    }

    public static HashMap<Integer, String> make_key(){
        HashMap<Integer, String> key = new HashMap<>();
        key.put(0,"2"); // Chars for values
        key.put(1,"3");
        key.put(2,"4");
        key.put(3,"5");
        key.put(4,"6");
        key.put(5,"7");
        key.put(6,"8");
        key.put(7,"9");
        key.put(8,"10");
        key.put(9, "J");
        key.put(10,"Q");
        key.put(11,"k");
        key.put(12,"A");

        key.put(13,"C"); //Chars for suits
        key.put(14,"S");
        key.put(15,"D");
        key.put(16,"H");

        return key;
    }

    public static void shuffle_and_deal(Card[] deck, Player p1, Player p2, Player p3, Player p4){
        List<Card> s_deck = Arrays.asList(deck);
        Collections.shuffle(s_deck);
        s_deck.toArray(deck);

        int i = 0;
        for(int j =0; j<13;j++){
            p1.hand[j] = deck[i];
            i+=1;
            p2.hand[j] = deck[i];
            i+=1;
            p3.hand[j] = deck[i];
            i+=1;
            p4.hand[j] = deck[i];
            i+=1;
        }
        p1.Sort_Hand();

    }

}