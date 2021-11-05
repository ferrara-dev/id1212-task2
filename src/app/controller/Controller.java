package app.controller;

import app.model.Game;
import app.repository.GameRepository;
import app.template.GuessTemplate;
import app.template.SuccessTemplate;
import app.template.Template;

import java.util.Objects;

public class Controller {
    private Template gt;
    private Template st;

    public Controller(){
        gt = new GuessTemplate();
        st = new SuccessTemplate();
    }

    public Game startGame(){
        Game game = new Game(0,100);
        GameRepository.insert(game.getId(),game);
        return game;
    }

    public void restartGame(String gameId){

    }

    public String guess(String gameId, int guess){
        Game game = GameRepository.find(gameId);
        if(Objects.nonNull(game)){
            boolean done = game.guessNumber(guess);
            if(!done)
                return new GuessTemplate().generate(game);
            else
                return "";
        }

        throw new IllegalArgumentException("gameId " + gameId + " does not exist" );
    }

    public Template success(String gameId){
        return null;
    }


}
