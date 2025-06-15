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

package de.p2tools.atplayer.gui.infopane;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.mediathek.audiodata.AudioData;
import de.p2tools.p2lib.mediathek.audiodata.AudioDataXml;
import de.p2tools.p2lib.mediathek.download.DownloadSizeData;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PaneAudioInfo extends VBox {
    private final SplitPane splitPane = new SplitPane();
    private final VBox vBoxLeft = new VBox();

    private final TextArea textArea = new TextArea();
    private final Button btnReset = new Button("@");
    private final Label lblTheme = new Label("");
    private final Label lblTitle = new Label("");
    private final HBox hBoxUrl = new HBox(10);
    private final Label lblUrl = new Label("zur Website: ");

    private final Label lblDate = new Label();
    private final Label lblTime = new Label();
    private final Label lblDuration = new Label();
    private final Label lblSize = new Label();

    private AudioData audioData = null;
    private DownloadData downloadData = null;
    private final ChangeListener<DownloadSizeData> sizeChangeListener;
    private String oldDescription = "";

    public PaneAudioInfo(DoubleProperty dividerProp) {
        this.sizeChangeListener = (u, o, n) -> setSize(true);
        VBox.setVgrow(this, Priority.ALWAYS);

        btnReset.setOnAction(a -> resetFilmDescription());
        btnReset.setTooltip(new Tooltip("Beschreibung zurücksetzen"));
        btnReset.setVisible(false);

        lblTheme.setFont(Font.font(null, FontWeight.BOLD, -1));
        hBoxUrl.setAlignment(Pos.CENTER_LEFT);
        lblUrl.setMinWidth(Region.USE_PREF_SIZE);

        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);
        textArea.textProperty().addListener((a, b, c) -> setFilmDescription());

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(textArea, btnReset);
        StackPane.setAlignment(btnReset, Pos.BOTTOM_RIGHT);
        stackPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        vBoxLeft.setSpacing(2);
        vBoxLeft.setPadding(new Insets(P2LibConst.PADDING));
        vBoxLeft.getChildren().addAll(lblTheme, lblTitle, stackPane, hBoxUrl);


        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("extra-pane-info");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(), P2ColumnConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        gridPane.add(new Label("Datum: "), 0, row);
        gridPane.add(lblDate, 1, row);
        gridPane.add(new Label("Zeit: "), 0, ++row);
        gridPane.add(lblTime, 1, row);
        gridPane.add(new Label("Dauer: "), 0, ++row);
        gridPane.add(lblDuration, 1, row);
        gridPane.add(new Label("Größe: "), 0, ++row);
        gridPane.add(lblSize, 1, row);


        splitPane.getItems().addAll(vBoxLeft, gridPane);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(dividerProp);
        SplitPane.setResizableWithParent(gridPane, Boolean.FALSE);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        setSpacing(0);
        setPadding(new Insets(0));
        getChildren().add(splitPane);
    }

    public void setAudioData(AudioData audioData) {
        hBoxUrl.getChildren().clear();

        if (audioData == null) {
            this.audioData = null;
            lblTheme.setText("");
            lblTitle.setText("");
            textArea.clear();
            oldDescription = "";
            btnReset.setVisible(false);

            lblDate.setText("");
            lblTime.setText("");
            lblDuration.setText("");
            lblSize.setText("");
            return;
        }

        this.audioData = audioData;

        lblTheme.setText(audioData.arr[AudioDataXml.AUDIO_CHANNEL] + "  -  " + audioData.arr[AudioDataXml.AUDIO_THEME]);
        lblTitle.setText(audioData.arr[AudioDataXml.AUDIO_TITLE]);
        textArea.setText(audioData.getDescription());
        oldDescription = audioData.getDescription();
        btnReset.setVisible(false);

        if (!audioData.arr[AudioDataXml.AUDIO_WEBSITE].isEmpty()) {
            P2Hyperlink hyperlink = new P2Hyperlink(audioData.arr[AudioDataXml.AUDIO_WEBSITE],
                    ProgConfig.SYSTEM_PROG_OPEN_URL);
            hBoxUrl.getChildren().addAll(lblUrl, hyperlink);
        }

        lblDate.setText(audioData.getDate().get_dd_MM_yyyy());
        lblDuration.setText(audioData.getDuration().isEmpty() ? "" : (audioData.getDuration() + " [min]"));
        lblSize.setText(audioData.getAudioSize().toString().isEmpty() ? "" : (audioData.getAudioSize().toString() + " [MB]"));
    }

    public void setAudioData(DownloadData downloadData) {
        hBoxUrl.getChildren().clear();
        if (this.downloadData != null) {
            this.downloadData.downloadSizeProperty().removeListener(sizeChangeListener);
        }

        this.audioData = null;
        this.downloadData = downloadData;

        if (downloadData == null) {
            lblTheme.setText("");
            lblTitle.setText("");
            textArea.clear();
            oldDescription = "";
            btnReset.setVisible(false);

            lblDate.setText("");
            lblTime.setText("");
            lblDuration.setText("");
            lblSize.setText("");
            return;
        }

        lblTheme.setText(downloadData.getChannel() + "  -  " + downloadData.getTheme());
        lblTitle.setText(downloadData.getTitle());
        lblDate.setText(P2LDateFactory.toString(downloadData.getFilmDate()));
        lblTime.setText(downloadData.getFilmTime());
        lblDuration.setText(downloadData.getDurationMinute() + " [min]");

        setSize(false); // die kann bim Film abweichen: HD, small
        downloadData.downloadSizeProperty().addListener(sizeChangeListener);

        textArea.setText(downloadData.getDescription());
        textArea.setEditable(false);
        oldDescription = downloadData.getDescription();
        btnReset.setVisible(false);

        if (!downloadData.getUrlWebsite().isEmpty()) {
            P2Hyperlink hyperlink = new P2Hyperlink(downloadData.getUrlWebsite(),
                    ProgConfig.SYSTEM_PROG_OPEN_URL);
            hBoxUrl.getChildren().addAll(lblUrl, hyperlink);
        }
    }

    private void setSize(boolean async) {
        if (downloadData != null) {
            final String size = downloadData.getDownloadSize().toString();

            if (async) {
                Platform.runLater(() -> {
                    // die kann bim Film abweichen: HD, small
                    // und wird beim Download asynchron gesetzt
                    if (size.isEmpty()) {
                        lblSize.setText("");
                    } else {
                        lblSize.setText(size + " [MB]");
                    }
                });

            } else {
                if (size.isEmpty()) {
                    lblSize.setText("");
                } else {
                    lblSize.setText(size + " [MB]");
                }
            }
        }
    }

    private void setFilmDescription() {
        if (audioData != null) {
            btnReset.setVisible(true);
            audioData.setDescription(textArea.getText());
        }
    }

    private void resetFilmDescription() {
        if (audioData != null) {
            audioData.setDescription(oldDescription);
            textArea.setText(audioData.getDescription());
            btnReset.setVisible(false);
        }
    }
}
