package controller;

import model.ReversiModel;
import model.ReversiAI;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ReversiController implements ActionListener {
    private ReversiModel model;
    private ReversiView view;
    private ReversiAI ai;
    private boolean aiEnabled = true; // Bật/tắt AI
    private int aiPlayer = ReversiModel.WHITE; // AI chơi màu TRẮNG

    public ReversiController(ReversiModel model, ReversiView view) {
        this.model = model;
        this.view = view;
        this.ai = new ReversiAI(aiPlayer);

        // dang ky su kien
        this.view.addGameListener(this);

        // cap nhat view lan dau
        updateViewFromModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String[] coords = command.split(",");
        int row = Integer.parseInt(coords[0]);
        int col = Integer.parseInt(coords[1]);

        // thuc hien nuoc di
        boolean DatCoThanhCong = model.DatQuanCo(row, col);

        if (DatCoThanhCong) {
            updateViewFromModel();

            // xu ly sau khi di
            XuLyLuotTiepTheo();
        }
    }

    private void XuLyLuotTiepTheo() {
        int LuotTiepTheo = model.getLuotChoiHienTai();

        // kiem tra nguoi ke tiep co di duoc khong
        if (!model.CoNuocDiHopLe(LuotTiepTheo)) {
            // nguoi ke tiep khong di duoc
            String name = (LuotTiepTheo == ReversiModel.BLACK) ? "ĐEN" : "TRẮNG";
            view.showMessage(name + " không còn nước đi hợp lệ! Đổi lượt.");

            // trả lai luot
            model.DoiLuot();
            updateViewFromModel();

            // kiem tra nguoi vua danh co di duoc khong
            int LuotBanDau = model.getLuotChoiHienTai();
            if (!model.CoNuocDiHopLe(LuotBanDau)) {
                // ca 2 deu khong di duoc
                GameOver();
                return;
            }
        }

        // Nếu đến lượt AI thì AI tự động đi
        if (aiEnabled && model.getLuotChoiHienTai() == aiPlayer) {
            aiMove();
        }
    }

    // AI thực hiện nước đi
    private void aiMove() {
        // Dùng Timer để delay một chút, tránh AI đi ngay lập tức
        Timer timer = new Timer(500, e -> {
            // Tìm nước đi tốt nhất
            int[] bestMove = ai.findBestMove(model.getBoard());

            if (bestMove != null) {
                int row = bestMove[0];
                int col = bestMove[1];

                // Thực hiện nước đi
                boolean success = model.DatQuanCo(row, col);

                if (success) {
                    updateViewFromModel();
                    XuLyLuotTiepTheo();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void GameOver() {
        String result = model.getGameResult();
        view.showMessage("TRÒ CHƠI KẾT THÚC!\n" + result);

        // hoi choi lai khong
        int choice = javax.swing.JOptionPane.showConfirmDialog(
                view, "Bạn có muốn chơi lại không?", "Game Over",
                javax.swing.JOptionPane.YES_NO_OPTION);

        if (choice == javax.swing.JOptionPane.YES_OPTION) {
            model.resetGame();
            updateViewFromModel();
        } else {
            System.exit(0);
        }
    }

    private void updateViewFromModel() {
        view.updateView(
                model.getBoard(),
                model.getLuotChoiHienTai(),
                model.getBlackScore(),
                model.getWhiteScore(),
                model.getValidMoves(model.getLuotChoiHienTai()));
    }

    // Bật/tắt AI
    public void setAiEnabled(boolean enabled) {
        this.aiEnabled = enabled;
    }

    // Đổi màu AI chơi
    public void setAiPlayer(int player) {
        this.aiPlayer = player;
        this.ai = new ReversiAI(player);
    }
}
