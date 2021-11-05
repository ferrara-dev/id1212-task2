package app.template;

import app.model.Game;

public class SuccessTemplate implements Template{
    public final String URL = "/success";
    private Game game;
    private static final String GUESS_TEMPLATE = "<!DOCTYPE html> <html lang=\"en\"> " +
            "<head> <meta charset=\"UTF-8\"> <title>Title</title> </head> " +
            "<body>" +
            "<p>Congratulations, your guess %s was correct. </p> <p>Attempts : %s</p> " +
            "<form action=\"/\" method=\"post\"> " +
            "<input type=\"submit\" value=\"Restart game\"> " +
            "</form>" +
            "</body>" +
            "</html>";
    public SuccessTemplate(){

    }

    @Override
    public String getURL() {
        return URL;
    }

    public String generate(Game game){
        return String.format(GUESS_TEMPLATE, game.getLatestGuess(), game.getGuesses());
    }
}
