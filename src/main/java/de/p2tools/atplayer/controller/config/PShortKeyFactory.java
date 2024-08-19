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


package de.p2tools.atplayer.controller.config;

import de.p2tools.atplayer.ATPlayerFactory;
import de.p2tools.atplayer.controller.ProgQuit;
import de.p2tools.atplayer.controller.audio.AudioFactory;
import de.p2tools.atplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutKey;
import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class PShortKeyFactory {
    public static String SHORT_CUT_LEER = "              ";

    private PShortKeyFactory() {
    }

    private static void setInfo() {
        ATPlayerFactory.setInfos();
    }

    private static void setFilter() {
        ATPlayerFactory.setFilter();
    }

    private static void playAudio() {
        AudioFactory.playAudio();
    }

    private static void saveAudio() {
        AudioFactory.saveAllAudios();
    }

    private static void setShown() {
        ProgData.getInstance().audioGuiController.setShown(true);
    }

    private static void unsetShown() {
        ProgData.getInstance().audioGuiController.setShown(false);
    }

    private static void setBookmark() {
        ProgData.getInstance().audioGuiController.setBookmark(true);
    }

    private static void unsetBookmark() {
        ProgData.getInstance().audioGuiController.setBookmark(false);
    }

    private static void centerGui() {
        ProgData.getInstance().primaryStage.centerOnScreen();
    }

    private static void quitAndWait() {
        ProgQuit.quit(true);
    }

    private static void showAudioInfos() {
        ProgData.getInstance().audioGuiController.showAudioInfo();
    }

    private static void addBlackList() {
        new ConfigDialogController(ProgData.getInstance(), true);
    }

    public static void addShortKey(Scene scene) {
        P2ShortcutKey pShortcutKey;
        KeyCombination kc;
        Runnable rn;

        // quitt and wait
        pShortcutKey = PShortcut.SHORTCUT_QUIT_PROGRAM_WAIT;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::quitAndWait;
        scene.getAccelerators().put(kc, rn);

        // Center GUI
        pShortcutKey = PShortcut.SHORTCUT_CENTER_GUI;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::centerGui;
        scene.getAccelerators().put(kc, rn);

        // Info
        pShortcutKey = PShortcut.SHORTCUT_SHOW_INFOS;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::setInfo;
        scene.getAccelerators().put(kc, rn);

        // Filter
        pShortcutKey = PShortcut.SHORTCUT_SHOW_FILTER;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::setFilter;
        scene.getAccelerators().put(kc, rn);

        // AudioInfos
        pShortcutKey = PShortcut.SHORTCUT_INFO_AUDIO;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::showAudioInfos;
        scene.getAccelerators().put(kc, rn);

        // Play
        pShortcutKey = PShortcut.SHORTCUT_PLAY;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::playAudio;
        scene.getAccelerators().put(kc, rn);

        // Play all
        pShortcutKey = PShortcut.SHORTCUT_PLAY_ALL;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::playAudio;
        scene.getAccelerators().put(kc, rn);

        // Save
        pShortcutKey = PShortcut.SHORTCUT_SAVE;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::saveAudio;
        scene.getAccelerators().put(kc, rn);

        // shown
        pShortcutKey = PShortcut.SHORTCUT_AUDIO_SHOWN;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::setShown;
        scene.getAccelerators().put(kc, rn);

        // unset shown
        pShortcutKey = PShortcut.SHORTCUT_AUDIO_NOT_SHOWN;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::unsetShown;
        scene.getAccelerators().put(kc, rn);

        // bookmark
        pShortcutKey = PShortcut.SHORTCUT_AUDIO_BOOKMARK;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::setBookmark;
        scene.getAccelerators().put(kc, rn);

        // unset bookmark
        pShortcutKey = PShortcut.SHORTCUT_AUDIO_NOT_BOOKMARK;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::unsetBookmark;
        scene.getAccelerators().put(kc, rn);

        // set blacklist
        pShortcutKey = PShortcut.SHORTCUT_ADD_BLACKLIST;
        kc = KeyCodeCombination.keyCombination(pShortcutKey.getActShortcut());
        rn = PShortKeyFactory::addBlackList;
        scene.getAccelerators().put(kc, rn);
    }
}
