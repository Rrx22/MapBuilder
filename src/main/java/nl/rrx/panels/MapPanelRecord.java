package nl.rrx.panels;

import javax.swing.JLabel;

public record MapPanelRecord(JLabel label, int row, int col) {

    public boolean sameLocation(int row, int col) {
        return this.row == row && this.col == col;
    }

}
