package app.template;

import app.model.Game;

public class GuessTemplate implements Template {
    public final String URL = "/guess";
    private Game game;
    private static final String GUESS_TEMPLATE = "<!DOCTYPE html> <html lang=\"en\"> " +
            "<head> <meta charset=\"UTF-8\"> <title>Title</title> </head> " +
            "<body>" +
            " <p>Sorry, you need to guess %s. </p> <p>Attempts so far : %s</p> " +
            "<form action=\"/guess\" method=\"post\"> " +
            "<label for=\"guess\">First name:</label> " +
            "<input type=\"number\" id=\"guess\" name=\"guess\">" +
            "<input type=\"submit\" value=\"Submit\"> " +
            "</form>" +
            "</body>" +
            "</html>";

    public GuessTemplate(){

    }

    @Override
    public String getURL() {
        return URL;
    }

    public  String generate(Game game){
        if(game.getNumber() > game.getLatestGuess()){
           return String.format(GUESS_TEMPLATE, "higher", game.getGuesses());
        }
        else
            return String.format(GUESS_TEMPLATE,"lower", game.getGuesses());
    }
}
