package main;

import model.ReversiModel;
import view.ReversiView;
import controller.ReversiController;

public class Main {
    public static void main(String[] args) {
        ReversiModel model = new ReversiModel();
        ReversiView view = new ReversiView();
        ReversiController controller = new ReversiController(model, view);
        view.setVisible(true);
        controller.setAiEnabled(true); // Tắt AI (chơi 2 người)
        controller.setAiPlayer(ReversiModel.WHITE);
    }
}