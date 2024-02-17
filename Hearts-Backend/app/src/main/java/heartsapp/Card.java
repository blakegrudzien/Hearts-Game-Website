package heartsapp;

public class Card {
    int val; // The value of the
    int suit;   //The suit of the card (1 - c,2 - s,3 - d,4 - h)
    StringBuilder signature;
    Player Holder;  //Which player has the card
    int point_value; //The point value of the card (Hearts are all one, and the Queen of Spades is 13)
    int Card_Number;
    boolean played;
    String imageURL;
    char suit_char;



    //Getters and Setter for every instance variable


    public int getVal() {
        return this.val;
    }

    public int getSuit() {
        return this.suit;
    }

    public StringBuilder getSignature() {
        return this.signature;
    }

    public Player getHolder() {
        return this.Holder;
    }

    public int getPointValue() {
        return this.point_value;
    }

    public int getCardNumber() {
        return this.Card_Number;
    }

    public boolean isPlayed() {
        return this.played;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public char getSuitChar() {
        return this.suit_char;
    }

    public void setPointValue(int point_value) {
        this.point_value = point_value;
    }

    public void setHolder(Player Holder) {
        this.Holder = Holder;
    }

    public void setCardNumber(int Card_Number) {
        this.Card_Number = Card_Number;
    }

    public void setSuitChar(char suitChar) {
        this.suit_char = suitChar;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }


//constructor
    public Card(){
        this.val = 0;
        this.suit = 0;
        imageURL = null;

    }

//constructor
    public Card(int v, int s, int p){
        this.val = v;
        this.suit = s;
        this.point_value = p;
        this.Card_Number= s*13 + v;
        if(s == 13){
            this.suit_char = 'c';
        }
        else if(s == 14){
            this.suit_char = 'd';
        }
        else if(s == 15){
            this.suit_char = 's';
        }
        else if(s == 16){
            this.suit_char = 'h';
        }

        played = false;
        if(this.val<10){
            imageURL = "/Cards/Modern/" + this.suit_char + "0" + this.val + ".jpg";
        }
        else if(this.val == 14){
            imageURL = "/Cards/Modern/" + this.suit_char + "01.jpg";
            
        }
        else{
            imageURL = "/Cards/Modern/" + this.suit_char + this.val + ".jpg";
        }
    }


//constructor
    public Card(int v, int s, int p, StringBuilder signature){
        this.val = v;
        this.suit = s;
        this.point_value = p;
        this.Card_Number = s*13 + v;
        this.signature = signature;
        imageURL = null;
        if(s == 13){
            this.suit_char = 'c';
        }
        else if(s == 14){
            this.suit_char = 'd';
        }
        else if(s == 15){
            this.suit_char = 's';
        }
        else if(s == 16){
            this.suit_char = 'h';

        }
        if(this.val<10){
            imageURL = "/Cards/Modern/" + this.suit_char + "0" + this.val + ".jpg";
        }
        else if(this.val == 14){
            imageURL = "/Cards/Modern/" + this.suit_char + "01.jpg";
        }
        else{
            imageURL = "/Cards/Modern/" + this.suit_char + this.val + ".jpg";
        }
    
    }

//adds the entered signature
    public void add_sig(StringBuilder sig){
        this.signature = sig;
    }

    //adds points to the card
    public void add_point(int p){
        this.point_value = p;

    }

}
