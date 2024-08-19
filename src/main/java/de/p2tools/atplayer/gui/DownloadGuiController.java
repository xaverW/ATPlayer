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

import de.p2tools.atplayer.controller.audio.AudioFactory;
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.atplayer.controller.data.download.DownloadDataFactory;
import de.p2tools.atplayer.controller.downloadtools.DownloadConstants;
import de.p2tools.atplayer.gui.dialog.AudioInfoDialogController;
import de.p2tools.atplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.atplayer.gui.tools.table.Table;
import de.p2tools.atplayer.gui.tools.table.TableDownload;
import de.p2tools.atplayer.gui.tools.table.TableRowDownload;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.P2SystemUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DownloadGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();
    private final P2ClosePaneH pClosePaneHInfo;
    private final TabPane tabPaneInfo;
    public final TableDownload tableView;
    private final ProgData progData;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    DoubleProperty splitPaneProperty = ProgConfig.AUDIO_GUI_DIVIDER;
    BooleanProperty boolInfoOn = ProgConfig.DOWNLOAD_GUI_DIVIDER_ON;
    private boolean boundSplitPaneDivPos = false;

    private DownloadInfoController downloadInfoController;

    public DownloadGuiController() {
        progData = ProgData.getInstance();
        pClosePaneHInfo = new P2ClosePaneH(ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, true);
        tabPaneInfo = new TabPane();
        tableView = new TableDownload(Table.TABLE_ENUM.DOWNLOAD, progData);


        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPaneTableFilm.setFitToHeight(true);
        scrollPaneTableFilm.setFitToWidth(true);
        scrollPaneTableFilm.setContent(tableView);

        initInfoPane();
        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();
    }

    public void isShown() {
        tableView.requestFocus();
    }

    public int getFilmCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public void showAudioInfo() {
        AudioInfoDialogController.getInstanceAndShow().showAudioInfo();
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.DOWNLOAD);
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

    public void stopWaitingDownloads() {
        // aus dem Menü
        stopWaiting();
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
        AudioFactory.playAudio(download.get());
    }

    public void copyUrl() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        P2SystemUtils.copyToClipboard(download.get().getUrl());
    }

    public void invertSelection() {
        P2TableFactory.invertSelection(tableView);
    }

    public void playAudio() {
        final Optional<DownloadData> download = getSel();
        download.ifPresent(AudioFactory::playAudio);
    }

    private void stopWaiting() {
        // aus dem Menü
        // es werden alle noch nicht gestarteten Downloads, gestoppt
        final ArrayList<DownloadData> listStopDownload = new ArrayList<>();
        tableView.getItems().stream().filter(download -> download.isStateStartedWaiting()).forEach(download -> {
            listStopDownload.add(download);
        });
        progData.downloadList.stopDownloads(listStopDownload);
    }
//    public void setShown(boolean set) {
//        // aus dem Menü/Kontext Tabelle
//        final ArrayList<DownloadData> list = getSelList();
//        if (list.isEmpty()) {
//            return;
//        }
//        AudioTools.setFilmShown(list, set);
//    }

    public void refreshTable() {
        P2TableFactory.refreshTable(tableView);
    }

    public void copyFilmThemeTitle(boolean theme) {
        final Optional<DownloadData> downloadData = getSel(true);
        downloadData.ifPresent(data -> P2SystemUtils.copyToClipboard(theme ? data.getTheme() : data.getTitle()));
    }

    public ArrayList<DownloadData> getSelList() {
        final ArrayList<DownloadData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            P2Alert.showInfoNoSelection();
        }
        return ret;
    }

    public Optional<DownloadData> getSel() {
        return getSel(true);
    }

    public Optional<DownloadData> getSel(boolean show) {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                P2Alert.showInfoNoSelection();
            }
            return Optional.empty();
        }
    }

    private void initListener() {
        PListener.addListener(new PListener(PListener.EVENT_TIMER, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if (!ProgConfig.FILTER_DOWNLOAD_STATE.get().isEmpty()) {
                    // dann den Filter aktualisieren
                    // todo?? bei vielen Downloads kann das sonst die ganze Tabelle ausbremsen
                    setFilter();
                }
            }
        });
        PListener.addListener(new PListener(new int[]{PListener.EVENT_GUI_HISTORY_CHANGED},
                DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
            }
        });
        PListener.addListener(new PListener(new int[]{PListener.EVENT_HISTORY_CHANGED},
                DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_BLACKLIST_CHANGED, this.getClass().getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);

        tableView.setItems(progData.downloadList.getSortedList());
        progData.downloadList.getSortedList().comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowDownload<DownloadData> row = new TableRowDownload<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    editDownloads();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final DownloadData downloadData = (DownloadData) row.getItem();
                if (row.isHover() && downloadData != null) { // null bei den leeren Zeilen unterhalb
                    downloadInfoController.setDownloadData(downloadData);
                } else if (downloadData == null) {
                    downloadInfoController.setDownloadData(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.hoverProperty().addListener((o) -> {
            if (!tableView.isHover()) {
                downloadInfoController.setDownloadData(tableView.getSelectionModel().getSelectedItem());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                //wird auch durch FilmlistenUpdate ausgelöst
                Platform.runLater(() -> downloadInfoController.setDownloadData(tableView.getSelectionModel().getSelectedItem())));
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<DownloadData> optionalDownload = getSel(false);
                DownloadData download = optionalDownload.orElse(null);
                ContextMenu contextMenu = new DownloadTableContextMenu(progData, this, tableView).
                        getContextMenu(download);
                tableView.setContextMenu(contextMenu);
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

    private void setFilterProperty() {
        ProgConfig.FILTER_DOWNLOAD_CHANNEL.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_GENRE.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_THEME.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_TITLE.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_STATE.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
    }

    private void setFilter() {
        Platform.runLater(() -> {
            Predicate<DownloadData> predicate = downloadData -> true;

            final String channel = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
            final String genre = ProgConfig.FILTER_DOWNLOAD_GENRE.getValueSafe();
            final String theme = ProgConfig.FILTER_DOWNLOAD_THEME.getValueSafe();
            final String title = ProgConfig.FILTER_DOWNLOAD_TITLE.getValueSafe();
            final String state = ProgConfig.FILTER_DOWNLOAD_STATE.getValueSafe();

            predicate = predicate.and(download -> !download.isPlacedBack());

            if (!channel.isEmpty()) {
                Filter filter = new Filter(channel, true);
                predicate = predicate.and(downloadData -> FilterCheck.check(filter, downloadData.getChannel()));
            }
            if (!genre.isEmpty()) {
                Filter filter = new Filter(genre, true);
                predicate = predicate.and(downloadData -> FilterCheck.check(filter, downloadData.getGenre()));
            }

            if (!theme.isEmpty()) {
                Filter filter = new Filter(theme, true);
                predicate = predicate.and(downloadData -> FilterCheck.check(filter, downloadData.getTheme()));
            }
            if (!title.isEmpty()) {
                Filter filter = new Filter(title, true);
                predicate = predicate.and(downloadData -> FilterCheck.check(filter, downloadData.getTitle()));
            }

            if (!state.isEmpty()) {
                predicate = predicate.and(downloadData -> state.equals(DownloadConstants.STATE_COMBO_NOT_STARTED) && !downloadData.isStarted() ||
                        state.equals(DownloadConstants.STATE_COMBO_WAITING) && downloadData.isStateStartedWaiting() ||
                        state.equals(DownloadConstants.STATE_COMBO_STARTED) && downloadData.isStarted() ||
                        state.equals(DownloadConstants.STATE_COMBO_LOADING) && downloadData.isStateStartedRun() ||
                        state.equals(DownloadConstants.STATE_COMBO_ERROR) && downloadData.isStateError());
            }

            progData.downloadList.getFilteredList().setPredicate(predicate);
        });
    }

    private void initInfoPane() {
        downloadInfoController = new DownloadInfoController();
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());
    }

    private void setInfoPane() {
        if (boolInfoOn.getValue()) {
            boundSplitPaneDivPos = true;
            setInfoTabPane();
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);

        } else {
            if (boundSplitPaneDivPos) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }

            if (splitPane.getItems().size() != 1) {
                splitPane.getItems().clear();
                splitPane.getItems().add(scrollPaneTableFilm);
            }
        }
    }

    private void setInfoTabPane() {
        if (splitPane.getItems().size() != 2) {
            //erst mal splitPane einrichten, dass Tabelle und Info angezeigt werden
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPaneTableFilm, pClosePaneHInfo);
            SplitPane.setResizableWithParent(pClosePaneHInfo, false);
        }

        Tab tabInfo = new Tab("Infos");
        tabInfo.setClosable(false);
        tabInfo.setContent(downloadInfoController);

        tabPaneInfo.getTabs().clear();
        tabPaneInfo.getTabs().addAll(tabInfo);

        pClosePaneHInfo.getVBoxAll().getChildren().clear();
        pClosePaneHInfo.getVBoxAll().getChildren().add(tabPaneInfo);
        VBox.setVgrow(tabPaneInfo, Priority.ALWAYS);
    }
}
