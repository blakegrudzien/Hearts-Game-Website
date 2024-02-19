package heartsapp;

import java.io.IOException;
//import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpSession;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.exceptions.JedisConnectionException;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@RestController
@SessionAttributes("gameState")
public class GameController {

    private final JedisPool jedisPool;

    public GameController(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    

    /*
     * This function fetches the gamestate from redis then sends it to the frontend
     */
    
    //@CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getGameState")
public String getGameState() {
    try (Jedis jedis = jedisPool.getResource()) {
        return jedis.get("gameState");
    } catch (Exception e) {
        System.out.println("Exception while getting gameState from Redis: " + e.getMessage());
        return null;
    }
}


    /*
     * This function fetches the turn from redis then sends it to the frontend
     */

   // @CrossOrigin(origins = "http://localhost:3000")
   @GetMapping("/getturn")
   public Integer getTurn() {
       try (Jedis jedis = jedisPool.getResource()) {
           String turnStr = jedis.get("turn");
           if (turnStr != null) {
               return Integer.parseInt(turnStr);
           } else {
               // Handle the case where the turn is not set in Redis
               return null;
           }
       } catch (Exception e) {
           System.out.println("Exception while getting turn from Redis: " + e.getMessage());
           return null;
       }
   }


    /*
     * This function fetches the trick from redis then sends it to the frontend
     */
    //@CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getTrick")
public String[] getTrick() {
    try (Jedis jedis = jedisPool.getResource()) {
        ObjectMapper mapper = new ObjectMapper();
        String[] trickList = new String[4];
        
        String trickJson = jedis.get("trick");
        if (trickJson == null) {
            // Handle the case where the trick is not set in Redis
            return new String[0];
        }

        Card[] trick = mapper.readValue(trickJson, Card[].class);

        for (int i = 0; i < 4; i++) {
            if (trick[i] != null) {
                trickList[i] = trick[i].imageURL;
            }
        }
        return trickList;
    } catch (Exception e) {
        System.out.println("Exception while getting trick from Redis: " + e.getMessage());
        return new String[0];
    }
}



    /*
     * This function fetches the round number from redis then sends it to the frontend
     */
   // @CrossOrigin(origins = "http://localhost:3000")
   @GetMapping("/getRoundNumber")
   public int getRoundNumber() {
       try (Jedis jedis = jedisPool.getResource()) {
           String roundNumberStr = jedis.get("round_number");
           if (roundNumberStr == null) {
               // Handle the case where the round number is not set in Redis
               return -1;
           }
           
           return Integer.parseInt(roundNumberStr);
       } catch (Exception e) {
           System.out.println("Exception while getting round number: " + e.getMessage());
           return -1;
       }
   }
    

    /*
     * This function takes the card that the player has chosen and adds it to the trick and removes it from their hand
     */
   // @CrossOrigin(origins = "http://localhost:3000")
   @PostMapping("/player_plays")
   public void playerPlays(@RequestBody Map<String, Integer> payload) {
       try (Jedis jedis = jedisPool.getResource()) {
           int index = payload.get("index");
           int turn = Integer.parseInt(jedis.get("turn"));
           ObjectMapper mapper = new ObjectMapper();
           
           String p1Json = jedis.get("p1");
           boolean heartsBroken = Boolean.parseBoolean(jedis.get("Hearts_Broken"));
           
           Player p1 = mapper.readValue(p1Json, Player.class);
           Card playedCard = p1.hand[index];
           p1.hand[index] = null;
           
           // Update player's hand in Redis
           p1Json = mapper.writeValueAsString(p1);
           jedis.set("p1", p1Json);
           
           // Update trick in Redis
           String trickJson = jedis.get("trick");
           Card[] trick = mapper.readValue(trickJson, Card[].class);
   
           for (int i = 0; i < 4; i++) {
               if (trick[i] == null) {
                   trick[i] = playedCard;
                   trick[i].setHolder(p1);
                   if (trick[i].getSuitChar() == 'h') {
                       heartsBroken = true;
                   }
                   break;
               }
           }
   
           // Update Hearts_Broken and turn in Redis
           jedis.set("Hearts_Broken", String.valueOf(heartsBroken));
           jedis.set("turn", String.valueOf(turn + 1));
   
           // Update trick in Redis
           String updatedTrickJson = mapper.writeValueAsString(trick);
           jedis.set("trick", updatedTrickJson);
       } catch (Exception e) {
           System.out.println("Exception while playing card: " + e.getMessage());
       }
   }
   


    /*
     * This function fetches the trick number from redis then sends it to the frontend
     */
    //@CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getTrickNumber")
    public Integer getTrickNumber() {
    try (Jedis jedis = jedisPool.getResource()) {
        String trickNumberStr = jedis.get("trick_number");
        if (trickNumberStr != null) {
            return Integer.parseInt(trickNumberStr);
        } else {
            return 0; // Or any default value you prefer
        }
    } catch (NumberFormatException | JedisConnectionException e) {
        System.out.println("Exception while getting trick number from Redis: " + e.getMessage());
        return null;
    }
}




    /*
     * If this function is triggered by  the computer, it will let the computer play 
     * a card and add it to the trick, if the player called it, it will look at the trick 
     * and return a boolean array that will correspond to which of the players cards can be played
     */
   // @CrossOrigin(origins = "http://localhost:3000")
   @PostMapping("/playCard")
   public synchronized Object playCard() throws JsonMappingException, JsonProcessingException {
    try (Jedis jedis = jedisPool.getResource()) {
        ObjectMapper mapper = new ObjectMapper();
        String trickJson = jedis.get("trick");
        Card[] trick = mapper.readValue(trickJson, Card[].class);
        String heartsBrokenJson = jedis.get("Hearts_Broken");
        boolean Hearts_Broken = Boolean.parseBoolean(heartsBrokenJson);
        String gameState = jedis.get("gameState");
        String p1Json = jedis.get("p1");
        String p2Json = jedis.get("p2");
        String p3Json = jedis.get("p3");
        String p4Json = jedis.get("p4");
        Player p1 = mapper.readValue(p1Json, Player.class);
        Player p2 = mapper.readValue(p2Json, Player.class);
        Player p3 = mapper.readValue(p3Json, Player.class);
        Player p4 = mapper.readValue(p4Json, Player.class);
        String turnJson = jedis.get("turn");
        int turn = Integer.parseInt(turnJson);
        String trickNumberJson = jedis.get("trick_number");
        int trick_number = Integer.parseInt(trickNumberJson);

       if(trick_number == 13){
           gameState = "End";
           return null;
       }
       Card play = null;
       int played = 0;
       int num = 0;
       for(int i = 0; i<4;i++){
           if(trick[i]!=null){
               num+=1;
           }
       }
      
       if(num == 4){
           return null;
       }
       if(turn == 1){
           
           gameState = "Play";
           boolean[] valid = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false};
           boolean possible_play = false;
           
           if(num == 0){     
               if(trick_number == 0){
                   valid[0] = true;
                   return valid;
               }  
               if(Hearts_Broken == true){
                   for(int i = 0;i<13;i++){
                       if(p1.hand[i]!=null){
                           valid[i] = true;       
                       }
                   }       
               }
               else{   
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
           return valid;
       }
       else if(turn == 2){
           played = p2.play_card(trick, num, Hearts_Broken, trick_number);
           play = p2.hand[played];
           turn = 3;
           p2.hand[played] = null;
       }
       else if(turn == 3){   
           played = p3.play_card(trick, num, Hearts_Broken, trick_number);
           play = p3.hand[played];
           turn = 4;
           p3.hand[played] = null;
       }
       else{ 
           played = p4.play_card(trick, num, Hearts_Broken, trick_number);
           play = p4.hand[played];
           turn = 1;
           p4.hand[played] = null;
       }
       if(play.suit_char == 'h'){
           Hearts_Broken = true;
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
   catch (IOException | JedisConnectionException e) {
    System.out.println("Exception while processing playCard request: " + e.getMessage());
    return null;
}
}



    /*
     * This function clears out the trick and allocates points to the winner of the trick
     */

    //@CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/clearTrick")
public void clearTrick() {
    try (Jedis jedis = jedisPool.getResource()) {
        ObjectMapper mapper = new ObjectMapper();
        String trickNumberStr = jedis.get("trick_number");
        int trickNumber = trickNumberStr != null ? Integer.parseInt(trickNumberStr) : 0;

        Player p1 = null;
        Player p2 = null;
        Player p3 = null;
        Player p4 = null;
        Card[] trick = null;
        int turn = 0;
        String gameState = null;
        
        try {
            gameState = jedis.get("gameState");
            p1 = mapper.readValue(jedis.get("p1"), Player.class);
            p2 = mapper.readValue(jedis.get("p2"), Player.class);
            p3 = mapper.readValue(jedis.get("p3"), Player.class);
            p4 = mapper.readValue(jedis.get("p4"), Player.class);
            trick = mapper.readValue(jedis.get("trick"), Card[].class);
            turn = Integer.parseInt(jedis.get("turn"));
        } catch (IOException e) {
            System.out.println("Exception while reading from Redis or converting values: " + e.getMessage());
        }

        char suitChar = trick[0].getSuitChar();
        int points = trick[0].getPointValue();
        Player winner = trick[0].getHolder();
        int max = trick[0].getVal();
        trick[0] = null;
        trickNumber += 1;
        
        for(int i = 1; i < 4; i++) {
            if(trick[i].getSuitChar() == suitChar) {
                if(trick[i].getVal() > max) {
                    max = trick[i].getVal();
                    winner = trick[i].getHolder();
                }
            }
            points += trick[i].getPointValue();
        }
        
        if(winner.getName().equals("Player 1")) {
            p1.setScore(p1.getScore() + points);
            turn = 1;
        } else if(winner.getName().equals("Player 2")) {
            p2.setScore(p2.getScore() + points);
            turn = 2;
        } else if(winner.getName().equals("Player 3")) {
            p3.setScore(p3.getScore() + points);
            turn = 3;
        } else {
            p4.setScore(p4.getScore() + points);
            turn = 4;
        }

        for(int i = 0; i < 4; i++) {
            trick[i] = null;
        }
        gameState = "Scoring";
        
        jedis.set("trick_number", Integer.toString(trickNumber));
        jedis.set("gameState", gameState);
        jedis.set("turn", Integer.toString(turn));
        jedis.set("p1", mapper.writeValueAsString(p1));
        jedis.set("p2", mapper.writeValueAsString(p2));
        jedis.set("p3", mapper.writeValueAsString(p3));
        jedis.set("p4", mapper.writeValueAsString(p4));
        jedis.set("trick", mapper.writeValueAsString(trick));
    } catch (IOException e) {
        System.out.println("Exception while writing to Redis or converting values: " + e.getMessage());
    }
}


    /*
     * This function starts a newround by resetting the trick, and the players hands
     */
   // @CrossOrigin(origins = "http://localhost:3000")
   @PostMapping("/startNewRound")
   public synchronized void startNewRound() {
        System.out.println("Starting a new round");
       try (Jedis jedis = jedisPool.getResource()) {
           ObjectMapper mapper = new ObjectMapper();
           Player p1 = mapper.readValue(jedis.get("p1"), Player.class);
           Player p2 = mapper.readValue(jedis.get("p2"), Player.class);
           Player p3 = mapper.readValue(jedis.get("p3"), Player.class);
           Player p4 = mapper.readValue(jedis.get("p4"), Player.class);
           int turn = Integer.parseInt(jedis.get("turn"));
           int roundNumber = Integer.parseInt(jedis.get("round_number"));
           int trickNumber = Integer.parseInt(jedis.get("trick_number"));
           boolean heartsBroken = Boolean.parseBoolean(jedis.get("heartsBroken"));
           Card[] trick = mapper.readValue(jedis.get("trick"), Card[].class);
           Boolean[] validCard = mapper.readValue(jedis.get("validCard"), Boolean[].class);
           String gameState = "Swap";
           
           heartsBroken = false;
           roundNumber += 1;
           if (roundNumber == 5) {
               roundNumber = 1;
           }
           trickNumber = 0;
           Card[] deck = new Card[52];
           p1.ResetHand();
           p2.ResetHand();
           p3.ResetHand();
           p4.ResetHand();
           p1.next = p2;
           p2.next = p3;
           p3.next = p4;
           p4.next = p1;
           make_deck(deck);
           shuffle_and_deal(deck, p1, p2, p3, p4);
           p1.Sort_Hand();
           p2.Sort_Hand();
           p3.Sort_Hand();
           p4.Sort_Hand();
           turn = find_start(p1);
           
           jedis.set("p1", mapper.writeValueAsString(p1));
           jedis.set("p2", mapper.writeValueAsString(p2));
           jedis.set("p3", mapper.writeValueAsString(p3));
           jedis.set("p4", mapper.writeValueAsString(p4));
           jedis.set("turn", Integer.toString(turn));
           jedis.set("round_number", Integer.toString(roundNumber));
           jedis.set("trick_number", Integer.toString(trickNumber));
           jedis.set("heartsBroken", Boolean.toString(heartsBroken));
           jedis.set("trick", mapper.writeValueAsString(trick));
           jedis.set("validCard", mapper.writeValueAsString(validCard));
           jedis.set("gameState", gameState);
       } catch (IOException e) {
           System.out.println("Exception while reading from Redis or converting values: " + e.getMessage());
       }
   }
   


    /*
     * This function fetches the scores from redis then sends it to the frontend
     */
   // @CrossOrigin(origins = "http://localhost:3000")
   @GetMapping("/getScores")
   public int[] getScores() {
       int[] scoreBoard = new int[4];
       try (Jedis jedis = jedisPool.getResource()) {
           ObjectMapper mapper = new ObjectMapper();
           String gameState = jedis.get("gameState");
           Player p1 = mapper.readValue(jedis.get("p1"), Player.class);
           Player p2 = mapper.readValue(jedis.get("p2"), Player.class);
           Player p3 = mapper.readValue(jedis.get("p3"), Player.class);
           Player p4 = mapper.readValue(jedis.get("p4"), Player.class);
           String trickNumberString = jedis.get("trick_number");
           int trickNumber = Integer.parseInt(trickNumberString);
   
           if(trickNumber == 13){
               if(p1.score == 26){
                   p1.score = 0;
                   p2.score = 26;
                   p3.score = 26;
                   p4.score = 26;
               } else if(p2.score == 26){
                   p2.score = 0;
                   p1.score = 26;
                   p3.score = 26;
                   p4.score = 26;
               } else if(p3.score == 26){
                   p3.score = 0;
                   p1.score = 26;
                   p2.score = 26;
                   p4.score = 26;
               } else if(p4.score == 26){
                   p4.score = 0;
                   p1.score = 26;
                   p2.score = 26;
                   p3.score = 26;
               }
   
               p1.overall_score += p1.score;
               p2.overall_score += p2.score;
               p3.overall_score += p3.score;
               p4.overall_score += p4.score;
   
               p1.score = 0;
               p2.score = 0;
               p3.score = 0;
               p4.score = 0;
           }
           scoreBoard[0] = p1.score + p1.overall_score;
           scoreBoard[1] = p2.score + p2.overall_score;
           scoreBoard[2] = p3.score + p3.overall_score;
           scoreBoard[3] = p4.score + p4.overall_score; 
   
           jedis.set("p1", mapper.writeValueAsString(p1));
           jedis.set("p2", mapper.writeValueAsString(p2));
           jedis.set("p3", mapper.writeValueAsString(p3));
           jedis.set("p4", mapper.writeValueAsString(p4));
   
           jedis.set("gameState", "Play");  // Update the gameState in Redis
       } catch (Exception e) {
           System.out.println("Exception while getting scores from Redis: " + e.getMessage());
       }
       return scoreBoard;
   }
   

    /*
     * This function performs the swap of cards between the players
     */

    //@CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/performSwap")
    public void swapCards(@RequestBody int[] swaps) {
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper mapper = new ObjectMapper();
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
    
            for(int i = 0; i < 3; i++) {
                if(swaps[i] == -1) {
                    gameState = "Play";
                    turn = find_start(p1);
                    jedis.set("gameState", gameState);
                    return;
                }   
            }
    
            p1.swap = swaps;
    
            p2.choose_swaps();
            p3.choose_swaps();
            p4.choose_swaps();
    
            Card[] temp = {null, null, null};
    
            if(round % 4 == 1) {
                circle_swaps(p1, p2, p3, p4, temp);
            } else if(round % 4 == 3) {
                across_swaps(p1, p3, temp);
                across_swaps(p2, p4, temp);
            } else {
                circle_swaps(p4, p3, p2, p1, temp);
            }
    
            p1.Sort_Hand();
            gameState = "Play";
            turn = find_start(p1);
            jedis.set("gameState", gameState);
    
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
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }



    /*
     * This function starts a new game by creating the necessary variables and setting the gamestate to swap
     */
   // @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/startGame")
    public void startNewGame(HttpSession session) {
        System.out.println("Attempting to start a new game");

        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper mapper = new ObjectMapper();

            // Set initial game state
            jedis.set("gameState", "Swap");

            // Create players and initialize game variables
            Player p1 = new Player("Player 1");
            Player p2 = new Player("Player 2");
            Player p3 = new Player("Player 3");
            Player p4 = new Player("Player 4");
            int roundNumber = 1;
            int trickNumber = 0;
            String gameState = "Swap";
            boolean heartsBroken = false;
            int turn = 0;
            Boolean[] validCard = new Boolean[13];
            Card[] trick = new Card[4];
            Card[] deck = new Card[52];
            make_deck(deck);
            shuffle_and_deal(deck, p1, p2, p3, p4);
            p1.Sort_Hand();
            p2.Sort_Hand();
            p3.Sort_Hand();
            p4.Sort_Hand();

            // Store game state in Redis
            jedis.set("p1", mapper.writeValueAsString(p1));
            jedis.set("p2", mapper.writeValueAsString(p2));
            jedis.set("p3", mapper.writeValueAsString(p3));
            jedis.set("p4", mapper.writeValueAsString(p4));
            jedis.set("turn", Integer.toString(turn));
            jedis.set("round_number", Integer.toString(roundNumber));
            jedis.set("trick_number", Integer.toString(trickNumber));
            jedis.set("gameState", gameState);
            jedis.set("trick", mapper.writeValueAsString(trick));
            jedis.set("validCard", mapper.writeValueAsString(validCard));
            jedis.set("heartsBroken", Boolean.toString(heartsBroken));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        
    }


    /*
     * This function fetches the Player's hand from redis then sends it to the frontend
     */
     
   // @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getPlayerHand")
    public String[] getPlayerHand() {

        try (Jedis jedis = jedisPool.getResource()) {
 
            ObjectMapper mapper = new ObjectMapper();
            Player p1 = mapper.readValue(jedis.get("p1"), Player.class);
            return p1.getImageUrls();
        } catch (IOException e) {
            System.out.println("Exception while getting player from Redis: " + e.getMessage());

            return new String[0]; 
        }

    }


    /*
     * This function fetches the number of cards in the computer player's hand from redis then sends it to the frontend
     */
     

    @GetMapping("/getComputerHand")
    public int getComputerHand(@RequestParam String playerName) {
        try (Jedis jedis = jedisPool.getResource()) {
            ObjectMapper mapper = new ObjectMapper(); 
            Player player = null;
            try {
                player = mapper.readValue(jedis.get(playerName), Player.class);
            } catch (Exception e) {
                System.out.println("Exception while getting player from Redis: " + e.getMessage());
            }
            int num = 0;
            for(int i = 0; i < 13; i++) {
                if(player != null && player.hand[i] != null) {
                    num += 1;
                }
            }
            return num;
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions as needed
            return 0; // or some default value
        }
    }

    /*
     * This function makes the deck by creating all the cards
     */
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
        deck[36].point_value = 13;
        return;
    }


    /*
     * This function creates the key in which the deck is created based off
     */
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


    /*
     * This function shuffles the deck and deals the cards to the players
     */
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
        p2.Sort_Hand();
        p3.Sort_Hand();
        p4.Sort_Hand();

        


    }


    /*
     * This function performs the swaps either to the left or right between the players
     */
    public static void circle_swaps(Player p1, Player p2, Player p3, Player p4, Card[] temp){
        for(int i = 0;i<3;i++){
            temp[i] = p4.hand[p4.swap[i]];
            p4.hand[p4.swap[i]] = p3.hand[p3.swap[i]];
            p3.hand[p3.swap[i]] = p2.hand[p2.swap[i]];
            p2.hand[p2.swap[i]] = p1.hand[p1.swap[i]];
            p1.hand[p1.swap[i]] = temp[i];
        }
        

    }


    /*
     * This function performs the swaps across the players
     */
    public static void across_swaps(Player p1, Player p2, Card[] temp){
        for(int i = 0;i<3;i++){
            temp[i] = p2.hand[p2.swap[i]];
            p2.hand[p2.swap[i]] = p1.hand[p1.swap[i]];
            p1.hand[p1.swap[i]] = temp[i];
        }
    }


    /*
     * This function figures out which player has the 2 of clubs and will go first 
     */
    public static Integer find_start(Player p1){
        p1.Sort_Hand();
        p1.next.Sort_Hand();
        p1.next.next.Sort_Hand();
        p1.next.next.next.Sort_Hand();
        if(p1.hand[0].val == 2 && p1.hand[0].suit_char == 'c'){
            System.out.println("Player 1 has the 2 of clubs");
            return 1;
        }
        else if(p1.next.hand[0].val == 2 && p1.next.hand[0].suit_char == 'c'){
            System.out.println("Player 2 has the 2 of clubs");
            return 2;
        }
        else if(p1.next.next.hand[0].val == 2 &&   p1.next.next.hand[0].suit_char == 'c'){
            System.out.println("Player 3 has the 2 of clubs");
            return 3;
        }
        else if (p1.next.next.next.hand[0].val == 2 && p1.next.next.next.hand[0].suit_char == 'c'){
            System.out.println("Player 4 has the 2 of clubs");
            return 4;
        }
        else{
            
            return 4;
        }

    }
}
