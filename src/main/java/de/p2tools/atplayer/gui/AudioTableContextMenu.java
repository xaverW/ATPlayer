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
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.tools.table.TableAudio;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.tools.P2SystemUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class AudioTableContextMenu {

    private final ProgData progData;
    private final AudioGuiController audioGuiController;
    private final TableAudio tableView;

    public AudioTableContextMenu(ProgData progData, AudioGuiController audioGuiController, TableAudio tableView) {
        this.progData = progData;
        this.audioGuiController = audioGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(AudioData film) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, film);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, AudioData audioData) {
        // Start/Save
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> AudioTools.playAudio(audioData));
        miStart.setDisable(audioData == null);
        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> AudioTools.saveAudio(audioData));
        miSave.setDisable(audioData == null);
        contextMenu.getItems().addAll(miStart, miSave);

        Menu mFilter = addFilter(audioData);// Filter
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(mFilter);

        Menu mCopyUrl = copyUrl(audioData);// URL kopieren
        contextMenu.getItems().addAll(mCopyUrl);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> audioGuiController.showAudioInfo());
        miFilmInfo.setDisable(audioData == null);

        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> {
            P2SystemUtils.copyToClipboard(audioData.getTitle());
        });
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> {
            P2SystemUtils.copyToClipboard(audioData.getTheme());
        });

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(/*miFilmsSetShown,*/ miFilmInfo, miCopyName, miCopyTheme);


        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(resetTable);
    }

    private Menu addFilter(AudioData film) {
        Menu submenuFilter = new Menu("Filter");
        if (film == null) {
            submenuFilter.setDisable(true);
            return submenuFilter;
        }

        final MenuItem miFilterChannel = new MenuItem("nach Sender filtern");
        miFilterChannel.setOnAction(event -> progData.actFilterWorker.getActFilterSettings().setChannel(film.getChannel()));

        final MenuItem miFilterTheme = new MenuItem("nach Thema filtern");
        miFilterTheme.setOnAction(event -> progData.actFilterWorker.getActFilterSettings().setTheme(film.getTheme()));

        final MenuItem miFilterTitle = new MenuItem("nach Titel filtern");
        miFilterTheme.setOnAction(event -> progData.actFilterWorker.getActFilterSettings().setTitle(film.getTheme()));

        final MenuItem miFilterChannelTheme = new MenuItem("nach Sender und Thema filtern");
        miFilterChannelTheme.setOnAction(event -> {
            progData.actFilterWorker.getActFilterSettings().setChannel(film.getChannel());
            progData.actFilterWorker.getActFilterSettings().setTheme(film.getTheme());
        });

        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender, und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.actFilterWorker.getActFilterSettings().setChannel(film.getChannel());
            progData.actFilterWorker.getActFilterSettings().setTitle(film.getTitle());
        });

        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterTitle, miFilterChannelTheme, miFilterChannelThemeTitle);
        return submenuFilter;
    }

    private Menu copyUrl(AudioData filmData) {
        final Menu subMenuURL = new Menu("Audio-URL kopieren");
        if (filmData == null) {
            subMenuURL.setDisable(true);
            return subMenuURL;
        }

        MenuItem item;
        item = new MenuItem("Audio-URL kopieren");
        item.setOnAction(a -> P2SystemUtils.copyToClipboard(filmData.getUrl()));
        subMenuURL.getItems().add(item);
        return subMenuURL;
    }
}
