package app.template;

import app.model.Game;

public interface Template {
     String generate(Game g);
     String getURL();
}
