/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.atplayer.gui.infopane;

import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AudioInfoController extends P2ClosePaneH {

    private PaneAudioInfo paneAudioInfo;
    private Tab tabFilmInfo;
    private final TabPane tabPane = new TabPane();

    private final ProgData progData;

    public AudioInfoController() {
        super(ProgConfig.AUDIO_GUI_DIVIDER_ON, true, true);
        progData = ProgData.getInstance();
        initInfoPane();
    }

    public void setAudioInfos(AudioData film) {
        if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.AUDIO,
                getVBoxAll(), tabPane, paneAudioInfo,
                ProgConfig.AUDIO_GUI_DIVIDER_ON, ProgConfig.AUDIO_PANE_DIALOG_INFO_ON)) {
            paneAudioInfo.setAudioData(film);
        }
    }

    private void initInfoPane() {
        paneAudioInfo = new PaneAudioInfo();
        tabFilmInfo = new Tab("Beschreibung");
        tabFilmInfo.setClosable(false);

        super.getRipProperty().addListener((u, o, n) -> {
            if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneAudioInfo)) {
                setDialogInfo();
            }
        });

        if (ProgConfig.AUDIO_PANE_DIALOG_INFO_ON.getValue()) {
            setDialogInfo();
        }
        ProgConfig.AUDIO_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setTabs()); // kommt beim Ein- und Ausschalten der Fenster
        setTabs();
    }

    private void setDialogInfo() {
        InfoPaneFactory.setDialogInfo(tabFilmInfo, paneAudioInfo, "Infos",
                ProgConfig.AUDIO_PANE_DIALOG_INFO_SIZE, ProgConfig.AUDIO_PANE_DIALOG_INFO_ON,
                ProgConfig.AUDIO_GUI_DIVIDER_ON, ProgData.AUDIO_TAB_ON);
    }

    private void setTabs() {
        int i = 0;

        if (ProgConfig.AUDIO_PANE_DIALOG_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabFilmInfo);
        } else {
            tabFilmInfo.setContent(paneAudioInfo);
            if (!tabPane.getTabs().contains(tabFilmInfo)) {
                tabPane.getTabs().add(i, tabFilmInfo);
            }
            ++i;
        }


        if (i == 0) {
            getVBoxAll().getChildren().clear();
            ProgConfig.AUDIO_GUI_DIVIDER_ON.set(false);
        } else if (i == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getVBoxAll().getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);
        } else {
            // dann gibts mehre Tabs
            getVBoxAll().getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
