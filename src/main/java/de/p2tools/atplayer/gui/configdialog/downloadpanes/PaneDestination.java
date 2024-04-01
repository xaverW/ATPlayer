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


package de.p2tools.atplayer.gui.configdialog.downloadpanes;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgConst;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneDestination {
    private final VBox vBoxAll = new VBox();
    private final TextField txtDestPath = new TextField();
    private final TextField txtDestName = new TextField();
    private final Slider slCut = new Slider();
    private final Slider slCutField = new Slider();
    private final Stage stage;

    public PaneDestination(Stage stage) {
        this.stage = stage;
        make();
    }

    public void close() {
        unBindProgData();
    }

    public void makePane(Collection<TitledPane> result) {
        TitledPane tpConfig = new TitledPane("Speicherziel", vBoxAll);
        result.add(tpConfig);
    }

    private void make() {
        vBoxAll.setFillWidth(true);
        vBoxAll.setPadding(new Insets(P2LibConst.PADDING));
        vBoxAll.setSpacing(25);

        final Button btnFileDest = new Button();
        btnFileDest.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFileDest.setTooltip(new Tooltip("Einen Ordner zum Speichern der Audios auswählen"));
        btnFileDest.setOnAction(event -> P2DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtDestPath));

        final Button btnDestPathReset = new Button();
        btnDestPathReset.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());
        btnDestPathReset.setTooltip(new Tooltip("Die Init-Parameter wieder herstellen"));
        btnDestPathReset.setOnAction(event -> txtDestPath.setText(ProgConfig.DOWNLOAD_FILE_PATH_INIT));

        final Button btnDestReset = new Button();
        btnDestReset.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());
        btnDestReset.setTooltip(new Tooltip("Die Init-Parameter wieder herstellen"));
        btnDestReset.setOnAction(event -> txtDestName.setText(ProgConfig.DOWNLOAD_FILE_NAME_INIT));

        final Button btnHelpDestName = P2Button.helpButton(stage, "Zieldateiname",
                HelpText.PSET_FILE_NAME);

        int row = 0;
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));
        vBoxAll.getChildren().add(gridPane);

        // path/name
        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        gridPane.add(txtDestPath, 1, row);
        gridPane.add(btnDestPathReset, 2, 1);
        gridPane.add(btnFileDest, 3, row);

        gridPane.add(new Label("Zieldateiname:"), 0, ++row);
        gridPane.add(txtDestName, 1, row);
        gridPane.add(btnDestReset, 2, row);
        gridPane.add(btnHelpDestName, 4, row);

        gridPane.add(new Label(), 1, ++row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());

        makeCut(vBoxAll);
        bindProgData();
    }

    private void makeCut(VBox vBox) {
        // cut
        Label lblTxtAll = new Label("Länge\nganzer Dateiname:");
        Label lblSizeAll = new Label();
        Label lblTxtField = new Label("Länge\neinzelne Felder:");
        Label lblSizeField = new Label();

        final Button btnHelpDestSize = P2Button.helpButton(stage, "Länge des Zieldateinamens",
                HelpText.PSET_DEST_FILE_SIZE);

        slCut.setMin(0);
        slCut.setMax(ProgConst.LAENGE_DATEINAME_MAX);
        slCut.setShowTickLabels(true);
        slCut.setMinorTickCount(10);
        slCut.setMajorTickUnit(50);
        slCut.setBlockIncrement(10);
        slCut.setSnapToTicks(true);

        slCutField.setMin(0);
        slCutField.setMax(ProgConst.LAENGE_FELD_MAX);
        slCutField.setShowTickLabels(true);
        slCutField.setMinorTickCount(10);
        slCutField.setMajorTickUnit(50);
        slCutField.setBlockIncrement(10);
        slCutField.setSnapToTicks(true);

        setValueSlider(slCut, lblSizeAll, "");
        slCut.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCut, lblSizeAll, ""));
        setValueSlider(slCutField, lblSizeField, "");
        slCutField.valueProperty().addListener((observable, oldValue, newValue) -> setValueSlider(slCutField, lblSizeField, ""));

        GridPane.setValignment(btnHelpDestSize, VPos.CENTER);
        GridPane.setValignment(lblTxtAll, VPos.CENTER);
        GridPane.setValignment(lblSizeAll, VPos.CENTER);
        GridPane.setValignment(slCutField, VPos.CENTER);
        GridPane.setValignment(lblTxtField, VPos.CENTER);
        GridPane.setValignment(lblSizeField, VPos.CENTER);
        GridPane.setValignment(slCutField, VPos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        vBox.getChildren().add(gridPane);

        int row = 0;
        gridPane.add(lblTxtAll, 0, ++row);//Platz nach oben!
        gridPane.add(slCut, 1, row);
        gridPane.add(lblSizeAll, 2, row);
        gridPane.add(btnHelpDestSize, 3, row);

        ++row;
        gridPane.add(lblTxtField, 0, ++row);
        gridPane.add(slCutField, 1, row);
        gridPane.add(lblSizeField, 2, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(), P2ColumnConstraints.getCcPrefSize());
    }

    private void setValueSlider(Slider sl, Label lb, String pre) {
        int days = (int) sl.getValue();
        lb.setText(pre + (days == 0 ? "Nicht\nbeschränken" : "Auf " + days + "\nZeichen beschränken"));
    }

    public void bindProgData() {
        unBindProgData();
        txtDestPath.textProperty().bindBidirectional(ProgConfig.DOWNLOAD_FILE_PATH);
        if (txtDestPath.getText().isEmpty()) {
            txtDestPath.setText(PSystemUtils.getStandardDownloadPath());
        }
        txtDestName.textProperty().bindBidirectional(ProgConfig.DOWNLOAD_FILE_NAME);
        if (txtDestName.getText().isEmpty()) {
            txtDestName.setText(ProgConfig.DOWNLOAD_FILE_NAME_INIT);
        }

        slCut.valueProperty().bindBidirectional(ProgConfig.SYSTEM_SAVE_MAX_SIZE);
        slCutField.valueProperty().bindBidirectional(ProgConfig.SYSTEM_SAVE_MAX_FIELD);
    }

    void unBindProgData() {
        txtDestPath.textProperty().unbindBidirectional(ProgConfig.DOWNLOAD_FILE_PATH);
        txtDestName.textProperty().unbindBidirectional(ProgConfig.DOWNLOAD_FILE_NAME);
        slCut.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_SAVE_MAX_SIZE);
        slCutField.valueProperty().unbindBidirectional(ProgConfig.SYSTEM_SAVE_MAX_FIELD);
    }
}
