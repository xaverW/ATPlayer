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

import de.p2tools.atplayer.controller.audio.AudioToolsFactory;
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.dialog.AudioInfoDialogController;
import de.p2tools.atplayer.gui.infopane.PaneAudioInfo;
import de.p2tools.atplayer.gui.tools.table.Table;
import de.p2tools.atplayer.gui.tools.table.TableAudio;
import de.p2tools.atplayer.gui.tools.table.TableRowAudio;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoController;
import de.p2tools.p2lib.guitools.pclosepane.P2InfoDto;
import de.p2tools.p2lib.tools.P2SystemUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Optional;

public class AudioGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();
    private final TableAudio tableView;
    private final ProgData progData;
    private final SortedList<AudioData> sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    private final PaneAudioInfo paneAudioInfo;
    private final P2InfoController infoController;
    private final BooleanProperty boundInfo = new SimpleBooleanProperty(false);

    public AudioGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.audioListFiltered.getSortedList();
        tableView = new TableAudio(Table.TABLE_ENUM.FILM, progData);

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPaneTableFilm.setFitToHeight(true);
        scrollPaneTableFilm.setFitToWidth(true);
        scrollPaneTableFilm.setContent(tableView);

        paneAudioInfo = new PaneAudioInfo(ProgConfig.AUDIO_PANE_AUDIO_INFO_DIVIDER);

        ArrayList<P2InfoDto> list = new ArrayList<>();
        P2InfoDto infoDto = new P2InfoDto(paneAudioInfo,
                ProgConfig.AUDIO__INFO_PANE_IS_RIP,
                ProgConfig.AUDIO__INFO_DIALOG_SIZE, ProgData.AUDIO_TAB_ON,
                "Info", "Audio", false,
                progData.maskerPane.visibleProperty());
        list.add(infoDto);
        infoController = new P2InfoController(list, ProgConfig.AUDIO__INFO_IS_SHOWING);

        ProgConfig.AUDIO__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.AUDIO__INFO_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());

        setInfoPane();
        initTable();
        initListener();
    }

    public void isShown() {
        setAudioInfos();
        tableView.requestFocus();
    }

    public int getCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public ArrayList<AudioData> getSelList() {
        final ArrayList<AudioData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            P2Alert.showInfoNoSelection();
        }
        return ret;
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
                P2Alert.showInfoNoSelection();
            }
            return Optional.empty();
        }
    }

    public void showAudioInfo() {
        AudioInfoDialogController.getInstanceAndShow().showAudioInfo();
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.FILM);
    }

    public void refreshTable() {
        P2TableFactory.refreshTable(tableView);
    }

    public void setShown(boolean set) {
        // aus dem Menü/Kontext Tabelle
        final ArrayList<AudioData> list = getSelList();
        if (list.isEmpty()) {
            return;
        }
        AudioToolsFactory.setFilmShown(list, set);
    }

    public void bookmarkAudio(boolean bookmark) {
        final ArrayList<AudioData> list = getSelList();
        if (!list.isEmpty()) {
            AudioToolsFactory.bookmarkFilmList(list, bookmark);
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
        PListener.addListener(new PListener(PListener.EVENT_BLACKLIST_CHANGED, this.getClass().getSimpleName()) {
            @Override
            public void pingFx() {
                P2TableFactory.refreshTable(tableView);
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
                P2Log.sysLog("STRG-A: lange Liste -> verhindern");
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

    public void copyFilmThemeTitle(boolean theme) {
        final Optional<AudioData> filmSelection = getSel(false);
        filmSelection.ifPresent(mtp -> P2SystemUtils.copyToClipboard(theme ? mtp.getTheme() : mtp.getTitle()));
    }

    private void setAudioInfos() {
        setAudioInfos(tableView.getSelectionModel().getSelectedItem());
    }

    private void setAudioInfos(AudioData audios) {
        // Film in FilmInfoDialog setzen
        paneAudioInfo.setAudioData(audios); // todo nur wenn sichtbar
        AudioInfoDialogController.getInstance().setAudio(audios);
    }

    private void setInfoPane() {
        P2ClosePaneFactory.setSplit(boundInfo, splitPane,
                infoController, false, scrollPaneTableFilm,
                ProgConfig.AUDIO__INFO_DIVIDER, ProgConfig.AUDIO__INFO_IS_SHOWING);
    }
}
