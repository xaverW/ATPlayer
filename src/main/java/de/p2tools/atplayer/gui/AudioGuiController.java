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
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.dialog.AudioInfoDialogController;
import de.p2tools.atplayer.gui.tools.table.Table;
import de.p2tools.atplayer.gui.tools.table.TableAudio;
import de.p2tools.atplayer.gui.tools.table.TableRowAudio;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

public class AudioGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();
    private final P2ClosePaneH pClosePaneHInfo;
    private final TabPane tabPaneInfo;
    private final TableAudio tableView;
    private final ProgData progData;
    private final SortedList<AudioData> sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    DoubleProperty splitPaneProperty = ProgConfig.AUDIO_GUI_DIVIDER;
    BooleanProperty boolInfoOn = ProgConfig.AUDIO_GUI_DIVIDER_ON;
    private AudioData lastShownFilmData = null;
    private boolean boundSplitPaneDivPos = false;

    private AudioInfoController audioInfoController;
    private final DownloadInfoController downloadInfoController;

    public AudioGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.audioList.getSortedList();
        pClosePaneHInfo = new P2ClosePaneH(ProgConfig.AUDIO_GUI_DIVIDER_ON, true);
        tabPaneInfo = new TabPane();
        tableView = new TableAudio(Table.TABLE_ENUM.FILM, progData);

        downloadInfoController = new DownloadInfoController();

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
    }

    public void isShown() {
        setAudioInfos();
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
        Table.saveTable(tableView, Table.TABLE_ENUM.FILM);
        downloadInfoController.saveTable();
    }

    public void setShown(boolean set) {
        // aus dem Menü/Kontext Tabelle
        final ArrayList<AudioData> list = getSelList();
        if (list.isEmpty()) {
            return;
        }
        AudioTools.setFilmShown(list, set);
    }

    public void refreshTable() {
        P2TableFactory.refreshTable(tableView);
    }

    public ArrayList<AudioData> getSelList() {
        final ArrayList<AudioData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public void setBookmark(boolean bookmark) {
        final ArrayList<AudioData> list = getSelList();
        if (!list.isEmpty()) {
            AudioTools.bookmarkFilmList(list, bookmark);
        }
    }

    public Optional<AudioData> getSel() {
        return getSel(true);
    }

    public Optional<AudioData> getSel(boolean show) {
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

    private void initListener() {
        PListener.addListener(new PListener(new int[]{PListener.EVENT_GUI_HISTORY_CHANGED},
                AudioGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
            }
        });
        PListener.addListener(new PListener(new int[]{PListener.EVENT_HISTORY_CHANGED},
                AudioGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
            }
        });
    }

    private void selectFilm() {
        Platform.runLater(() -> {
            if ((tableView.getItems().size() == 0)) {
                return;
            }
            if (lastShownFilmData != null) {
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(lastShownFilmData);
                tableView.scrollTo(lastShownFilmData);

            } else {
                AudioData selFilm = tableView.getSelectionModel().getSelectedItem();
                if (selFilm != null) {
                    tableView.scrollTo(selFilm);
                } else {
                    tableView.getSelectionModel().clearSelection();
                    tableView.getSelectionModel().select(0);
                    tableView.scrollTo(0);
                }
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);

        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<AudioData> optionalFilm = getSel(false);
                AudioData film;
                film = optionalFilm.orElse(null);
                ContextMenu contextMenu = new AudioTableContextMenu(progData, this, tableView).getContextMenu(film);
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

            if (STRG_A.match(event) && tableView.getItems().size() > 3_000) {
                //macht eingentlich keine Sinn???
                PLog.sysLog("STRG-A: lange Liste -> verhindern");
                event.consume();
            }
        });

        tableView.setRowFactory(tableView -> {
            TableRowAudio<AudioData> row = new TableRowAudio<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    AudioInfoDialogController.getInstanceAndShow().showAudioInfo();
                }
            });
            row.hoverProperty().addListener((observable) -> {
                final AudioData audioData = (AudioData) row.getItem();
                if (row.isHover() && audioData != null) { // null bei den leeren Zeilen unterhalb
                    setAudioInfos(audioData);
                } else if (audioData == null) {
                    setAudioInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setAudioInfos));
        tableView.hoverProperty().addListener((o) -> {
            if (!tableView.isHover()) {
                setAudioInfos(tableView.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void setAudioInfos() {
        setAudioInfos(tableView.getSelectionModel().getSelectedItem());
    }

    private void setAudioInfos(AudioData audios) {
        // Film in FilmInfoDialog setzen
        audioInfoController.setAudioData(audios);
        AudioInfoDialogController.getInstance().setAudio(audios);
    }

    private void initInfoPane() {
        audioInfoController = new AudioInfoController();
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
        tabInfo.setContent(audioInfoController);

        Tab tabDownloads = new Tab("Downloads");
        tabDownloads.setClosable(false);
        tabDownloads.setContent(downloadInfoController);

        tabPaneInfo.getTabs().clear();
        tabPaneInfo.getTabs().addAll(tabInfo, tabDownloads);

        pClosePaneHInfo.getVBoxAll().getChildren().clear();
        pClosePaneHInfo.getVBoxAll().getChildren().add(tabPaneInfo);
        VBox.setVgrow(tabPaneInfo, Priority.ALWAYS);
    }
}
