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
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoController;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoDto;
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
    private final P2InfoController infoControllerFilter;
    private final BooleanProperty boundFilter = new SimpleBooleanProperty(false);

    public AudioGui() {
        audioFilterController = new AudioFilterController();
        audioGuiController = new AudioGuiController();
        ProgData.getInstance().audioGuiController = audioGuiController;

        ArrayList<P2InfoDto> list = new ArrayList<>();
        P2InfoDto infoDto = new P2InfoDto(audioFilterController,
                ProgConfig.AUDIO__FILTER_IS_RIP,
                ProgConfig.AUDIO__FILTER_DIALOG_SIZE, ProgData.AUDIO_TAB_ON,
                "Filter", "Audio", true);
        list.add(infoDto);
        infoControllerFilter = new P2InfoController(list, ProgConfig.AUDIO__FILTER_IS_SHOWING);
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

//        if (bound) {
//            splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.AUDIO_GUI_FILTER_DIVIDER);
//            bound = false;
//        }
//        if (filterPaneDialog != null) {
//            filterPaneDialog.closeSetNoRip();
//            filterPaneDialog = null;
//        }
//        splitPane.getItems().clear();
//
//        if (ProgConfig.AUDIO__FILTER_IS_SHOWING.get()) {
//            if (ProgConfig.AUDIO__FILTER_IS_RIP.get()) {
//
//                filterPaneDialog = new FilterPaneDialog(audioFilterController, "Audiofilter",
//                        ProgConfig.AUDIO__FILTER_DIALOG_SIZE,
//                        ProgConfig.AUDIO__FILTER_IS_RIP,
//                        ProgData.AUDIO_TAB_ON);
//                splitPane.getItems().addAll(audioGuiController);
//
//            } else {
//                P2ClosePaneV closePaneV = new P2ClosePaneV();
//                closePaneV.addPane(audioFilterController);
//                closePaneV.getButtonClose().setOnAction(a -> ProgConfig.AUDIO__FILTER_IS_SHOWING.set(false));
//                closePaneV.getButtonRip().setOnAction(a -> ProgConfig.AUDIO__FILTER_IS_RIP.set(!ProgConfig.AUDIO__FILTER_IS_RIP.get()));
//                SplitPane.setResizableWithParent(closePaneV, Boolean.FALSE);
//
//                splitPane.getItems().addAll(closePaneV, audioGuiController);
//                splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.AUDIO_GUI_FILTER_DIVIDER);
//                bound = true;
//            }
//
//        } else {
//            splitPane.getItems().addAll(audioGuiController);
//        }

    }
}
