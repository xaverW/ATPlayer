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

package de.p2tools.atplayer.gui;

import de.p2tools.atplayer.controller.config.ProgConfig;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MenuController extends ScrollPane {

    public enum StartupMode {
        AUDIO, DOWNLOAD
    }

    public MenuController(StartupMode sm) {
        VBox vBox = new VBox();
        setMinWidth(Region.USE_PREF_SIZE);
        setFitToHeight(true);
        setFitToWidth(true);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setContent(vBox);

        vBox.setPadding(new Insets(5));
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.TOP_CENTER);

        switch (sm) {
            case AUDIO:
                new AudioMenu(vBox).init();
                visibleProperty().bind(ProgConfig.AUDIO_GUI_SHOW_MENU);
                managedProperty().bind(ProgConfig.AUDIO_GUI_SHOW_MENU);
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        ProgConfig.AUDIO_GUI_SHOW_MENU.set(!ProgConfig.AUDIO_GUI_SHOW_MENU.get());
                    }
                });
                break;
            case DOWNLOAD:
                new DownloadMenu(vBox).init();
                visibleProperty().bind(ProgConfig.DOWNLOAD_GUI_SHOW_MENU);
                managedProperty().bind(ProgConfig.DOWNLOAD_GUI_SHOW_MENU);
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        ProgConfig.DOWNLOAD_GUI_SHOW_MENU.set(!ProgConfig.DOWNLOAD_GUI_SHOW_MENU.get());
                    }
                });
                break;
        }
    }
}
