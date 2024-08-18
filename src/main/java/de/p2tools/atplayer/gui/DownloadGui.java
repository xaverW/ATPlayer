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

package de.p2tools.atplayer.gui;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.filter.DownloadFilterController;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class DownloadGui {

    final DownloadFilterController downloadFilterController;
    final DownloadGuiController downloadGuiController;
    private final SplitPane splitPane = new SplitPane();
    private boolean bound = false;

    public DownloadGui() {
        downloadFilterController = new DownloadFilterController();
        downloadGuiController = new DownloadGuiController();
        ProgData.getInstance().downloadGuiController = downloadGuiController;
    }

    public void closeSplit() {
        ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.get());
    }

    public HBox pack() {
        final MenuController menuController = new MenuController(MenuController.StartupMode.DOWNLOAD);

        HBox hBox = new HBox();
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(downloadFilterController, Boolean.FALSE);
        splitPane.getItems().addAll(downloadFilterController, downloadGuiController);
        ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return hBox;
    }


    private void setSplit() {
        if (ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.getValue()) {
            splitPane.getItems().clear();
            splitPane.getItems().addAll(downloadFilterController, downloadGuiController);
            bound = true;
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER);
        } else {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER);
            }
            splitPane.getItems().clear();
            splitPane.getItems().addAll(downloadGuiController);
        }
    }
}
