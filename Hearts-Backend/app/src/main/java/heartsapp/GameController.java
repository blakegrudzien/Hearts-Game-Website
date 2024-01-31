package heartsapp;



import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.yaml.snakeyaml.util.ArrayUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.JedisShardInfo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import redis.clients.jedis.Jedis;



@Service
@RestController
@SessionAttributes("gameState")
public class GameController {
    
    
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getGameState")
    public String getGameState() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        String gameState = null;
        try {
            gameState = jedis.get("gameState");
            System.out.println("gameState from Redis: " + gameState);
        } catch (Exception e) {
            System.out.println("Exception while getting gameState from Redis: " + e.getMessage());
        }
        
        return gameState;
    }



    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getturn")
public Integer getturn() {
    JedisShardInfo shardInfo = new JedisShardInfo("localhost");
    Jedis jedis = new Jedis(shardInfo);
    Integer turn = null;
    try {
        turn = Integer.parseInt(jedis.get("turn"));
        System.out.println("turn Redis: " + turn);
    } catch (Exception e) {
        System.out.println("Exception while getting turn from Redis: " + e.getMessage());
    }
    
    return turn;
}



    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getTrick")
    public String[] getTrick() {
    JedisShardInfo shardInfo = new JedisShardInfo("localhost");
    Jedis jedis = new Jedis(shardInfo);
    ObjectMapper mapper = new ObjectMapper();
    String[] trickList = new String[4];
    Card[] trick = null;
    try {
        String trickJson = jedis.get("trick");
        trick = mapper.readValue(trickJson, Card[].class);
        System.out.println("Trick from Redis: " + Arrays.toString(trick));
    } catch (Exception e) {
        System.out.println("Exception while getting trick from Redis: " + e.getMessage());
    }

    for(int i = 0;i<4;i++){
        if(trick[i]!=null){
            trickList[i] = trick[i].imageURL;
        }
    }
    
    return trickList != null ? trickList : new String[0];
}




    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/get_played_card/{player}")
    public String getPlayedCard(@PathVariable String player) {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Get the trick from Redis
            String trickJson = jedis.get("trick");

            // Deserialize the trick from JSON to a Card array
            Card[] trick = mapper.readValue(trickJson, Card[].class);

            String playerJson = jedis.get(player);
            Player Card_player = mapper.readValue(playerJson, Player.class);

            // Find the card played by the specified player
            for (Card card : trick) {
                if (card != null && card.Holder.equals(Card_player)) {
                    // Return the URL of the card
                    return card.imageURL;
                }
            }
    
            // If no card was played by the specified player, return null
            return null;
        } catch (Exception e) {
            System.out.println("Exception while getting played card: " + e.getMessage());
            return "Error while getting played card";
        } 
    }
    





    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/player_plays")
    public void playerPlays(@RequestBody Map<String, Integer> payload) {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper();

        try {
            int index = payload.get("index");

            String p1Json = jedis.get("p1");

            Player p1 = mapper.readValue(p1Json, Player.class);

   
            Card playedCard = p1.hand[index];

            p1.hand[index] = null;
            p1Json = mapper.writeValueAsString(p1);

            // Update the p1 object in Redis
            jedis.set("p1", p1Json);

            String trickJson = jedis.get("trick");

            Card[] trick = mapper.readValue(trickJson, Card[].class);

            for(int i = 0;i<4;i++){
                if(trick[i] == null){
                    trick[i] = playedCard;
                    break;
                }
            }
            String updatedTrickJson = mapper.writeValueAsString(trick);

            // Update the trick array in Redis
            jedis.set("trick", updatedTrickJson);
        } catch (Exception e) {
            System.out.println("Exception while playing card: " + e.getMessage());
        }
    }




    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/playCard")
    public Object playCard() throws JsonMappingException, JsonProcessingException {

        System.out.println("playCard() called");
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper();

        // Get the trick array from Redis
        String trickJson = jedis.get("trick");

        // Deserialize the trick array from JSON to a Card array
        Card[] trick = mapper.readValue(trickJson, Card[].class);
        String heartsBrokenJson = jedis.get("Hearts_Broken");
        String gameState = jedis.get("gameState");

        // Get each player from Redis
        String p1Json = jedis.get("p1");
        String p2Json = jedis.get("p2");
        String p3Json = jedis.get("p3");
        String p4Json = jedis.get("p4");

        // Deserialize each player from JSON to a Player object
        Player p1 = mapper.readValue(p1Json, Player.class);
        Player p2 = mapper.readValue(p2Json, Player.class);
        Player p3 = mapper.readValue(p3Json, Player.class);
        Player p4 = mapper.readValue(p4Json, Player.class);
        

        // Get the turn from Redis
        String turnJson = jedis.get("turn");
        String trick_numberJson = jedis.get("trick_number");
        boolean Hearts_Broken = Boolean.parseBoolean(heartsBrokenJson);

        // Convert the turn from JSON to an int
        int turn = Integer.parseInt(turnJson);
        int trick_number = Integer.parseInt(trick_numberJson);


        Card play = null;
        int played = 0;
        int num = 0;
        for(int i = 0; i<4;i++){
            if(trick[i]!=null){
                num+=1;
            }
        }

        if(turn == 1){
            System.out.println("player 1 called it");
            gameState = "Play";
            boolean[] valid = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false};
            boolean possible_play = false;
            if(num == 0){
                
                if(Hearts_Broken == true){
                    System.out.println("First Card and Hearts are Broken");
                    for(int i = 0;i<13;i++){
                        if(p1.hand[i]!=null){
                            valid[i] = true;
                                
                        }
                    }
    
                        
                }
                else{
                    System.out.println("First Card and Hearts are Not Broken");
                    for(int i = 0;i<13;i++){
                        if(p1.hand[i]!=null && p1.hand[i].suit_char != 'h'){
                            valid[i] = true;
                            possible_play = true;
                        }
                            
                    }
                    if(possible_play == false){
                        for(int i = 0;i<13;i++){
                            if(p1.hand[i]!=null){
                                valid[i] = true;
                                    
                            }
                        }

                    }
                        
                }
            }
            else{
               
                char trick_char = trick[0].suit_char;
                System.out.println("not the first Card and Suit is: " + trick_char);
                for(int i = 0;i<13;i++){
                    if(p1.hand[i]!=null && p1.hand[i].suit_char == trick_char){
                        valid[i] = true;
                        possible_play = true;
                    }
                }
                if(possible_play == false){
                    for(int i = 0;i<13;i++){
                        if(p1.hand[i]!=null){
                            valid[i] = true;
                                
                        }
                    }

                }
                    
            }
            for(int i = 0;i<13;i++){
                if(valid[i] == true){
                    System.out.println(valid[i]);
                }
            }
            System.out.println(valid);

            for( int i = 0;i<13;i++){  
                if(valid[i] == true){
                    System.out.println(p1.hand[i].imageURL);
                }
             
            }
            return valid;
        }
        else if(turn == 2){
            System.out.println("player 2 called it");
            played = p2.play_card(trick, num, Hearts_Broken, trick_number);
            play = p2.hand[played];
            turn = 3;
            p2.hand[played] = null;
                

        }
        else if(turn == 3){
            System.out.println("player 3 called it");
            played = p3.play_card(trick, num, Hearts_Broken, trick_number);
            play = p3.hand[played];
            turn = 4;
            p3.hand[played] = null;

        }
        else{
            System.out.println("player 4 called it");
            played = p4.play_card(trick, num, Hearts_Broken, trick_number);
            play = p4.hand[played];
            turn = 1;
            p4.hand[played] = null;
        }


        if(play.suit_char == 'h'){
            Hearts_Broken = true;
        }

        if(num == 4){
            Card high = trick[0];
            int trick_suit = trick[0].suit;
            int points = trick[0].point_value;
            for(int i = 1;i<4; i++){
                if(trick[i].suit == trick_suit){
                    if(trick[i].val > high.val){
                        high = trick[i];
                    }

                }
            }
            high.Holder.score[trick_number] = points;
            if(high.Holder == p1){
                turn = 1;
            }
            else if(high.Holder == p2){
                turn = 2;
            }
            else if(high.Holder == p3){
                turn = 3;
            }
            else{
                turn = 4;
            }


            for(int i = 0;i<4;i++){
                trick[i] = null;
            }
            trick_number += 1;

        }


        jedis.set("Hearts_Broken", String.valueOf(Hearts_Broken));
        jedis.set("p1", mapper.writeValueAsString(p1));
        jedis.set("p2", mapper.writeValueAsString(p2));
        jedis.set("p3", mapper.writeValueAsString(p3));
        jedis.set("p4", mapper.writeValueAsString(p4));
        jedis.set("turn", String.valueOf(turn));
        jedis.set("trick_number", String.valueOf(trick_number));
        jedis.set("gameState", gameState);
        jedis.set("trick", mapper.writeValueAsString(trick));
            
            
        

    return null;
    }

   
    @CrossOrigin(origins = "http://localhost:3000")
@PostMapping("/performSwap")
public void swap_cards(@RequestBody int[] swaps) {
    // Create a JedisShardInfo object
    JedisShardInfo shardInfo = new JedisShardInfo("localhost");

    // Create a Jedis object
    Jedis jedis = new Jedis(shardInfo);

    // Create an ObjectMapper object
    ObjectMapper mapper = new ObjectMapper();

    // Get the players and the round number from Redis
    Player p1 = null;
    Player p2 = null;
    Player p3 = null;
    Player p4 = null;
    int round = 0;
    String gameState = null;
    int turn = 0;

    




    try {
        gameState = jedis.get("gameState");
        p1 = mapper.readValue(jedis.get("p1"), Player.class);
        p2 = mapper.readValue(jedis.get("p2"), Player.class);
        p3 = mapper.readValue(jedis.get("p3"), Player.class);
        p4 = mapper.readValue(jedis.get("p4"), Player.class);
        round = Integer.parseInt(jedis.get("round_number"));
        turn = Integer.parseInt(jedis.get("turn"));
        
    } catch (Exception e) {
        System.out.println("Exception while getting players and round number from Redis: " + e.getMessage());
    }

    p1.next = p2;
    p2.next = p3;
    p3.next = p4;
    p4.next = p1;

    
    p1.swap = swaps;
    p2.choose_swaps();
    p3.choose_swaps();
    p4.choose_swaps();

    if (p2 != null) {
        p2.choose_swaps();
    } else {
        System.out.println("p2 is null");
    }
    
    if (p3 != null) {
        p3.choose_swaps();
    } else {
        System.out.println("p3 is null");
    }
    
    if (p4 != null) {
        p4.choose_swaps();
    } else {
        System.out.println("p4 is null");
    }

    Card[] temp = {null, null, null};

    if(round % 4 == 1){
        circle_swaps(p1, p2, p3, p4, temp);
    }
    else if(round % 4 == 2){
        across_swaps(p1, p3, temp);
        across_swaps(p2, p4, temp);
    }
    else{
        circle_swaps(p4, p3, p2, p1, temp);
    }
    p1.Sort_Hand();
    gameState = "Play";
    turn = find_start(p1);
    System.out.println(turn + "has the 2 of clubs");
    

    
    jedis.set("gameState", gameState);
    round +=1;



    try {
        jedis.set("p1", mapper.writeValueAsString(p1));
        jedis.set("p2", mapper.writeValueAsString(p2));
        jedis.set("p3", mapper.writeValueAsString(p3));
        jedis.set("p4", mapper.writeValueAsString(p4));
        jedis.set("round_number", Integer.toString(round));
        jedis.set("turn", Integer.toString(turn));
    } catch (Exception e) {
        System.out.println("Exception while setting updated players and round number to Redis: " + e.getMessage());
    }


}

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/startGame")
    public void startNewGame(HttpSession session) {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper(); // create a new ObjectMapper

        
            jedis.set("gameState", "Swap");
        Player p1 = new Player("Player 1");
        Player p2 = new Player("Player 2");
        Player p3 = new Player("Player 3");
        Player p4 = new Player("Player 4");
        int round_number = 1;
        int trick_number = 0;
        String gameState = "Swap";
        Boolean Hearts_Broken = false;
        

        p1.next = p2;
        p2.next = p3;
        p3.next = p4;
        p4.next = p1;
        int turn = 0;

        Boolean[] ValidCard = new Boolean[13]; 
        Card[] trick = new Card[4];


        Card[] deck = new Card[52];

        make_deck(deck);
        shuffle_and_deal(deck, p1, p2, p3, p4);
        
        System.out.println("Turn =   " + turn);
      
        try {
            // serialize the players and round number to JSON and store them in Redis
            jedis.set("p1", mapper.writeValueAsString(p1));
            jedis.set("p2", mapper.writeValueAsString(p2));
            jedis.set("p3", mapper.writeValueAsString(p3));
            jedis.set("p4", mapper.writeValueAsString(p4));
            jedis.set("turn", Integer.toString(turn));
            jedis.set("round_number", Integer.toString(round_number));
            jedis.set("trick_number", Integer.toString(trick_number));
            jedis.set("gameState", gameState);
            jedis.set("trick", mapper.writeValueAsString(trick));
            jedis.set("ValidCard", mapper.writeValueAsString(ValidCard));
            jedis.set("Hearts_Broken", Hearts_Broken.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        

   
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getPlayerHand")
    public String[] getPlayerHand() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper(); // create a new ObjectMapper
    
    // Get the player from Redis
    Player p1 = null;
    try {
        p1 = mapper.readValue(jedis.get("p1"), Player.class);
    } catch (Exception e) {
        System.out.println("Exception while getting player from Redis: " + e.getMessage());
    }

    
    return p1.getImageUrls();
}

@CrossOrigin(origins = "http://localhost:3000")
@GetMapping("/getComputerHand")
public int getComputerHand(@RequestParam String playerName) {

    JedisShardInfo shardInfo = new JedisShardInfo("localhost");
    Jedis jedis = new Jedis(shardInfo);
    ObjectMapper mapper = new ObjectMapper();

    
    Player player = null;
    try {
        player = mapper.readValue(jedis.get(playerName), Player.class);
        
    } catch (Exception e) {
        System.out.println("Exception while getting player from Redis: " + e.getMessage());
    }
    int num = 0;
    for(int i = 0;i<13;i++){
        if(player.hand[i] != null){
            num+=1;
        }
    }

    return num;
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
        key.put(14,"D");
        key.put(15,"S");
        key.put(16,"H");

        return key;
    }

    public static void shuffle_and_deal(Card[] deck, Player p1, Player p2, Player p3, Player p4){
        List<Card> s_deck = Arrays.asList(deck);
        Collections.shuffle(s_deck);
        s_deck.toArray(deck);
    
        int i = 0;
        for(int j =0; j<13;j++){
            p1.hand[j] = deck[i++];
            p2.hand[j] = deck[i++];
            p3.hand[j] = deck[i++];
            p4.hand[j] = deck[i++];
        }
        p1.Sort_Hand();
    }

    

    public static void circle_swaps(Player p1, Player p2, Player p3, Player p4, Card[] temp){
        for(int i = 0;i<3;i++){
            temp[i] = p4.hand[p4.swap[i]];
            p4.hand[p4.swap[i]] = p3.hand[p3.swap[i]];
            p3.hand[p3.swap[i]] = p2.hand[p2.swap[i]];
            p2.hand[p2.swap[i]] = p1.hand[p1.swap[i]];
            p1.hand[p1.swap[i]] = temp[i];
        }



    }


    public static void across_swaps(Player p1, Player p2, Card[] temp){
        for(int i = 0;i<3;i++){
            temp[i] = p2.hand[p2.swap[i]];
            p2.hand[p2.swap[i]] = p1.hand[p1.swap[i]];
            p1.hand[p1.swap[i]] = temp[i];
        }


    }


    public static boolean valid_swap(int choice, int first, int second){
        if(choice < 13 && choice > -1){
            if(choice != first && choice != second){
                return true;
            }
            else{
                System.out.println("You already picked this card, pick another");
                return false;
            }
        }
        else{
            System.out.println("Pick a valid card to swap");
            return false;
        }
    }


    public static Integer find_start(Player p1){
        p1.Sort_Hand();
        p1.next.Sort_Hand();
        p1.next.next.Sort_Hand();
        p1.next.next.next.Sort_Hand();

        if(p1.hand[0].val == 2){
            return 1;
    
        }
        else if(p1.next.hand[0].val == 2){
            return 2;
        }
        else if(p1.next.next.hand[0].val == 2){
            return 3;
        }
        else if (p1.next.next.next.hand[0].val == 2){
            return 4;
        }
        else{
            System.out.println("Nobody has the card");
            return 4;
        }
    
    }




}
