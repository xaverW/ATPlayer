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
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AudioInfoController extends VBox {

    private final TabPane tabPane = new TabPane();
    private PaneAudioInfo paneAudioInfo;

    public AudioInfoController() {
        initInfoPane();
    }

    public void setAudioInfos(AudioData film) {
        if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.AUDIO, paneAudioInfo)) {
            paneAudioInfo.setAudioData(film);
        }
    }

    public boolean arePanesShowing() {
        return !ProgConfig.AUDIO_PANE_INFO_IS_RIP.getValue();
    }

    private void initInfoPane() {
        paneAudioInfo = new PaneAudioInfo(ProgConfig.AUDIO_PANE_INFO_DIVIDER);

        if (ProgConfig.AUDIO_PANE_INFO_IS_RIP.getValue()) {
            dialogInfo();
        }
        ProgConfig.AUDIO_PANE_INFO_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfo();
            } else {
                ProgConfig.AUDIO_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        setTabs();
    }

    private void dialogInfo() {
        new InfoPaneDialog(paneAudioInfo, "Infos",
                ProgConfig.AUDIO_PANE_DIALOG_INFO_SIZE,
                ProgConfig.AUDIO_PANE_INFO_IS_RIP,
                ProgData.AUDIO_TAB_ON);
    }

    private void setTabs() {
        tabPane.getTabs().clear();

        if (!ProgConfig.AUDIO_PANE_INFO_IS_RIP.get()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneAudioInfo, "Infos", ProgConfig.AUDIO_INFO_TAB_IS_SHOWING, ProgConfig.AUDIO_PANE_INFO_IS_RIP));
        }

        if (tabPane.getTabs().isEmpty()) {
            // keine Tabs

        } else if (tabPane.getTabs().size() == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);

        } else {
            // dann gibts mehre Tabs
            getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
