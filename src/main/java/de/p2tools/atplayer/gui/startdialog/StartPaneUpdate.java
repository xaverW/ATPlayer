/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.p2tools.atplayer.gui.startdialog;


import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartPaneUpdate extends VBox {
    private final Stage stage;
    private final P2ToggleSwitch tglSearch = new P2ToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");

    public StartPaneUpdate(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        tglSearch.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SEARCH_UPDATE);
    }

    public void makeStart() {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        //einmal am Tag Update suchen
        tglSearch.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SEARCH_UPDATE);


        final Button btnHelp = P2Button.helpButton(stage, "Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. Wenn es " +
                        "eine neue Version gibt, wird das mit einer Nachricht mitgeteilt. Es wird nicht " +
                        "automatisch das Programm verändert.");

        gridPane.add(tglSearch, 0, 0);
        gridPane.add(btnHelp, 1, 0);
        gridPane.getColumnConstraints().addAll(de.p2tools.p2lib.guitools.grid.P2GridConstraints.getCcComputedSizeAndHgrow());

        getChildren().addAll(StartFactory.getTitle("Programmupdate"), gridPane);
    }
}
