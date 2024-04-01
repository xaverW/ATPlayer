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

package de.p2tools.atplayer;

import de.p2tools.atplayer.controller.audio.LoadAudioFactory;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.gui.AudioGuiPack;
import de.p2tools.atplayer.gui.ProgMenu;
import de.p2tools.atplayer.gui.StatusBarController;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class ATPlayerController extends StackPane {

    private final ProgData progData;
    private final BorderPane borderPane = new BorderPane();

    public ATPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    private void init() {
        try {
            // Toolbar
            final Button btnFilmlist = new Button("Audioliste");
            btnFilmlist.setMinWidth(Region.USE_PREF_SIZE);
            btnFilmlist.getStyleClass().addAll("btnFunction", "btnFunc-4");
            btnFilmlist.setTooltip(new Tooltip("Eine neue Audioliste laden."));
            btnFilmlist.setOnAction(e -> {
                LoadAudioFactory.getInstance().loadListButton();
            });

            HBox hBoxTop = new HBox();
            hBoxTop.setPadding(new Insets(10));
            hBoxTop.setSpacing(10);
            hBoxTop.getChildren().addAll(btnFilmlist, P2GuiTools.getHBoxGrower(), new ProgMenu());

            // Gui zusammenbauen
            borderPane.setTop(hBoxTop);
            borderPane.setCenter(new AudioGuiPack().pack());
            borderPane.setBottom(new StatusBarController(progData));

            this.setPadding(new Insets(0));
            this.getChildren().addAll(borderPane, progData.maskerPane);
            initMaskerPane();
        } catch (Exception ex) {
            P2Log.errorLog(597841023, ex);
        }
    }

    private void initMaskerPane() {
        StackPane.setAlignment(progData.maskerPane, Pos.CENTER);
        progData.maskerPane.setPadding(new Insets(4, 1, 1, 1));
        progData.maskerPane.toFront();
        Button btnStop = progData.maskerPane.getButton();
        progData.maskerPane.setButtonText("");
        btnStop.setGraphic(ProgIcons.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> LoadAudioFactory.getInstance().loadAudioList.setStop(true));
    }

    public void setFocus() {
        progData.audioGuiController.isShown();
    }
}
