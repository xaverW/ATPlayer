/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.atplayer.gui.infopane;


import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class InfoPaneFactory {
    private InfoPaneFactory() {
    }

    public static Tab makeTab(Pane pane, String title, BooleanProperty tabProp, BooleanProperty ripProp) {
        P2ClosePaneH closePaneH = new P2ClosePaneH();
        closePaneH.getButtonClose().setOnAction(a -> tabProp.set(false));
        closePaneH.getButtonRip().setOnAction(a -> ripProp.set(!ripProp.get()));
        closePaneH.addPane(pane);
        Tab tab = new Tab(title);
        tab.setClosable(false);
        tab.setContent(closePaneH);
        return tab;
    }
    
    public static boolean paneIsVisible(ATPlayerController.PANE_SHOWN paneShown, Pane pane) {
        if (ATPlayerController.paneShown != paneShown) {
            return false;

        } else if (!pane.isVisible()) {
            // dann im Extrafenster
            return false;

        } else {
            return true;
        }
    }

    public static boolean isSelPane(VBox vBoxAll, TabPane tabPane, Pane pane) {
        if (!vBoxAll.getChildren().isEmpty() &&
                vBoxAll.getChildren().get(0).equals(pane)) {
            // dann wird nur das angezeigt
            return true;

        } else if (tabPane.getSelectionModel().getSelectedItem() != null &&
                tabPane.getSelectionModel().getSelectedItem().getContent().equals(pane)) {
            // dann ist der Tab ausgewählt
            return true;

        } else {
            return false;
        }
    }
}
