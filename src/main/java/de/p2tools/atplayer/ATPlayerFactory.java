/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.atplayer;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.atplayer.gui.configdialog.ConfigDialogController;

public class ATPlayerFactory {
    private ATPlayerFactory() {
    }

    public static void centerGui() {
        ProgData.getInstance().primaryStage.centerOnScreen();
    }

    public static void setFilter() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.getValue());
                break;
            case DOWNLOAD:
                ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.getValue());
                break;
        }
    }

    public static void setInfos() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                ProgConfig.AUDIO_GUI_INFO_ON.setValue(!ProgConfig.AUDIO_GUI_INFO_ON.getValue());
                break;
            case DOWNLOAD:
                ProgConfig.DOWNLOAD_GUI_INFO_ON.setValue(!ProgConfig.DOWNLOAD_GUI_INFO_ON.getValue());
                break;
        }
    }

    public static void showFilmInfos() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                ProgData.getInstance().audioGuiController.showAudioInfo();
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadGuiController.showAudioInfo();
                break;
        }
    }

    public static void copyTheme() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                ProgData.getInstance().audioGuiController.copyFilmThemeTitle(true);
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadGuiController.copyFilmThemeTitle(true);
                break;
        }
    }

    public static void copyTitle() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                ProgData.getInstance().audioGuiController.copyFilmThemeTitle(false);
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadGuiController.copyFilmThemeTitle(false);
                break;
        }
    }

    public static void addBlacklist() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                BlacklistFactory.addBlackFilm(true);
                break;
            case DOWNLOAD:
                BlacklistFactory.addBlackFilm(false);
                break;
        }
    }

    public static void addBlacklistTheme() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                BlacklistFactory.addBlackThemeFilm();
                break;
            case DOWNLOAD:
                BlacklistFactory.addBlackThemeDownload();
                break;
        }
    }

    public static void showBlacklist() {
        new ConfigDialogController(ProgData.getInstance(), true);
    }

    public static void undoDels() {
        switch (ATPlayerController.paneShown) {
            case AUDIO:
                break;
            case DOWNLOAD:
                ProgData.getInstance().downloadList.undoDownloads();
                break;
        }
    }
}
