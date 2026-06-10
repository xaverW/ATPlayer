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

package de.p2tools.atplayer.gui.startdialog;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgConst;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StartPaneAudio extends VBox {

    private final Slider slDays = new Slider();
    private final Slider slDuration = new Slider();
    private final Label lblDays = new Label("");
    private final Label lblDuration = new Label("");
    private final Stage stage;
    private final ProgData progData;

    public StartPaneAudio(Stage stage) {
        this.stage = stage;
        this.progData = null;
    }

    public void close() {
        slDays.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDuration.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
    }

    public void make() {
        final Button btnHelpDays = P2Button.helpButton(stage, "Audioliste beim Laden filtern",
                HelpText.LOAD_ONLY_FILMS);
        initSlider();

        final TilePane tilePaneSender = new TilePane();
        tilePaneSender.setHgap(10);
        tilePaneSender.setVgap(10);

        final VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));

        int row = 0;
        gridPane.add(new Label("Nur Audios der letzten Tage laden:"), 0, row, 2, 1);
        gridPane.add(new Label("Audios laden:"), 0, ++row);
        gridPane.add(slDays, 1, row);
        gridPane.add(lblDays, 2, row);
        gridPane.add(btnHelpDays, 3, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("Nur Audios mit Mindestlänge laden:"), 0, ++row, 2, 1);
        gridPane.add(new Label("Audios laden:"), 0, ++row);
        gridPane.add(slDuration, 1, row);
        gridPane.add(lblDuration, 2, row);

        gridPane.getColumnConstraints().addAll(de.p2tools.p2lib.guitools.grid.P2GridConstraints.getCcPrefSize(),
                de.p2tools.p2lib.guitools.grid.P2GridConstraints.getCcPrefSize(),
                de.p2tools.p2lib.guitools.grid.P2GridConstraints.getCcComputedSizeAndHgrow(),
                de.p2tools.p2lib.guitools.grid.P2GridConstraints.getCcPrefSize());
        vBox.getChildren().add(gridPane);


        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(tilePaneSender);
        HBox.setHgrow(tilePaneSender, Priority.ALWAYS);
        vBox.getChildren().addAll(hBox);

        getChildren().addAll(StartFactory.getTitle("Audioliste bereits beim Laden filtern"), vBox);
    }

    private void initSlider() {
        slDays.setMin(0);
        slDays.setMax(ProgConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDays.setShowTickLabels(false);
        slDays.setMajorTickUnit(20);
        slDays.setBlockIncrement(4);

        slDays.valueProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        slDays.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        slDuration.setMin(0);
        slDuration.setMax(ProgConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
        slDuration.setShowTickLabels(false);
        slDuration.setMajorTickUnit(10);
        slDuration.setBlockIncrement(1);

        slDuration.valueProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION);
        slDuration.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider());

        setValueSlider();
    }

    private void setValueSlider() {
        int days = (int) slDays.getValue();
        lblDays.setText(days == 0 ? "Alles laden" : "Nur Audios der letzten " + days + " Tage");

        int duration = (int) slDuration.getValue();
        lblDuration.setText(duration == 0 ? "Alles laden" : "Nur Audios mit mindestens " + duration + " Minuten Länge");
    }
}
