package heartsapp;
import java.util.*;

//main function
public class Main {
    public static void main(String[] args) {
        System.out.println("enter y to play");
        Scanner input = new Scanner(System.in);
        String response = input.next();
        input.close();
        if(!(response.equals("y"))){
            System.out.println("OK, Hope to see you soon!");

        }
        else{
            Player player_1 = new Player("Player 1");
            Player player_2 = new Player("Player 2");
            Player player_3 = new Player("Player 3");
            Player player_4 = new Player("Player 4");
            player_1.next = player_2;
            player_2.next = player_3;
            player_3.next = player_4;
            player_4.next = player_1;

           // Play_Hearts(player_1, player_2, player_3, player_4);
        }

    }

//Plays the game
    public static void Play_Hearts(Player p1, Player p2, Player p3, Player p4){

        Card[] deck = new Card[52];

        int round_number = 0;
        
        print_scoreboard(round_number, p1, p2, p3, p4);
        while(p1.total<100 && p2.total<100 && p3.total<100 && p4.total<100){
            make_deck(deck);
            shuffle_and_deal(deck, p1, p2, p3, p4);

            Play_hand(p1, p2, p3, p4, round_number);
            print_scoreboard(round_number, p1, p2, p3, p4);

            round_number +=1;
        }

        System.out.println("The game is over!!");

    }


//plays a hand of hearts
    public static void Play_hand(Player p1, Player p2, Player p3, Player p4, int round){

        System.out.println("Your hand:");
        p1.print_hand();

        if(round % 4 != 3){
            swap_cards(p1, p2, p3, p4, round);
            System.out.println("Your New Cards:");
            for(int i = 0;i<3;i++){
                System.out.println(p1.hand[p1.swap[i]].signature);
            }

        }
        else{
            System.out.println("No Swaps this turn");
        }

        sort_hand(p1.hand,13);
        sort_hand(p2.hand,13);
        sort_hand(p3.hand,13);
        sort_hand(p4.hand,13);


        System.out.println("Your New Cards Sorted:");

        p1.print_hand();

        p1.Hearts_broken = false;
        Player first = find_start(p1);
        for(int i = 0;i<13;i++){

            Card[] Trick = new Card[]{null, null, null, null};
            first = play_trick(p1, first, p1.Hearts_broken, Trick, i, round);

        }
        System.out.println("Hand finished");
    }

    //find who has the 2C and prompts them to play it
    public static Player find_start(Player p1){

        if(p1.hand[0].val == 2){
            return p1;

        }
        else if(p1.next.hand[0].val == 2){
            return p1.next;
        }
        else if(p1.next.next.hand[0].val == 2){
            return p1.next.next;
        }
        else {
            return p1.next.next.next;
        }

    }

//plays a trick of cards
    public static Player play_trick(Player p1, Player first, boolean Hearts_broken, Card[] Trick, int trick_number, int round){
        int played = 0;

        while(first != p1){
            p1.Hearts_broken = first.play_card(Trick, played, p1.Hearts_broken, trick_number);
            first = first.next;
            played+=1;
        }

        user_plays_card(p1, p1.Hearts_broken, Trick, played, trick_number);
        played+=1;

        first = p1.next;

        while(played<4){

            p1.Hearts_broken = first.play_card(Trick, played, p1.Hearts_broken, trick_number);
            first = first.next;
            played+=1;
        }
        Card winner = Trick[0];
        int point = Trick[0].point_value;
        print_trick(Trick);
        for(int i = 1;i<4; i++){
            point+= Trick[i].point_value;
            if(Trick[i].suit == winner.suit && Trick[i].val>winner.val){
                winner = Trick[i];
            }
        }
        System.out.println(winner.Holder.name + "  Took The trick with: " + winner.signature);
        System.out.println(" ");
        System.out.println(" ");
        winner.Holder.score[round] += point;

        return winner.Holder;

    }

//prompts the user to play a card then plays it
    public static void user_plays_card(Player p1, Boolean Hearts_broken, Card[] Trick, int cards_played, int trick_number){

        if(cards_played==0 && trick_number == 0){
            System.out.println("You start, play 2C");
        }
        else {


            System.out.println("Your Turn, Play a Card");
            p1.print_hand();

        }

        Scanner input = new Scanner(System.in);
        int num = input.nextInt();
        while(!check_card(p1.hand, Trick, num, p1.Hearts_broken)){
            System.out.println("Play another card");
            input = new Scanner(System.in);
            num = input.nextInt();
        }

        Trick[cards_played] = p1.hand[num];
        Trick[cards_played].Holder = p1;

        p1.hand[num].played = true;

        if(!Hearts_broken && p1.hand[num].suit == 16){
            System.out.println("Hearts Have Been Broken");
            p1.Hearts_broken = true;
        }
    }

//checks if the card the user is trying to play, is allowed to be played there
    public static boolean check_card(Card[] Hand, Card[] Trick, int choice, boolean Hearts_broken){

        if(choice>12 || choice<0 || Hand[choice].played){
            System.out.println("That number does not correspond to a card currently in your hand");
            return false;
        }

        if(Trick[0] == null){
            if(!Hearts_broken && Hand[choice].suit == 16){
                for(int i = 0;i<13; i++){
                    if(Hand[i].suit != 16 && !Hand[i].played){
                        System.out.println("Hearts have not been broken");
                        return false;
                    }
                }
            }
            return true;
        }

        if(Trick[0].suit != Hand[choice].suit){
            for(int i =0;i<13;i++){
                if(Hand[i].suit==Trick[0].suit && !Hand[i].played){
                    System.out.println("You must play a card that matches the suit of the trick if you have one");
                    return false;
                }
            }
        }

        return true;
    }

//prints out the trick
    public static void print_trick(Card[] Trick){
        System.out.println("--------------------");
        for(int i = 0;i<4;i++){
            if(Trick[i]==null){
                System.out.println("-");

            }
            else{
                if(Trick[i].point_value!=0){
                    System.out.println(Trick[i].Holder.name + " - " + Trick[i].signature + " (" + Trick[i].point_value + "  Points)");
                }
                else{
                    System.out.println(Trick[i].Holder.name + " - " + Trick[i].signature);
                }

            }
        }
        System.out.println("--------------------");
    }

//shuffles the deck then deals out the cards 13 to each player
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

    }


//sorts hand by suit then value
    public static void sort_hand(Card[] hand, int n)
    {
        int i;
        int j;
        Card key;
        for (i = 1; i < n; i++) {
            key = hand[i];
            j = i - 1;

            while (j >= 0 && hand[j].Card_Number > key.Card_Number) {
                hand[j + 1] = hand[j];
                j = j - 1;
            }
            hand[j + 1] = key;
        }
    }


//Prompts the user to choose cards to give to another player, and gives the user the cards swapped to them
    public static void swap_cards(Player p1, Player p2, Player p3, Player p4, int round){

        System.out.println("What cards would you like to swap: (one at a time)");

        Scanner input = new Scanner(System.in);
        p1.swap[0] = input.nextInt();


        while(!valid_swap(p1.swap[0], -1, -1)){
            input = new Scanner(System.in);
            p1.swap[0] = input.nextInt();
        }


        System.out.println("next swap");

        input = new Scanner(System.in);
        p1.swap[1] = input.nextInt();

        while(!valid_swap(p1.swap[1], p1.swap[0], -1)){
            input = new Scanner(System.in);
            p1.swap[1] = input.nextInt();
        }

        System.out.println("last swap");

        input = new Scanner(System.in);
        p1.swap[2] = input.nextInt();

        while(!valid_swap(p1.swap[2], p1.swap[0], p1.swap[1])){
            input = new Scanner(System.in);
            p1.swap[2] = input.nextInt();
        }


        p2.choose_swaps();
        p3.choose_swaps();
        p4.choose_swaps();

        Card[] temp = {null, null, null};

        if(round % 4 == 1){
            System.out.println("From player 4:");
            circle_swaps(p1, p2, p3, p4, temp);

        }
        else if(round % 4 == 2){
            System.out.println("From player 3:");
            across_swaps(p1, p3, temp);
            across_swaps(p2, p4, temp);
        }
        else{
            System.out.println("From player 2:");
            circle_swaps(p4, p3, p2, p1, temp);
        }

    }

// checks if the number the user entered is a valid card to swap (corresponds to a card that is in the hand and hasn't already been chosen)
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


//swaps cards either clockwise or counterclockwise
    public static void circle_swaps(Player p1, Player p2, Player p3, Player p4, Card[] temp){
        for(int i = 0;i<3;i++){
            temp[i] = p4.hand[p4.swap[i]];
            p4.hand[p4.swap[i]] = p3.hand[p3.swap[i]];
            p3.hand[p3.swap[i]] = p2.hand[p2.swap[i]];
            p2.hand[p2.swap[i]] = p1.hand[p1.swap[i]];
            p1.hand[p1.swap[i]] = temp[i];
        }



    }


    //swaps card with player across
    public static void across_swaps(Player p1, Player p2, Card[] temp){
        for(int i = 0;i<3;i++){
            temp[i] = p2.hand[p2.swap[i]];
            p2.hand[p2.swap[i]] = p1.hand[p1.swap[i]];
            p1.hand[p1.swap[i]] = temp[i];
        }


    }

//prints each round then the total at the bottom, also checks if someone has shot the moon
    public static void print_scoreboard(int round, Player p1, Player p2, Player p3, Player p4 ){
                System.out.println("                    Player 1 (you)    |      Player 2     |      Player 3      |        Player 4      |");
        for(int i = 0;i<round+1;i++){
            System.out.println("                           "+ p1.score[i] +"                    "+ p2.score[i] +"                    " + p3.score[i] +"                       " + p4.score[i]);


        }

        if(p1.score[round] == 26 || p2.score[round] == 26 || p3.score[round] == 26 || p4.score[round] == 26){
            Player shooter  = p1;
            for(int i = 1; i<4;i++){
                if(shooter.score[round]==26){
                    System.out.println(shooter.name + "   Has shot the moon!!");
                    Shoot_the_moon(shooter, round);
                }
                shooter = shooter.next;
            }
            System.out.println("New Scores:");

        }

        p1.total += p1.score[round];
        p2.total += p2.score[round];
        p3.total += p3.score[round];
        p4.total += p4.score[round];
        System.out.print("Totals:                    "+ p1.total    +"                    "+ p2.total    +"                    "+ p3.total     +"                       " + p4.total);
        System.out.println(" ");

    }

//Changes the shooters score to zero and everyone else's score to 26
    public static void Shoot_the_moon(Player shooter, int round){
        shooter.score[round] = 0;
        shooter.next.score[round] = 26;
        shooter.next.next.score[round] = 26;
        shooter.next.next.next.score[round] = 26;
    }


//Initializes the deck of cards
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

//makes the key to assign cards suit and number values
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

}