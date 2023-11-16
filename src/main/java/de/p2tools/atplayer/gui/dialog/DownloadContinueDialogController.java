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

package de.p2tools.atplayer.gui.dialog;

import de.p2tools.atplayer.controller.config.ProgColorList;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.atplayer.controller.data.download.DownloadTools;
import de.p2tools.atplayer.controller.downloadtools.DownloadState;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DownloadContinueDialogController extends PDialogExtra {

    private final ProgData progData;
    private final DownloadData download;
    private final boolean directDownload;
    private final Label lblHeader = new Label("Die Filmdatei existiert bereits.");
    private final Button btnRestartDownload = new Button("neu _Starten");
    private final Button btnCancel = new Button("_Abbrechen");
    private final Button btnContinueDownload = new Button("_Weiterführen");
    private final CheckBox chkSave = new CheckBox("Merken");
    private final Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private final TextField txtFileName = new TextField("");
    private final ComboBox<String> cbPath = new ComboBox<>();
    private final Label lblSizeFree = new Label("");
    private final Button btnPath = new Button("");
    private final GridPane gridPane = new GridPane();
    private DownloadState.ContinueDownload result = DownloadState.ContinueDownload.CANCEL_DOWNLOAD;
    private final String oldPathFile;

    private Timeline timeline = null;
    private Integer timeSeconds = ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS.getValue();

    public DownloadContinueDialogController(StringProperty conf, ProgData progData,
                                            DownloadData download, boolean directDownload) {
        super(progData.primaryStage, conf, "Download weiterführen", true, false);

        this.progData = progData;
        this.download = download;
        this.directDownload = directDownload;
        this.oldPathFile = download.getDestPathFile();
        init(true);
    }

    public DownloadState.ContinueDownload getResult() {
        return result;
    }

    public boolean isNewName() {
        switch (ProgramToolsFactory.getOs()) {
            case LINUX:
                return !oldPathFile.equals(download.getDestPathFile());
            case WIN32:
            case WIN64:
            default:
                return !oldPathFile.equalsIgnoreCase(download.getDestPathFile());
        }
    }

    @Override
    public void make() {
        initCont();

        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(download.getTitle());

        btnContinueDownload.setVisible(directDownload);
        btnContinueDownload.setManaged(directDownload);

        if (!directDownload) {
            // nur für Downloads mit Programm
            txtFileName.setDisable(true);
            cbPath.setDisable(true);
        }

        initButton();
        initPathAndName();
        handleCountDownAction(); // damit die Dialoggröße stimmt

        //start the countdown...
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> handleCountDownAction()));
        timeline.playFromStart();
    }

    private void initCont() {
        getHBoxTitle().getChildren().add(lblHeader);

        // Gridpane
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        gridPane.add(new Label("Film:"), 0, row);
        gridPane.add(lblFilmTitle, 1, row, 2, 1);

        gridPane.add(new Label("Dateiname:"), 0, ++row);
        gridPane.add(txtFileName, 1, row, 2, 1);

        cbPath.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        gridPane.add(cbPath, 1, row);
        gridPane.add(btnPath, 2, row);

        GridPane.setHalignment(lblSizeFree, HPos.RIGHT);
        gridPane.add(lblSizeFree, 1, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        getVBoxCont().setPadding(new Insets(5));
        getVBoxCont().setSpacing(20);
        getVBoxCont().getChildren().addAll(gridPane);

        addCancelButton(btnCancel);
        addOkButton(btnRestartDownload);
        addOkButton(btnContinueDownload);

        VBox vBox = new VBox(5);
        vBox.getChildren().addAll(new Label("Wie möchten Sie forfahren?"), chkSave);
        getHboxLeft().getChildren().add(vBox);
    }

    private void initButton() {
        btnPath.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen"));
        btnPath.setOnAction(event -> getDestination());

        btnCancel.setOnAction(event -> {
            result = DownloadState.ContinueDownload.CANCEL_DOWNLOAD;
            quit();
        });

        btnRestartDownload.setOnAction(event -> {
            if (chkSave.isSelected()) {
                ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__RESTART);
            }

            result = DownloadState.ContinueDownload.RESTART_DOWNLOAD;
            download.setPathName(cbPath.getSelectionModel().getSelectedItem(), txtFileName.getText());
            quit();
        });
        btnContinueDownload.setOnAction(event -> {
            if (chkSave.isSelected()) {
                ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__CONTINUE);
            }

            result = DownloadState.ContinueDownload.CONTINUE_DOWNLOAD;
            quit();
        });
    }

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        final String[] p = ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");
        cbPath.getItems().addAll(p);

        if (download.getDestPath().isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
        } else {
            cbPath.getSelectionModel().select(download.getDestPath());
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            stopCounter();
            if (newValue != null) {
                btnContinueDownload.setDisable(!download.getDestPath().equals(newValue));
            }

            DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);
        });
        DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);

        txtFileName.setText(download.getDestFileName());
        txtFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            stopCounter();
            if (newValue != null) {
                btnContinueDownload.setDisable(!download.getDestFileName().equals(newValue));
            }

            if (!txtFileName.getText().equals(FileNameUtils.checkFileName(txtFileName.getText(), false /* pfad */))) {
                txtFileName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtFileName.setStyle("");
            }
        });
    }

    private void handleCountDownAction() {
        timeSeconds--;
        if (timeSeconds > 0) {
            if (!directDownload) {
                btnRestartDownload.setText("neu _Starten in " + timeSeconds + " s");
            } else {
                btnContinueDownload.setText("_Weiterführen in " + timeSeconds + " s");
            }
        } else {
            timeline.stop();
            result = DownloadState.ContinueDownload.CONTINUE_DOWNLOAD;
            quit();
        }
    }

    private void stopCounter() {
        if (timeline != null) {
            timeline.stop();
        }
        setButtonText();
    }

    private void setButtonText() {
        if (!directDownload) {
            btnRestartDownload.setText("neu _Starten");
        } else {
            btnContinueDownload.setText("_Weiterführen");
        }
    }

    private void quit() {
        timeline.stop();
        close();
    }

    private void getDestination() {
        PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cbPath);
    }
}
