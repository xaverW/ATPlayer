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

import de.p2tools.atplayer.controller.audio.AudioToolsFactory;
import de.p2tools.atplayer.controller.config.PEvents;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class StatusBarController extends AnchorPane {

    private final Label lblSel = new Label();
    private final Label lblLeft = new Label();
    private final Label lblRight = new Label();

    private final HBox hBox;
    private final ProgData progData;
    private boolean stopTimer = false;

    public StatusBarController(ProgData progData) {
        this.progData = progData;

        hBox = getHbox(lblSel, lblLeft, lblRight);
        getChildren().addAll(hBox);
        AnchorPane.setLeftAnchor(hBox, 0.0);
        AnchorPane.setBottomAnchor(hBox, 0.0);
        AnchorPane.setRightAnchor(hBox, 0.0);
        AnchorPane.setTopAnchor(hBox, 0.0);
        make();
    }

    private HBox getHbox(Label lblSel, Label lblLeft, Label lblRight) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        lblSel.setPadding(new Insets(0, 10, 0, 0));
        lblSel.getStyleClass().add("lblSelectedLines");

        lblLeft.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeft, Priority.ALWAYS);

        hBox.getChildren().addAll(lblSel, lblLeft, lblRight);
        hBox.setStyle("-fx-background-color: -fx-background;");
        return hBox;
    }

    private void make() {
        setInfoAudio();
        setTextForRightDisplay();
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_RADIO_LIST_START) {
            @Override
            public void pingGui(P2Event event) {
                stopTimer = true;
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_RADIO_LIST_FINISHED) {
            @Override
            public void pingGui(P2Event event) {
                stopTimer = false;
                setStatusbarIndex();
            }
        });

        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_TIMER) {
            @Override
            public void pingGui() {
                try {
                    if (!stopTimer) {
                        setStatusbarIndex();
                    }
                } catch (final Exception ex) {
                    P2Log.errorLog(936251087, ex);
                }
            }
        });
    }

    public void setStatusbarIndex() {
        setInfoAudio();
        setTextForRightDisplay();
    }

    private void setInfoAudio() {
        lblLeft.setText(AudioToolsFactory.getStatusInfosAudio());
        final int selCount = progData.audioGuiController.getSelCount();
        lblSel.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setTextForRightDisplay() {
        // Text rechts: alter/neuladenIn anzeigen
        String strText = "Liste erstellt: ";
        strText += ProgConfig.SYSTEM_AUDIOLIST_DATE_TIME.getValueSafe();

        // Infopanel setzen
        lblRight.setText(strText);
    }
}
