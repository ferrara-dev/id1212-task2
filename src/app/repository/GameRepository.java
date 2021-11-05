package app.repository;

import app.model.Game;

import java.util.HashMap;

public class GameRepository {
    private static HashMap<String, Game> games = new HashMap<>();

    public static Game find(String id){
        return games.get(id);
    }

    public static boolean exists(String id){
        return games.containsKey(id);
    }

    public static boolean insert(String id, Game game){
        boolean e = exists(id);
        if(!e){
            games.put(id,game);
        }
        return e;
    }
}
