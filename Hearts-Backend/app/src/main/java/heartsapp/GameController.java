package heartsapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.JedisShardInfo;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@RestController
@SessionAttributes("gameState")
public class GameController {

    /*
     * This function fetches the gamestate from redis then sends it to the frontend
     */
    
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getGameState")
    public String getGameState() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        String gameState = null;
        try {
            gameState = jedis.get("gameState");
            
        } catch (Exception e) {
            System.out.println("Exception while getting gameState from Redis: " + e.getMessage());
        }
        return gameState;
    }


    /*
     * This function fetches the turn from redis then sends it to the frontend
     */

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getturn")
    public Integer getturn() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        Integer turn = null;
        try {
            turn = Integer.parseInt(jedis.get("turn"));
            
        } catch (Exception e) {
            System.out.println("Exception while getting turn from Redis: " + e.getMessage());
        }
        return turn;
    }


    /*
     * This function fetches the trick from redis then sends it to the frontend
     */
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



    /*
     * This function fetches the round number from redis then sends it to the frontend
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getRoundNumber")
    public int getRoundNumber() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        
        String round_numberStr = jedis.get("round_number");
        if (round_numberStr == null) {
            // Handle the case where the round number is not set in Redis
            return -1;
        }
        
        int roundNumber = Integer.parseInt(round_numberStr);
        return roundNumber;
    }
    

    /*
     * This function takes the card that the player has chosen and adds it to the trick and removes it from their hand
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/player_plays")
    public void playerPlays(@RequestBody Map<String, Integer> payload) {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper();
        int turn = 0;

        try {
            int index = payload.get("index");
            turn = Integer.parseInt(jedis.get("turn"));
            String p1Json = jedis.get("p1");
            String heartsBrokenJson = jedis.get("Hearts_Broken");
            boolean Hearts_Broken = Boolean.parseBoolean(heartsBrokenJson);
            Player p1 = mapper.readValue(p1Json, Player.class);   
            Card playedCard = p1.hand[index];
            p1.hand[index] = null;
            p1Json = mapper.writeValueAsString(p1);
            jedis.set("p1", p1Json);
            String trickJson = jedis.get("trick");
            Card[] trick = mapper.readValue(trickJson, Card[].class);

            for(int i = 0;i<4;i++){
                if(trick[i] == null){
                    trick[i] = playedCard;
                    trick[i].Holder = p1;
                    if(trick[i].suit_char == 'h'){
                        Hearts_Broken = true;
                    }
                    break;
                }
            }
            
            turn +=1;
            String updatedTrickJson = mapper.writeValueAsString(trick);
            p1Json = mapper.writeValueAsString(p1);
            jedis.set("p1", p1Json);
            jedis.set("Hearts_Broken", String.valueOf(Hearts_Broken));           
            jedis.set("turn", String.valueOf(turn));
            jedis.set("trick", updatedTrickJson);
        } catch (Exception e) {
            System.out.println("Exception while playing card: " + e.getMessage());
        }
    }


    /*
     * This function fetches the trick number from redis then sends it to the frontend
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getTrickNumber")
    public Integer getTrickNumber() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        int trick_number = Integer.parseInt(jedis.get("trick_number"));
        
        return trick_number;
    }




    /*
     * If this function is triggered by  the computer, it will let the computer play 
     * a card and add it to the trick, if the player called it, it will look at the trick 
     * and return a boolean array that will correspond to which of the players cards can be played
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/playCard")
    public synchronized Object playCard() throws JsonMappingException, JsonProcessingException {
        
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper();
        String trickJson = jedis.get("trick");
        Card[] trick = mapper.readValue(trickJson, Card[].class);
        String heartsBrokenJson = jedis.get("Hearts_Broken");
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
        String trick_numberJson = jedis.get("trick_number");
        boolean Hearts_Broken = Boolean.parseBoolean(heartsBrokenJson);
        int turn = Integer.parseInt(turnJson);
        int trick_number = Integer.parseInt(trick_numberJson);

        System.out.println("Trick before at the start of playcard" );
        for(int  i = 0;i<4;i++){
            if(trick[i] != null){
                System.out.println(trick[i].signature);
            }
            else{
                System.out.println("null");
            }
        }

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



    /*
     * This function clears out the trick and allocates points to the winner of the trick
     */

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/clearTrick")
    public void clearTrick() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        
        Jedis jedis = new Jedis(shardInfo);

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
            e.printStackTrace();
        }
        char suit_char = trick[0].suit_char;
        int points = trick[0].point_value;
        Player winner = trick[0].Holder;
        int max = trick[0].val;
        trick[0] = null;
        trickNumber += 1;
        
        
        for(int i = 1;i<4;i++){
            if(trick[i].suit_char == suit_char){
                if(trick[i].val > max){
                    max = trick[i].val;
                    winner = trick[i].Holder;
                }
            }
            points+=trick[i].point_value;
        }
        
        if(winner.name.equals("Player 1")){
            
            p1.score += points;
            turn = 1;
        }
        else if(winner.name.equals("Player 2")){
           
            p2.score += points;
            turn = 2;
        }
        else if(winner.name.equals("Player 3")){
           
            p3.score += points;
            turn = 3;
        }
        else{
            
            p4.score += points;
            turn = 4;
        }

        for(int i = 0;i<4;i++){
            trick[i] = null;
        }
        gameState = "Scoring";
        

        try {
            jedis.set("trick_number", Integer.toString(trickNumber));
            jedis.set("gameState", gameState);
            jedis.set("turn", Integer.toString(turn));
            jedis.set("p1", mapper.writeValueAsString(p1));
            jedis.set("p2", mapper.writeValueAsString(p2));
            jedis.set("p3", mapper.writeValueAsString(p3));
            jedis.set("p4", mapper.writeValueAsString(p4));
            jedis.set("trick", mapper.writeValueAsString(trick));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
}

    /*
     * This function starts a newround by resetting the trick, and the players hands
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/startNewRound")
    public void startNewRound() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper(); // create a new ObjectMapper
        try {
            Player p1 = mapper.readValue(jedis.get("p1"), Player.class);
            Player p2 = mapper.readValue(jedis.get("p2"), Player.class);
            Player p3 = mapper.readValue(jedis.get("p3"), Player.class);
            Player p4 = mapper.readValue(jedis.get("p4"), Player.class);
            int turn = Integer.parseInt(jedis.get("turn"));
            int round_number = Integer.parseInt(jedis.get("round_number"));
            int trick_number = Integer.parseInt(jedis.get("trick_number"));
            boolean Hearts_Broken = Boolean.parseBoolean(jedis.get("Hearts_Broken"));
            Card[] trick = mapper.readValue(jedis.get("trick"), Card[].class);
            Boolean[] ValidCard = mapper.readValue(jedis.get("ValidCard"), Boolean[].class);
            String gameState = "Swap";
            /*if(round_number % 4 == 0){
                gameState = "Play";
            }*/
            
            
            Hearts_Broken = false;
            round_number +=1;
            if(round_number == 5){
                round_number = 1;
            }
            trick_number = 0; 
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
            jedis.set("round_number", Integer.toString(round_number));
            jedis.set("trick_number", Integer.toString(trick_number));
            jedis.set("Hearts_Broken", Boolean.toString(Hearts_Broken));
            jedis.set("trick", mapper.writeValueAsString(trick));
            jedis.set("ValidCard", mapper.writeValueAsString(ValidCard));
            jedis.set("gameState", gameState);
        } catch (IOException e) {
            System.out.println("Exception while reading from Redis or converting values: " + e.getMessage());
        }
    }


    /*
     * This function fetches the scores from redis then sends it to the frontend
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getScores")
    public int[] getScores() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper();
        int[] scoreBoard = new int[4];
        System.out.println("Getting scores from Redis");

        try {

            String gameState = jedis.get("gameState");
            Player p1 = mapper.readValue(jedis.get("p1"), Player.class);
            Player p2 = mapper.readValue(jedis.get("p2"), Player.class);
            Player p3 = mapper.readValue(jedis.get("p3"), Player.class);
            Player p4 = mapper.readValue(jedis.get("p4"), Player.class);
            
            System.out.println("After fetching players");
            String trickNumberString = jedis.get("trick_number");
            int trickNumber = Integer.parseInt(trickNumberString);

            System.out.println("After fetching scores");

            System.out.println("Trick number" + trickNumber);
            


            if(trickNumber == 13){
                System.out.println("Checking for moon and resetting scores");

                
                    System.out.println("p1 score: " + p1.score);
                    System.out.println("p2 score: " + p2.score);
                    System.out.println("p3 score: " + p3.score);
                    System.out.println("p4 score: " + p4.score);


                    if(p1.score == 26){
                        p1.score = 0;
                        p2.score = 26;
                        p3.score = 26;
                        p4.score = 26;
                    }
                    else if(p2.score == 26){
                        p2.score = 0;
                        p1.score = 26;
                        p3.score = 26;
                        p4.score = 26;
                    }
                    else if(p3.score == 26){
                        p3.score = 0;
                        p1.score = 26;
                        p2.score = 26;
                        p4.score = 26;
                    }
                    else if(p4.score == 26){
                        p4.score = 0;
                        p1.score = 26;
                        p2.score = 26;
                        p3.score = 26;
                    }


                    System.out.println("p1 score: " + p1.score);
                    System.out.println("p2 score: " + p2.score);
                    System.out.println("p3 score: " + p3.score);
                    System.out.println("p4 score: " + p4.score);


                    
                p1.overall_score += p1.score;
                p2.overall_score += p2.score;
                p3.overall_score += p3.score;
                p4.overall_score += p4.score;

                p1.score = 0;
                p2.score = 0;
                p3.score = 0;
                p4.score = 0;



            }

            System.out.println("p1 score: " + p1.score);
            System.out.println("p2 score: " + p2.score);
            System.out.println("p3 score: " + p3.score);
            System.out.println("p4 score: " + p4.score);


            System.out.println("p1 overall score: " + p1.overall_score);
            System.out.println("p2 overall score: " + p2.overall_score);
            System.out.println("p3 overall score: " + p3.overall_score);
            System.out.println("p4 overall score: " + p4.overall_score);


            

            
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
        System.out.println("Scores from Redis: " + Arrays.toString(scoreBoard));
        return scoreBoard;
    }

    /*
     * This function performs the swap of cards between the players
     */

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/performSwap")
    public void swap_cards(@RequestBody int[] swaps) {
        


        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
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

        System.out.println("Before Swapping");

        for(int i = 0; i<13;i++){
            if(p1.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p1.hand[i].signature + " ");
            }
        }
        System.out.println("p2 hand:");
        for(int i = 0; i<13;i++){
            if(p2.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p2.hand[i].signature + " ");
            }
        }
        System.out.println("p3 hand:");
        for(int i = 0; i<13;i++){
            if(p3.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p3.hand[i].signature + " ");
            }
        }
        System.out.println("p4 hand:");
        for(int i = 0; i<13;i++){
            if(p4.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p4.hand[i].signature + " ");
            }
        }
        
        

        

        

        p1.next = p2;
        p2.next = p3;
        p3.next = p4;
        p4.next = p1;  
        

        for(int i = 0;i<3;i++){
            if(swaps[i] == -1){
                
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


        System.out.println("p2 swaps:");
        for(int i = 0;i<3;i++){
            System.out.print(p2.swap[i] + " ");
        }
        System.out.println("p3 swaps:");
        for(int i = 0;i<3;i++){
            System.out.print(p3.swap[i] + " ");
        }
        System.out.println("p4 swaps:");
        for(int i = 0;i<3;i++){
            System.out.print(p4.swap[i] + " ");
        }


        





        
        Card[] temp = {null, null, null};

        if(round % 4 == 1){
            circle_swaps(p1, p2, p3, p4, temp);
        }
        else if(round % 4 == 3){
            across_swaps(p1, p3, temp);
            across_swaps(p2, p4, temp);
        }
        else{
            circle_swaps(p4, p3, p2, p1, temp);
        }
        p1.Sort_Hand();
        gameState = "Play";
        turn = find_start(p1);

       System.out.println("After Swapping:");

        HashSet<StringBuilder> Deck_Check = new HashSet<>();

        for(int i = 0; i<13;i++){
            if(p1.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p1.hand[i].signature + " ");
            }
            if(Deck_Check.contains(p1.hand[i].signature)){
                System.out.println("Duplicate card found in p1's hand");
                System.exit(0); 
            }
            Deck_Check.add(p1.hand[i].signature);
        }
        System.out.println("p2 hand:");
        for(int i = 0; i<13;i++){
            if(p2.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p2.hand[i].signature + " ");
            }
            if(Deck_Check.contains(p2.hand[i].signature)){
                System.out.println("Duplicate card found in p1's hand");
                System.exit(0); 
            }
            Deck_Check.add(p2.hand[i].signature);
        }
        System.out.println("p3 hand:");
        for(int i = 0; i<13;i++){
            if(p3.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p3.hand[i].signature + " ");
            }
            if(Deck_Check.contains(p3.hand[i].signature)){
                System.out.println("Duplicate card found in p1's hand");
                System.exit(0); 
            }
            Deck_Check.add(p3.hand[i].signature);
        }
        System.out.println("p4 hand:");
        for(int i = 0; i<13;i++){
            if(p4.hand[i] == null){
                System.out.print("Null ");
            }
            else{
                System.out.print(p4.hand[i].signature + " ");
            }
            if(Deck_Check.contains(p4.hand[i].signature)){
                System.out.println("Duplicate card found in p1's hand");
                System.exit(0); 
            }
            Deck_Check.add(p4.hand[i].signature);
        }
        
   
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
    }



    /*
     * This function starts a new game by creating the necessary variables and setting the gamestate to swap
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/startGame")
    public void startNewGame(HttpSession session) {
        System.out.println("Starting a new game");
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
       // for(int i = 0;i<52;i++){
       //     System.out.println(deck[i].signature);
       // }
        shuffle_and_deal(deck, p1, p2, p3, p4); 
        p1.Sort_Hand();
        p2.Sort_Hand();
        p3.Sort_Hand();
        p4.Sort_Hand();


        
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


    /*
     * This function fetches the Player's hand from redis then sends it to the frontend
     */
     
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/getPlayerHand")
    public String[] getPlayerHand() {
        JedisShardInfo shardInfo = new JedisShardInfo("localhost");
        Jedis jedis = new Jedis(shardInfo);
        ObjectMapper mapper = new ObjectMapper(); // create a new ObjectMapper
    Player p1 = null;
    try {
        p1 = mapper.readValue(jedis.get("p1"), Player.class);
    } catch (Exception e) {
        System.out.println("Exception while getting player from Redis: " + e.getMessage());
    }
    return p1.getImageUrls();
    }


    /*
     * This function fetches the number of cards in the computer player's hand from redis then sends it to the frontend
     */
     
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
            return 1;
        }
        else if(p1.next.hand[0].val == 2 && p1.next.hand[0].suit_char == 'c'){
            return 2;
        }
        else if(p1.next.next.hand[0].val == 2 &&   p1.next.next.hand[0].suit_char == 'c'){
            return 3;
        }
        else if (p1.next.next.next.hand[0].val == 2 && p1.next.next.next.hand[0].suit_char == 'c'){
            return 4;
        }
        else{
            System.out.println("Nobody has the card");
            return 4;
        }
    }
}
