package app.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    private int min;
    private int max;
    private String id;
    private int number;
    private LinkedList<Integer> guessList;
    private int guesses;
    private boolean done;

    public Game(int min, int max){
        this.min = min;
        this.max = max;
        number = ThreadLocalRandom.current().nextInt(min, max+1);
        done = false;
        id = UUID.randomUUID().toString();
        guessList = new LinkedList<>();
    }

    public Game(String id){
        this.min = min;
        this.max = max;
        number = ThreadLocalRandom.current().nextInt(min, max+1);
        done = false;
        this.id = id;
        guessList = new LinkedList<>();
    }


    public String getId(){
        return id;
    }


    public boolean isDone(){
        return done;
    }

    public int getNumber(){
        return number;
    }

    public int getGuesses() {
        return guesses;
    }

    public int getLatestGuess(){
        return guessList.getLast();
    }

    public boolean guessNumber(int guess){

        if(!done){
            guesses++;
            guessList.addLast(guess);
            if(number == guess){
               done = true;
            }
        }

        return done;
    }

    public void restart(){
        Game g = new Game(this.id);
        this.done = false;
        this.guessList = new LinkedList<>();
        this.number = g.number;
        this.guessList = g.guessList;
        this.guesses = g.guesses;
        this.done = g.done;

    }
}
