package controller;

import javafx.stage.Stage;

public abstract class Controller
{
    protected Stage stage;
    void setStage(Stage stage) { this.stage = stage; }
}
