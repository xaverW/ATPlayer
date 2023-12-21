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

import de.p2tools.atplayer.controller.audio.AudioTools;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.atplayer.controller.data.download.DownloadDataFactory;
import de.p2tools.atplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.atplayer.gui.tools.table.Table;
import de.p2tools.atplayer.gui.tools.table.TableDownload;
import de.p2tools.atplayer.gui.tools.table.TableRowDownload;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DownloadInfoController extends AnchorPane {

    private final HBox hBoxAll = new HBox();
    private final VBox vBoxTable = new VBox();
    private final TableDownload tableView;
    private final ScrollPane scrollPane = new ScrollPane();
    private final ProgData progData;
    private final SortedList<DownloadData> sortedDownloads;

    DoubleProperty doubleProperty; //sonst geht die Ref verloren

    public DownloadInfoController() {
        progData = ProgData.getInstance();
        tableView = new TableDownload(Table.TABLE_ENUM.DOWNLOAD);
        this.doubleProperty = ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER;
        sortedDownloads = new SortedList<>(progData.downloadList);

        AnchorPane.setLeftAnchor(hBoxAll, 0.0);
        AnchorPane.setBottomAnchor(hBoxAll, 0.0);
        AnchorPane.setRightAnchor(hBoxAll, 0.0);
        AnchorPane.setTopAnchor(hBoxAll, 0.0);
        getChildren().add(hBoxAll);
        make();
    }

    public void tableRefresh() {
        tableView.refresh();
    }

    public void startDownloads(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Audios
        final ArrayList<DownloadData> startDownloadsList =
                new ArrayList<>(all ? tableView.getItems() : getSelList());
        progData.downloadList.startDownloads(startDownloadsList, true);
    }

    public void startDownloads(DownloadData downloadData) {
        progData.downloadList.startDownloads(downloadData);
    }

    public void stopDownloads(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Audios
        final ArrayList<DownloadData> data =
                new ArrayList<>(all ? tableView.getItems() : getSelList());
        progData.downloadList.stopDownloads(data);
    }

    public void stopDownloads(DownloadData downloadData) {
        progData.downloadList.stopDownloads(downloadData);
    }

    public void editDownloads() {
        List<DownloadData> list = getSelList();
        if (!list.isEmpty()) {
            new DownloadAddDialogController(progData, null, list);
        }
    }

    public void editDownloads(DownloadData downloadData) {
        List<DownloadData> list = new ArrayList<>();
        list.add(downloadData);
        new DownloadAddDialogController(progData, null, list);
    }

    public void deleteDownloads() {
        progData.downloadList.delDownloads(getSelList());
    }

    public void deleteDownloads(DownloadData downloadData) {
        progData.downloadList.delDownloads(downloadData);
    }

    public void preferDownload() {
        progData.downloadList.preferDownloads(getSelList());
    }

    public void moveDownloadBack() {
        progData.downloadList.putBackDownloads(getSelList());
    }

    public void deleteAudioFile() {
        // Download nur löschen wenn er nicht läuft
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        DownloadDataFactory.deleteAudioFile(download.get());
    }

    public void openDestinationDir() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        String s = download.get().getDestPath();
        P2Open.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
    }

    public void playUrl() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        // und starten
        AudioTools.playAudio(download.get());
    }

    public void copyUrl() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        PSystemUtils.copyToClipboard(download.get().getUrl());
    }

    public void invertSelection() {
        P2TableFactory.invertSelection(tableView);
    }

    public void playAudio() {
        final Optional<DownloadData> download = getSel();
        download.ifPresent(AudioTools::playAudio);
    }

    private void make() {
        hBoxAll.setSpacing(P2LibConst.DIST_BUTTON);
        hBoxAll.setPadding(new Insets(P2LibConst.PADDING));

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);
        vBoxTable.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Button btnStart = new Button();
        Button btnStartAll = new Button();
        Button btnStop = new Button();
        Button btnDel = new Button();
        Button btnEdit = new Button();
        Button btnClearFilter = new Button();

        VBox vBoxButton = new VBox(P2LibConst.DIST_BUTTON);
        vBoxButton.setAlignment(Pos.TOP_CENTER);
        vBoxButton.getChildren().addAll(btnStart, btnStartAll, btnStop, btnDel, btnEdit, btnClearFilter);

        hBoxAll.getChildren().addAll(vBoxTable, vBoxButton);

        btnStart.setGraphic(ProgIcons.ICON_BUTTON_DOWNLOAD_START.getImageView());
        btnStart.setTooltip(new Tooltip("Markierte Downloads starten"));
        btnStart.getStyleClass().add("buttonSmall");
        btnStart.setOnAction(a -> startDownloads(false /* alle */));

        btnStartAll.setGraphic(ProgIcons.ICON_BUTTON_DOWNLOAD_START_ALL.getImageView());
        btnStartAll.setTooltip(new Tooltip("Alle Downloads starten"));
        btnStartAll.getStyleClass().add("buttonSmall");
        btnStartAll.setOnAction(a -> startDownloads(true /* alle */));

        btnStop.setGraphic(ProgIcons.ICON_BUTTON_DOWNLOAD_STOP.getImageView());
        btnStop.setTooltip(new Tooltip("Markierte Downloads stoppen"));
        btnStop.getStyleClass().add("buttonSmall");
        btnStop.setOnAction(a -> stopDownloads(false /* alle */));

        btnDel.setGraphic(ProgIcons.ICON_BUTTON_DOWNLOAD_DEL.getImageView());
        btnDel.setTooltip(new Tooltip("Markierte Downloads löschen"));
        btnDel.getStyleClass().add("buttonSmall");
        btnDel.setOnAction(a -> ProgData.getInstance().downloadList.delDownloads(getSelList()));

        btnEdit.setGraphic(ProgIcons.ICON_BUTTON_DOWNLOAD_EDIT.getImageView());
        btnEdit.setTooltip(new Tooltip("Markierte Downloads ändern"));
        btnEdit.getStyleClass().add("buttonSmall");
        btnEdit.setOnAction(a -> editDownloads());

        btnClearFilter.setGraphic(ProgIcons.ICON_BUTTON_DOWNLOAD_CLEAN.getImageView());
        btnClearFilter.setTooltip(new Tooltip("Tabelle aufräumen"));
        btnClearFilter.getStyleClass().add("buttonSmall");
        btnClearFilter.setOnAction(a -> progData.downloadList.cleanUpList());

        initTable();
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.DOWNLOAD);
    }

    private void initTable() {
        Table.setTable(tableView);

        tableView.setItems(sortedDownloads);
        sortedDownloads.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowDownload<DownloadData> row = new TableRowDownload<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    editDownloads();
                }
            });
            return row;
        });
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<DownloadData> optionalDownload = getSel(false);
                DownloadData download = optionalDownload.orElse(null);
                ContextMenu contextMenu = new DownloadTableContextMenu(progData, this, tableView).
                        getContextMenu(download);
                tableView.setContextMenu(contextMenu);
            }
        });
        tableView.getItems().addListener((ListChangeListener<DownloadData>) c -> {
            if (tableView.getItems().size() == 1) {
                // wenns nur eine Zeile gibt, dann gleich selektieren
                tableView.getSelectionModel().select(0);
            }
        });
        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (P2TableFactory.SPACE.match(event)) {
                P2TableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (P2TableFactory.SPACE_SHIFT.match(event)) {
                P2TableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }
        });
    }

    private Optional<DownloadData> getSel() {
        return getSel(true);
    }

    private Optional<DownloadData> getSel(boolean show) {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                PAlert.showInfoNoSelection();
            }
            return Optional.empty();
        }
    }

    private ArrayList<DownloadData> getSelList() {
        final ArrayList<DownloadData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }
}
