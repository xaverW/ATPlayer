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

package de.p2tools.atplayer.gui;


import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.atplayer.ATPlayerFactory;
import de.p2tools.atplayer.controller.config.PShortKeyFactory;
import de.p2tools.atplayer.controller.config.PShortcut;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DownloadMenu {
    private final VBox vBox;
    private final ProgData progData;

    public DownloadMenu(VBox vBox) {
        this.vBox = vBox;
        progData = ProgData.getInstance();
        init();
    }

    public void init() {
        vBox.getChildren().clear();

        initMenu();
        initButton();
    }

    private void initButton() {
        // Button
        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(0);
        vBoxSpace.setMinHeight(0);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btnRefresh = new ToolBarButton(vBox,
                "Downloads aktualisieren", "Liste der Downloads aktualisieren", ProgIcons.ICON_TOOLBAR_DOWNLOAD_REFRESH.getImageView());

        vBox.getChildren().add(P2GuiTools.getVDistance(10));
        final ToolBarButton btnStart = new ToolBarButton(vBox,
                "Downloads Starten", "Markierte Downloads starten", ProgIcons.ICON_TOOLBAR_DOWNLOAD_START.getImageView());
        final ToolBarButton btnStartAll = new ToolBarButton(vBox,
                "Alle Downloads starten", "Alle Downloads starten", ProgIcons.ICON_TOOLBAR_DOWNLOAD_START_ALL.getImageView());

        vBox.getChildren().add(P2GuiTools.getVDistance(10));
        final ToolBarButton btnBack = new ToolBarButton(vBox,
                "Downloads zurückstellen", "Markierte Downloads zurückstellen", ProgIcons.ICON_TOOLBAR_DOWNLOAD_UNDO.getImageView());
        final ToolBarButton btnDel = new ToolBarButton(vBox,
                "Downloads löschen", "Markierte Downloads löschen", ProgIcons.ICON_TOOLBAR_DOWNLOAD_DEL.getImageView());
        final ToolBarButton btnChange = new ToolBarButton(vBox,
                "Downloads ändern", "Markierte Downloads ändern", ProgIcons.ICON_TOOLBAR_CONFIG.getImageView());
        final ToolBarButton btnClear = new ToolBarButton(vBox,
                "Downloads aufräumen", "Liste der Downloads aufräumen", ProgIcons.ICON_TOOLBAR_DOWNLOAD_CLEAN.getImageView());

        vBox.getChildren().add(P2GuiTools.getVDistance(10));
        final ToolBarButton btnPlayStoredAudio = new ToolBarButton(vBox,
                "Film Starten", "Gespeicherten Film abspielen", ProgIcons.ICON_TOOLBAR_START.getImageView());

        btnRefresh.setOnAction(a -> {
            progData.downloadList.resetPlacedBack();
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
        btnClear.setOnAction(a -> {
            progData.downloadList.cleanUpList();
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
        btnChange.setOnAction(a -> {
            progData.downloadGuiController.editDownloads();
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
        btnStart.setOnAction(a -> {
            progData.downloadGuiController.startDownload(false);
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
        btnStartAll.setOnAction(a -> {
            progData.downloadGuiController.startDownload(true);
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
        btnBack.setOnAction(a -> {
            progData.downloadGuiController.moveDownloadBack();
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
        btnDel.setOnAction(a -> {
            progData.downloadGuiController.deleteDownloads();
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
        btnPlayStoredAudio.setOnAction(a -> {
            progData.downloadGuiController.playStoredAudio();
            progData.downloadGuiController.tableView.refresh();
            progData.downloadGuiController.tableView.requestFocus();
        });
    }

    private void initMenu() {
        // MenuButton
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Downloadmenü anzeigen"));
        mb.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-0");

        final MenuItem miDownloadStart = new MenuItem("Downloads starten");
        miDownloadStart.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadGuiController.startDownload(false);
        });
        P2ShortcutWorker.addShortCut(miDownloadStart, PShortcut.SHORTCUT_DOWNLOAD_START);

        final MenuItem miDownloadStop = new MenuItem("Downloads stoppen");
        miDownloadStop.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadGuiController.stopDownloads(false);
        });
        P2ShortcutWorker.addShortCut(miDownloadStop, PShortcut.SHORTCUT_DOWNLOAD_STOP);

        final MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadGuiController.editDownloads();
        });
        P2ShortcutWorker.addShortCut(miChange, PShortcut.SHORTCUT_DOWNLOAD_CHANGE);

        final MenuItem miUndo = new MenuItem("Gelöschte wieder anlegen");
        miUndo.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadList.undoDownloads();
        });
        P2ShortcutWorker.addShortCut(miUndo, PShortcut.SHORTCUT_UNDO_DELETE);
        miUndo.disableProperty().bind(Bindings.isEmpty(progData.downloadList.getUndoList()));

        mb.getItems().addAll(miDownloadStart, miDownloadStop, miChange, miUndo);

        // Submenü "Download"
        final MenuItem miPrefer = new MenuItem("Downloads vorziehen");
        miPrefer.setOnAction(a -> progData.downloadGuiController.preferDownload());
        final MenuItem miPutBack = new MenuItem("Downloads zurückstellen");
        miPutBack.setOnAction(a -> progData.downloadGuiController.moveDownloadBack());
        final MenuItem miRemove = new MenuItem("Downloads aus Liste entfernen");
        miRemove.setOnAction(a -> progData.downloadGuiController.deleteDownloads());

        Menu submenuDownload = new Menu("Downloads");
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(submenuDownload);

        // Submenü "alle Downloads"
        final MenuItem mbStartAll = new MenuItem("Alle Downloads starten");
        mbStartAll.setOnAction(a -> progData.downloadGuiController.startDownload(true /* alle */));
        final MenuItem mbStopAll = new MenuItem("Alle Downloads stoppen");
        mbStopAll.setOnAction(a -> progData.downloadGuiController.stopDownloads(true /* alle */));
        final MenuItem mbStopWait = new MenuItem("Alle wartenden Downloads stoppen");
        mbStopWait.setOnAction(a -> progData.downloadGuiController.stopWaitingDownloads());

        final MenuItem mbClean = new MenuItem("Liste der Downloads aufräumen");
        mbClean.setOnAction(e -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.DOWNLOAD) {
                return;
            }
            progData.downloadList.cleanUpList();
        });
        P2ShortcutWorker.addShortCut(mbClean, PShortcut.SHORTCUT_DOWNLOADS_CLEAN_UP);

        Menu submenuAllDownloads = new Menu("Alle Downloads");
        submenuAllDownloads.getItems().addAll(mbStartAll, mbStopAll, mbStopWait, mbClean);
        mb.getItems().addAll(submenuAllDownloads);

        MenuItem miPlayUrl = new MenuItem("Audio (URL) abspielen");
        miPlayUrl.setOnAction(a -> progData.downloadGuiController.playUrl());
        MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> progData.downloadGuiController.copyUrl());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miPlayUrl, miCopyUrl);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        miShowFilter.setOnAction(a -> ATPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());
        miShowInfo.setOnAction(a -> ATPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }


}
