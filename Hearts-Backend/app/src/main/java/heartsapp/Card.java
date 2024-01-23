package heartsapp;



// No changes needed for this code block.
public class Card {
    int val; // The value of the
    int suit;   //The suit of the card (1 - c,2 - s,3 - d,4 - h)
    StringBuilder signature;
    Player Holder;  //Which player has the card
    int point_value; //The point value of the card (Hearts are all one, and the Queen of Spades is 13)
    int order;
    boolean played;




//constructor
    public Card(){
        this.val = 0;
        this.suit = 0;

    }

//constructor
    public Card(int v, int s, int p){
        this.val = v;
        this.suit = s;
        this.point_value = p;
        this.order = s*13 + v;
        played = false;

    }

//constructor
    public Card(int v, int s, int p, StringBuilder signature){
        this.val = v;
        this.suit = s;
        this.point_value = p;
        this.order = s*13 + v;
        this.signature = signature;

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
