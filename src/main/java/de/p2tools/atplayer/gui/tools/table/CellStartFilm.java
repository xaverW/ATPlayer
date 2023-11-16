/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.atplayer.gui.tools.table;

import de.p2tools.atplayer.controller.audio.AudioTools;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.atdata.AudioData;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class CellStartFilm<S, T> extends TableCell<S, T> {

    public final Callback<TableColumn<AudioData, String>, TableCell<AudioData, String>> cellFactory
            = (final TableColumn<AudioData, String> param) -> new TableCell<>() {

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
                setText(null);
                return;
            }

            final HBox hbox = new HBox();
            hbox.setSpacing(4);
            hbox.setAlignment(Pos.CENTER);
            hbox.setPadding(new Insets(0, 2, 0, 2));
            setGraphic(hbox);

            final Button btnPlay;
            btnPlay = new Button("");
            btnPlay.getStyleClass().addAll("btnFunction", "btnFuncTable");
            btnPlay.setGraphic(ProgIcons.IMAGE_TABLE_FILM_PLAY.getImageView());
            btnPlay.setOnAction((ActionEvent event) -> {
                int col = getIndex();
                AudioData film = getTableView().getItems().get(col);
                getTableView().getSelectionModel().clearAndSelect(col);
                AudioTools.playAudio(film);
            });

            final Button btnSave;
            btnSave = new Button("");
            btnSave.getStyleClass().addAll("btnFunction", "btnFuncTable");
            btnSave.setGraphic(ProgIcons.IMAGE_TABLE_FILM_SAVE.getImageView());
            btnSave.setOnAction(event -> {
                int col = getIndex();
                AudioData audioData = getTableView().getItems().get(col);
                getTableView().getSelectionModel().clearAndSelect(col);
                AudioTools.saveAudio(audioData);
            });

            final Button btnBookmark;
            btnBookmark = new Button("");
            btnBookmark.getStyleClass().addAll("btnFunction", "btnFuncTable");
            btnBookmark.setGraphic(ProgIcons.IMAGE_TABLE_FILM_BOOKMARK.getImageView());
            btnBookmark.setOnAction(e -> {
                AudioData film = getTableView().getItems().get(getIndex());
                AudioTools.changeBookmarkFilm(film);
            });

            hbox.getChildren().addAll(btnPlay, btnSave, btnBookmark);
        }
    };
}
