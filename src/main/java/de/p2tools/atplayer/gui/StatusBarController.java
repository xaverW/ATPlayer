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
import de.p2tools.atplayer.controller.audio.LoadAudioFactory;
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
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
        LoadAudioFactory.getInstance().loadAudioList.p2LoadNotifier.addListenerLoadFilmlist(new P2LoadListener() {
            @Override
            public void start(P2LoadEvent event) {
                stopTimer = true;
            }

            @Override
            public void finished(P2LoadEvent event) {
                stopTimer = false;
                setStatusbarIndex();
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_TIMER, StatusBarController.class.getSimpleName()) {
            @Override
            public void pingFx() {
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

//        final int second = progData.audioList.getAge();
//        if (second != 0) {
//            strText += " ||  Alter: ";
//            final int minute = second / 60;
//            String strSecond = String.valueOf(second % 60);
//            String strMinute = String.valueOf(minute % 60);
//            String strHour = String.valueOf(minute / 60);
//            if (strSecond.length() < 2) {
//                strSecond = '0' + strSecond;
//            }
//            if (strMinute.length() < 2) {
//                strMinute = '0' + strMinute;
//            }
//            if (strHour.length() < 2) {
//                strHour = '0' + strHour;
//            }
//            strText += strHour + ':' + strMinute + ':' + strSecond + ' ';
//        }

        // Infopanel setzen
        lblRight.setText(strText);
    }
}
