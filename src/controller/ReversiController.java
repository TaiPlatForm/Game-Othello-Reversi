package controller;

import model.ReversiModel;
import view.ReversiView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReversiController implements ActionListener {
    private ReversiModel model;
    private ReversiView view;

    public ReversiController(ReversiModel model, ReversiView view) {
        this.model = model;
        this.view = view;

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
        if (model.NguoiChoiCoTheDi(LuotTiepTheo)) {
            return;
        }

        // nguoi ke tiep khong di duoc
        String name = (LuotTiepTheo == 1) ? "ĐEN" : "TRẮNG";
        view.showMessage(name + " không còn nước đi hợp lệ! Đổi lượt.");

        // trả lai luot
        model.DoiLuot();
        updateViewFromModel();

        // kiem tra nguoi vua danh co di duoc khong
        int LuotBanDau = model.getLuotChoiHienTai();
        if (!model.NguoiChoiCoTheDi(LuotBanDau)) {
            // ca 2 deu khong di duoc
            GameOver();
        }
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
                model.getWhiteScore());
    }

}