package heartsapp;

import java.util.List;

public class Player {
int total;
String name;
int score = 0;
Card[] hand = {null, null, null, null, null, null, null, null, null, null, null, null, null};
Player next;
int[] swap = {0,-1,-1};
boolean Hearts_broken;
String[] imageUrls;


public Player() {
}

Player(String n){
    this.name = n;

}

public void ResetHand(){
    for(int i = 0; i<13;i++){
        this.hand[i] = null;
    }
}

// Getter and setter for name
    public String getName() {
        return name;
    }

 public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    

public String[] getImageUrls() {
    // Initialize an array with the URLs
    String[] imageUrls = new String[13];

    // Fill the array with URLs
    for (int i = 0; i < 13; i++) {
        if(this.hand[i] == null){
            imageUrls[i] = null;
            continue;
        }
        Card Cur = this.hand[i];
        if (Cur.val < 10) {
            imageUrls[i] = "/images/Cards/Modern/" + Cur.suit_char + "0" + Cur.val + ".jpg";
        } else if (Cur.val == 14){

            imageUrls[i] = "/images/Cards/Modern/" + Cur.suit_char + "01.jpg";

        }
        else{
            imageUrls[i] = "/images/Cards/Modern/" + Cur.suit_char + Cur.val + ".jpg";

        }
    }

    return imageUrls;
}

public Card[] getHand() {
    return this.hand;
}

public void setImageUrls(String[] imageUrls) {
    this.imageUrls = imageUrls;
}



//chooses the swaps from the computer, opting for the highest valued cards
    public void choose_swaps() {
        int max = this.hand[0].val;

        for(int i = 0; i<3;i++){
            for(int j = 0;j<13;j++){
                if(this.hand[j].val> max){
                    if(j!=this.swap[0] && j!=this.swap[1]) {
                        this.swap[i] = j;
                        max = this.hand[j].val;
                    }
                }

            }
            max = 0;
        }

    }

//prints out the player's hand
    public void print_hand(){
        for(int i = 0;i<13;i++) {
            if(!(this.hand[i].played)) {
                System.out.print("    " + this.hand[i].signature + "(" + i + ")");
            }
        }
        System.out.println(" ");
    }

//the computer plays a card, with simple but strong strategy
    public Integer play_card(Card[] Trick, int cards_played, boolean Hearts_broken, int trick_number){
        int min = 0;
        while(this.hand[min] == null){
            min++;
        }
        
        int i = 0;
        boolean valid = false;
        int min_val = 15;

        if(cards_played == 0){
            //If starting the trick, selects the lowest valued card in the hand
            if(trick_number!=0){


                for (i = 0; i < 13; i++) {
                    if (this.hand[i] !=null && this.hand[i].val < min_val ) {
                        if (Hearts_broken || this.hand[i].suit != 16) { //doesn't let computer play a heart if hearts are not broken

                            min = i;
                            min_val = this.hand[i].val;
                        }
                    }
                }
            }
        }
        else{
            min = -1;
            //if the computer isn't starting the trick, it plays the lowest valued card of the given suit
            for(i = 0;i<13;i++){
                if(this.hand[i]!= null && this.hand[i].suit==Trick[0].suit ){
                    min = i;
                    break;
                }
            }
            //if the computer does not have a card in the given suit, it will play the card with the most points, then the highest value
            if(min == -1){
                min = 0;
                int max_points = -1;
                int max_val = 0;
                for(i = 0;i<13;i++){
                    if(this.hand[i]!= null && this.hand[i].point_value > max_points){
                        min = i;
                        max_points = this.hand[min].point_value;
                    }
                }
                if(max_points == 0){
                    for(i = 0;i<13;i++){
                        if(this.hand[i] != null && this.hand[i].val > max_val ){
                            min = i;
                            max_val = this.hand[min].val;
                        }
                    }
                }
            }
        }
        Trick[cards_played] = this.hand[min];

        if(!Hearts_broken && this.hand[min].suit == 16){
            System.out.println("Hearts Have Been Broken");
            Hearts_broken = true;
        }
        System.out.println(this.name + " -  " + this.hand[min].signature);


        //this.hand[min] = null;
        Trick[cards_played].Holder = this;

        return min;
    }


    

    public void Sort_Hand(){
        int i, j;
        Card key;
        
        for (i = 1; i < 13; i++) {
            key = this.hand[i];
            j = i - 1;
 
        // Move elements of arr[0..i-1],
        // that are greater than key, 
        // to one position ahead of their
        // current position
        while (j >= 0 && this.hand[j].Card_Number > key.Card_Number) {
            this.hand[j + 1] = this.hand[j];
            j = j - 1;
        }
        this.hand[j + 1] = key;
    }
        
    }

}




