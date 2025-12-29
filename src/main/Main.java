package main;

import model.ReversiModel;
import view.ReversiView;
import controller.ReversiController;

public class Main {
    public static void main(String[] args) {
        ReversiModel model = new ReversiModel();
        ReversiView view = new ReversiView();
        new ReversiController(model, view);
        view.setVisible(true);
    }
}