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
import de.p2tools.atplayer.gui.filter.AudioFilterController;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class AudioGui {

    final AudioFilterController audioFilterController;
    final AudioGuiController audioGuiController;
    private final SplitPane splitPane = new SplitPane();
    private boolean bound = false;

    public AudioGui() {
        audioFilterController = new AudioFilterController();
        audioGuiController = new AudioGuiController();
        ProgData.getInstance().audioGuiController = audioGuiController;
    }

    public void closeSplit() {
        ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.get());
    }

    private void setSplit() {
        if (ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.getValue()) {
            splitPane.getItems().clear();
            splitPane.getItems().addAll(audioFilterController, audioGuiController);
            bound = true;
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.AUDIO_GUI_FILTER_DIVIDER);
        } else {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.AUDIO_GUI_FILTER_DIVIDER);
            }
            splitPane.getItems().clear();
            splitPane.getItems().addAll(audioGuiController);
        }
    }

    public HBox pack() {
        final MenuController menuController = new MenuController(MenuController.StartupMode.AUDIO);

        HBox hBox = new HBox();
        hBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        SplitPane.setResizableWithParent(audioFilterController, Boolean.FALSE);

        ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return hBox;
    }
}
