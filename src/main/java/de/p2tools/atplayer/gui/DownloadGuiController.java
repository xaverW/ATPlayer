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

import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.atplayer.controller.audio.AudioPlayFactory;
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadConstants;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.atplayer.controller.data.download.DownloadDataFactory;
import de.p2tools.atplayer.gui.dialog.AudioInfoDialogController;
import de.p2tools.atplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.atplayer.gui.infopane.*;
import de.p2tools.atplayer.gui.tools.table.Table;
import de.p2tools.atplayer.gui.tools.table.TableDownload;
import de.p2tools.atplayer.gui.tools.table.TableRowDownload;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoController;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoDto;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.P2SystemUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DownloadGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    public final TableDownload tableView;
    private final ProgData progData;

    private final PaneAudioInfo paneFilmInfo;
    private final PaneBandwidthChart paneBandwidthChart;
    private final PaneDownloadError paneDownloadError;
    private final PaneDownloadInfo paneDownloadInfoList;
    private final P2InfoController infoController;
    private final BooleanProperty boundInfo = new SimpleBooleanProperty(false);

    public DownloadGuiController() {
        progData = ProgData.getInstance();
        tableView = new TableDownload(Table.TABLE_ENUM.DOWNLOAD, progData);

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

        paneFilmInfo = new PaneAudioInfo(ProgConfig.DOWNLOAD_PANE_INFO_DIVIDER);
        paneBandwidthChart = new PaneBandwidthChart(progData);
        paneDownloadError = new PaneDownloadError();
        paneDownloadInfoList = new PaneDownloadInfo();

        ArrayList<P2InfoDto> list = new ArrayList<>();
        P2InfoDto infoDto = new P2InfoDto(paneFilmInfo,
                ProgConfig.DOWNLOAD__INFO_INFO_IS_RIP,
                ProgConfig.DOWNLOAD__INFO_INFO_DIALOG_SIZE, ProgData.DOWNLOAD_TAB_ON,
                "Beschreibung", "Beschreibung", false,
                progData.maskerPane.visibleProperty());
        list.add(infoDto);

        infoDto = new P2InfoDto(paneBandwidthChart,
                ProgConfig.DOWNLOAD__INFO_CHART_IS_RIP,
                ProgConfig.DOWNLOAD__INFO_CHART_DIALOG_SIZE, ProgData.DOWNLOAD_TAB_ON,
                "Downloadchart", "Downloadchart", false,
                progData.maskerPane.visibleProperty());
        list.add(infoDto);

        infoDto = new P2InfoDto(paneDownloadError,
                ProgConfig.DOWNLOAD__INFO_ERROR_IS_RIP,
                ProgConfig.DOWNLOAD__INFO_ERROR_DIALOG_SIZE, ProgData.DOWNLOAD_TAB_ON,
                "Fehler", "Fehler", false,
                progData.maskerPane.visibleProperty());
        list.add(infoDto);

        infoDto = new P2InfoDto(paneDownloadInfoList,
                ProgConfig.DOWNLOAD__INFO_LIST_IS_RIP,
                ProgConfig.DOWNLOAD__INFO_LIST_DIALOG_SIZE, ProgData.DOWNLOAD_TAB_ON,
                "Infos", "Infos", false,
                progData.maskerPane.visibleProperty());
        list.add(infoDto);

        infoController = new P2InfoController(list, ProgConfig.DOWNLOAD__INFO_IS_SHOWING);

        ProgConfig.DOWNLOAD__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__INFO_INFO_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__INFO_CHART_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__INFO_ERROR_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__INFO_LIST_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();

        PListener.addListener(new PListener(PListener.EVENT_TIMER, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // todo nur wenn sichtbar
                paneBandwidthChart.searchInfos(InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD, paneBandwidthChart));

                if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD, paneDownloadInfoList)) {
                    paneDownloadInfoList.setInfoText();
                }
            }
        });


    }

    public void isShown() {
        setAudioInfos(tableView.getSelectionModel().getSelectedItem());
        tableView.requestFocus();
    }

    public int getFilmCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    private void setAudioInfos(DownloadData download) {
        if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD, paneFilmInfo)) {
            paneFilmInfo.setAudioData(download);
        }
        AudioInfoDialogController.getInstance().setAudio(download != null ? download.getAudioData() : null);
    }

    public void showAudioInfo() {
        AudioInfoDialogController.getInstanceAndShow().showAudioInfo();
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.DOWNLOAD);
    }

    public void startDownload(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Filme
        // der/die noch nicht gestartet sind, werden gestartet
        // Filme dessen Start schon auf fehler steht werden wieder gestartet
        final ArrayList<DownloadData> startDownloadsList = new ArrayList<>();
        startDownloadsList.addAll(all ? tableView.getItems() : getSelList());
        progData.downloadList.startDownloads(startDownloadsList, true);
    }

    public void startDownload(DownloadData downloadData) {
        progData.downloadList.startDownloads(downloadData);
    }

    public void stopDownloads(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Audios
        final ArrayList<DownloadData> data =
                new ArrayList<>(all ? tableView.getItems() : getSelList());
        progData.downloadList.stopDownloads(data);
    }

//    public void stopDownloads(DownloadData downloadData) {
//        progData.downloadList.stopDownloads(downloadData);
//    }

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

    public void playUrl() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        // und starten
        AudioPlayFactory.playUrlAudio(download.get());
    }

    public void playStoredAudio() {
        final Optional<DownloadData> download = getSel();
        download.ifPresent(AudioPlayFactory::playStoredAudio);
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
        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                setFilter());
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
                    setAudioInfos(downloadData);
                } else if (downloadData == null) {
                    setAudioInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.hoverProperty().addListener((o) -> {
            if (!tableView.isHover()) {
                setAudioInfos(tableView.getSelectionModel().getSelectedItem());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                //wird auch durch FilmlistenUpdate ausgelöst
                Platform.runLater(() -> setAudioInfos(tableView.getSelectionModel().getSelectedItem())));
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

    private void setInfoPane() {
        P2ClosePaneFactory.setSplit(boundInfo, splitPane,
                infoController, false, scrollPane,
                ProgConfig.DOWNLOAD__INFO_DIVIDER, ProgConfig.DOWNLOAD__INFO_IS_SHOWING);
    }
}
