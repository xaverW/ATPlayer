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

package de.p2tools.atplayer.controller;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.download.DownloadFactory;
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.tools.P2ShutDown;
import de.p2tools.p2lib.tools.log.P2LogMessage;
import javafx.application.Platform;

public class ProgQuit {

    private ProgQuit() {
    }

    /**
     * Quit the ATPlayer application
     */
    public static void quit() {
        saveConfig();
        exitProg();
    }

    /**
     * Quit the ATPlayer application and shutDown the computer
     */
    public static void quitShutDown() {
        saveConfig();
        P2ShutDown.shutDown();
        exitProg();
    }

    /**
     * Quit the ATPlayer application and show QuitDialog
     *
     * @param startWithWaiting starts the dialog with the masker pane
     */
    public static void quit(boolean startWithWaiting) {
        final ProgData progData = ProgData.getInstance();
        //dann Programm beenden
        saveConfig();
        exitProg();
    }

    private static void saveConfig() {
        if (ProgData.getInstance().primaryStage.isShowing()) {
            P2GuiSize.getSize(ProgConfig.SYSTEM_SIZE_GUI, ProgData.getInstance().primaryStage);
        }
        DownloadFactory.stopAllDownloads();
        writeTabSettings();
        ProgSave.saveAll();
        P2LogMessage.endMsg();
    }

    private static void exitProg() {
        // dann jetzt beenden -> Tschüss
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private static void writeTabSettings() {
        // Tabelleneinstellungen merken
        final ProgData progData = ProgData.getInstance();
        progData.audioGuiController.saveTable();
        progData.downloadGuiController.saveTable();
    }
}
