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
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneDto;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;

public class AudioGui {

    final AudioFilterController audioFilterController;
    final AudioGuiController audioGuiController;
    private final SplitPane splitPane = new SplitPane();
    private final P2ClosePaneController infoControllerFilter;
    private final BooleanProperty boundFilter = new SimpleBooleanProperty(false);

    public AudioGui() {
        audioFilterController = new AudioFilterController();
        audioGuiController = new AudioGuiController();
        ProgData.getInstance().audioGuiController = audioGuiController;

        ArrayList<P2ClosePaneDto> list = new ArrayList<>();
        P2ClosePaneDto infoDto = new P2ClosePaneDto(audioFilterController,
                ProgConfig.AUDIO__FILTER_IS_RIP,
                ProgConfig.AUDIO__FILTER_DIALOG_SIZE, ProgData.AUDIO_TAB_ON,
                "Filter", "Audio", true,
                ProgData.getInstance().maskerPane.visibleProperty());
        list.add(infoDto);
        infoControllerFilter = new P2ClosePaneController(list, ProgConfig.AUDIO__FILTER_IS_SHOWING);
    }

    public HBox pack() {
        final MenuController menuController = new MenuController(MenuController.StartupMode.AUDIO);

        HBox hBox = new HBox();
        HBox.setHgrow(splitPane, Priority.ALWAYS);
        hBox.getChildren().addAll(splitPane, menuController);

        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ProgConfig.AUDIO__FILTER_IS_SHOWING.addListener((observable, oldValue, newValue) -> setSplit());
        ProgConfig.AUDIO__FILTER_IS_RIP.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        return hBox;
    }

    private void setSplit() {
        P2ClosePaneFactory.setSplit(boundFilter, splitPane,
                infoControllerFilter, true, audioGuiController,
                ProgConfig.AUDIO__FILTER_DIVIDER, ProgConfig.AUDIO__FILTER_IS_SHOWING);
    }
}
