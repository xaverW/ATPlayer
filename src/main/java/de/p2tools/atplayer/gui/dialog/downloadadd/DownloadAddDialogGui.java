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

package de.p2tools.atplayer.gui.dialog.downloadadd;


import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;

public class DownloadAddDialogGui {

    private final AddDownloadDto addDownloadDto;
    private final VBox vBoxCont;
    private final HBox hBoxTop = new HBox();

    public DownloadAddDialogGui(AddDownloadDto addDownloadDto, VBox vBoxCont) {
        this.addDownloadDto = addDownloadDto;
        this.vBoxCont = vBoxCont;
    }

    public void addCont() {
        addDownloadDto.lblAudio.setStyle("-fx-font-weight: bold;");
        addDownloadDto.lblAudioTitle.setStyle("-fx-font-weight: bold;");

        // Top
        hBoxTop.getStyleClass().add("downloadDialog");
        hBoxTop.setSpacing(20);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setPadding(new Insets(5));
        hBoxTop.getChildren().addAll(addDownloadDto.btnPrev, addDownloadDto.lblSum, addDownloadDto.btnNext);
        vBoxCont.getChildren().add(hBoxTop);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(5));
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        vBoxCont.setPadding(new Insets(10));

//        gridPane.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.actAudioIsShown.addListener((u, o, n) -> gridPane.setDisable(addDownloadDto.getAct().downloadIsRunning()));

        int row = 0;
        // Titel
        gridPane.add(addDownloadDto.lblAudio, 0, row);
        gridPane.add(addDownloadDto.lblAudioTitle, 1, row);
        gridPane.add(addDownloadDto.lblAll, 3, row, 1, 2);
        addDownloadDto.lblAll.setMinHeight(Region.USE_PREF_SIZE);
        GridPane.setValignment(addDownloadDto.lblAll, VPos.TOP);
        // Datum - Zeit - Länge
        gridPane.add(addDownloadDto.lblAudioDateTime, 1, ++row);
        gridPane.add(addDownloadDto.lblAudioSize, 1, ++row);

        ++row;
        // URL
        addDownloadDto.p2HyperlinkUrlDownload.setWrapText(true);
        addDownloadDto.p2HyperlinkUrlDownload.setMinHeight(Region.USE_PREF_SIZE);
        addDownloadDto.p2HyperlinkUrlDownload.setPadding(new Insets(5));

        TitledPane tpUrl = new TitledPane("", new HBox());
        tpUrl.setExpanded(false);
        GridPane g = new GridPane();
        g.add(new Label("URL: "), 0, row);
        g.add(addDownloadDto.p2HyperlinkUrlDownload, 1, row++, 3, 1);
        tpUrl.setContent(g);

        gridPane.add(new Label("URL:"), 0, ++row);
        gridPane.add(tpUrl, 1, row, 2, 1);

        // Dateiname
        gridPane.add(DownloadAddDialogFactory.getText("Dateiname:"), 0, ++row);
        gridPane.add(addDownloadDto.txtName, 1, row, 2, 1);

        // Pfad
        gridPane.add(DownloadAddDialogFactory.getText("Pfad:"), 0, ++row);
        addDownloadDto.cboPath.setMaxWidth(Double.MAX_VALUE);
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.getChildren().addAll(addDownloadDto.btnDest, addDownloadDto.btnPropose, addDownloadDto.btnClean);

        gridPane.add(addDownloadDto.cboPath, 1, row);
        gridPane.add(hBox, 2, row);
        gridPane.add(addDownloadDto.chkPathAll, 3, row);

        HBox hBox2 = new HBox();
        hBox2.getChildren().add(addDownloadDto.lblFree);
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox2, 1, ++row, 2, 1);

        // Info
        gridPane.add(DownloadAddDialogFactory.getText("Infodatei:"), 0, ++row);
        gridPane.add(addDownloadDto.chkInfo, 1, row);
        gridPane.add(addDownloadDto.chkInfoAll, 3, row);

        // Startzeit
        final HBox hBoxStartTime = new HBox();
        hBoxStartTime.getStyleClass().add("downloadDialog");
        hBoxStartTime.setAlignment(Pos.CENTER_LEFT);
        hBoxStartTime.setSpacing(20);
        hBoxStartTime.setPadding(new Insets(5));
        hBoxStartTime.getChildren().addAll(addDownloadDto.rbStartNotYet,
                addDownloadDto.rbStartNow);
        gridPane.add(DownloadAddDialogFactory.getText("Startzeit:"), 0, ++row);
        gridPane.add(hBoxStartTime, 1, row, 2, 1);
        gridPane.add(addDownloadDto.chkStartTimeAll, 3, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSizeCenter());
        vBoxCont.getChildren().add(gridPane);
    }

    public void init() {
        if (addDownloadDto.addDownloadData.length == 1) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
            addDownloadDto.lblAll.setVisible(false);
            addDownloadDto.lblAll.setManaged(false);

            addDownloadDto.chkSetAll.setVisible(false);
            addDownloadDto.chkSetAll.setManaged(false);
            addDownloadDto.chkResolutionAll.setVisible(false);
            addDownloadDto.chkResolutionAll.setManaged(false);
            addDownloadDto.chkPathAll.setVisible(false);
            addDownloadDto.chkPathAll.setManaged(false);
            addDownloadDto.chkInfoAll.setVisible(false);
            addDownloadDto.chkInfoAll.setVisible(false);
            addDownloadDto.chkInfoAll.setManaged(false);
            addDownloadDto.chkStartTimeAll.setVisible(false);
            addDownloadDto.chkStartTimeAll.setManaged(false);
        }
    }
}
