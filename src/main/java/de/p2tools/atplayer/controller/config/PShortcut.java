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


package de.p2tools.atplayer.controller.config;

import de.p2tools.p2lib.tools.shortcut.P2ShortcutKey;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;

public class PShortcut {

    // Menü
    public static final P2ShortcutKey SHORTCUT_QUIT_PROGRAM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_QUIT_PROGRAM, ProgConfig.SHORTCUT_QUIT_PROGRAM_INIT,
                    "Programm beenden",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, " +
                            "wird in einem Dialog abgefragt, was getan werden soll.");

    public static final P2ShortcutKey SHORTCUT_QUIT_PROGRAM_WAIT =
            new P2ShortcutKey(ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT, ProgConfig.SHORTCUT_QUIT_PROGRAM_WAIT_INIT,
                    "Programm beenden, Downloads abwarten",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, " +
                            "wird dieser noch abgeschlossen und " +
                            "das Programm wartet auf den Download. Der Dialog mit der Abfrage " +
                            "was getan werden soll, wird aber übersprungen.");

    public static final P2ShortcutKey SHORTCUT_CENTER_GUI =
            new P2ShortcutKey(ProgConfig.SHORTCUT_CENTER_GUI, ProgConfig.SHORTCUT_CENTER_INIT,
                    "Center Programm",
                    "Das Programmfenster wird auf dem Bildschirm zentriert positioniert.");

    public static final P2ShortcutKey SHORTCUT_SHOW_FILTER =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SHOW_FILTER, ProgConfig.SHORTCUT_SHOW_FILTER_INIT,
                    "Filter anzeigen",
                    "Im Programm werden die Filter angezeigt.");

    public static final P2ShortcutKey SHORTCUT_SHOW_INFOS =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SHOW_INFOS, ProgConfig.SHORTCUT_SHOW_INFOS_INIT,
                    "Infos anzeigen",
                    "Unter der Tabelle werden die Infos anzeigen.");

    public static final P2ShortcutKey SHORTCUT_INFO_AUDIO =
            new P2ShortcutKey(ProgConfig.SHORTCUT_INFO_AUDIO, ProgConfig.SHORTCUT_INFO_AUDIO_INIT,
                    "Infos-Fenster der Audios anzeigen",
                    "In der Tabelle \"Audios\" werden die Infos des markierten Beitrags angezeigt.");

    // Tabelle Filme
    public static final P2ShortcutKey SHORTCUT_PLAY =
            new P2ShortcutKey(ProgConfig.SHORTCUT_PLAY, ProgConfig.SHORTCUT_PLAY_INIT,
                    "Beitrag abspielen",
                    "Der markierte Beitrag in der Tabelle \"Audios\" wird abgespielt.");

    public static final P2ShortcutKey SHORTCUT_PLAY_ALL =
            new P2ShortcutKey(ProgConfig.SHORTCUT_PLAY_ALL, ProgConfig.SHORTCUT_PLAY_ALL_INIT,
                    "Alle markierten Audios abspielen",
                    "Alle markierten Audios in der Tabelle \"Audios\" werden abgespielt.");

    public static final P2ShortcutKey SHORTCUT_SAVE =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SAVE, ProgConfig.SHORTCUT_SAVE_INIT,
                    "Beitrag speichern",
                    "Der markierte Beitrag in der Tabelle \"Audios\" wird gespeichert.");

    public static final P2ShortcutKey SHORTCUT_AUDIO_SHOWN =
            new P2ShortcutKey(ProgConfig.SHORTCUT_AUDION_SHOWN, ProgConfig.SHORTCUT_AUDIO_SHOWN_INIT,
                    "Audio als gesehen markieren",
                    "Der Beitrag wird zur Liste der gehörten Audios hinzugefügt.");

    public static final P2ShortcutKey SHORTCUT_AUDIO_NOT_SHOWN =
            new P2ShortcutKey(ProgConfig.SHORTCUT_AUDIO_NOT_SHOWN, ProgConfig.SHORTCUT_AUDIO_NOT_SHOWN_INIT,
                    "Audio als ungesehen markieren",
                    "Der Beitrag wird aus der Liste der gehörten Audios gelöscht.");

    public static final P2ShortcutKey SHORTCUT_AUDIO_BOOKMARK =
            new P2ShortcutKey(ProgConfig.SHORTCUT_AUDION_BOOKMARK, ProgConfig.SHORTCUT_AUDIO_BOOKMARK_INIT,
                    "Bookmark für das Audio anlegen",
                    "Der Beitrag wird zur Liste der Bookmarks hinzugefügt.");

    public static final P2ShortcutKey SHORTCUT_AUDIO_NOT_BOOKMARK =
            new P2ShortcutKey(ProgConfig.SHORTCUT_AUDIO_NOT_BOOKMARK, ProgConfig.SHORTCUT_AUDIO_NOT_BOOKMARK_INIT,
                    "Bookmark des Audios löschen",
                    "Der Beitrag wird aus der Liste der Bookmarks gelöscht.");

    public static final P2ShortcutKey SHORTCUT_ADD_BLACKLIST =
            new P2ShortcutKey(ProgConfig.SHORTCUT_ADD_BLACKLIST, ProgConfig.SHORTCUT_ADD_BLACKLIST_INIT,
                    "Blacklist-Dialog öffnen",
                    "Den Blacklist-Dialog zum Ändern der Blacklist, öffnen");

    public static final P2ShortcutKey SHORTCUT_ADD_BLACKLIST_THEME =
            new P2ShortcutKey(ProgConfig.SHORTCUT_ADD_BLACKLIST_THEME, ProgConfig.SHORTCUT_ADD_BLACKLIST_THEME_INIT,
                    "Thema direkt in die Blacklist einfügen",
                    "Einen neuen Eintrag mit dem exakten Thema in der Blacklist erstellen, " +
                            "der markierte Film dient dabei Vorlage.");

    public static final P2ShortcutKey SHORTCUT_COPY_AUDIO_THEME_TO_CLIPBOARD =
            new P2ShortcutKey(ProgConfig.SHORTCUT_COPY_AUDIO_THEME_TO_CLIPBOARD, ProgConfig.SHORTCUT_COPY_AUDIO_THEME_TO_CLIPBOARD_INIT,
                    "Audio-Thema in die Zwischenablage Kopieren",
                    "Das Thema des markierten Audios/Downloads in der " +
                            "Tabelle \"Audio\" und \"Downloads\" wird in die Zwischenablage kopiert.");
    public static final P2ShortcutKey SHORTCUT_COPY_AUDIO_TITLE_TO_CLIPBOARD =
            new P2ShortcutKey(ProgConfig.SHORTCUT_COPY_AUDIO_TITLE_TO_CLIPBOARD, ProgConfig.SHORTCUT_COPY_AUDIO_TITLE_TO_CLIPBOARD_INIT,
                    "Audio-Titel in die Zwischenablage Kopieren",
                    "Der Titel des markierten Audio/Downloads in der " +
                            "Tabelle \"Audio\" und \"Downloads\" wird in die Zwischenablage kopiert.");

    // Tabelle Download
    public static final P2ShortcutKey SHORTCUT_DOWNLOAD_START =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_START, ProgConfig.SHORTCUT_DOWNLOAD_START_INIT,
                    "Download starten",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestartet.");

    public static final P2ShortcutKey SHORTCUT_DOWNLOAD_STOP =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_STOP, ProgConfig.SHORTCUT_DOWNLOAD_STOP_INIT,
                    "Download stoppen",
                    "Der markierte Download in der Tabelle \"Downloads\" wird gestoppt.");

    public static final P2ShortcutKey SHORTCUT_DOWNLOAD_CHANGE =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_CHANGE, ProgConfig.SHORTCUT_DOWNLOAD_CHANGE_INIT,
                    "Download ändern",
                    "Der markierte Download in der Tabelle \"Downloads\" kann geändert werden.");

    public static final P2ShortcutKey SHORTCUT_UNDO_DELETE =
            new P2ShortcutKey(ProgConfig.SHORTCUT_UNDO_DELETE, ProgConfig.SHORTCUT_UNDO_DELETE_INIT,
                    "Gelöschte Downloads wieder anlegen",
                    "Die zuletzt gelöschten Downloads in der Tabelle \"Downloads\" " +
                            "können wieder angelegt werden.");

    public static final P2ShortcutKey SHORTCUT_DOWNLOADS_CLEAN_UP =
            new P2ShortcutKey(ProgConfig.SHORTCUT_DOWNLOAD_CLEAN_UP, ProgConfig.SHORTCUT_DOWNLOAD_CLEAN_UP_INIT,
                    "Downloads aufräumen",
                    "Die Liste der Downloads in der Tabelle \"Downloads\" wird aufgeräumt.");

    private static final ObservableList<P2ShortcutKey> shortcutList = FXCollections.observableArrayList();

    public PShortcut() {
        shortcutList.add(SHORTCUT_QUIT_PROGRAM);
        shortcutList.add(SHORTCUT_CENTER_GUI);
        shortcutList.add(SHORTCUT_SHOW_FILTER);
        shortcutList.add(SHORTCUT_SHOW_INFOS);
        shortcutList.add(SHORTCUT_INFO_AUDIO);

        shortcutList.add(SHORTCUT_PLAY);
        shortcutList.add(SHORTCUT_PLAY_ALL);
        shortcutList.add(SHORTCUT_SAVE);

        shortcutList.add(SHORTCUT_AUDIO_SHOWN);
        shortcutList.add(SHORTCUT_AUDIO_NOT_SHOWN);
        shortcutList.add(SHORTCUT_AUDIO_BOOKMARK);
        shortcutList.add(SHORTCUT_AUDIO_NOT_BOOKMARK);
        shortcutList.add(SHORTCUT_ADD_BLACKLIST);
        shortcutList.add(SHORTCUT_ADD_BLACKLIST_THEME);
        shortcutList.add(SHORTCUT_COPY_AUDIO_THEME_TO_CLIPBOARD);
        shortcutList.add(SHORTCUT_COPY_AUDIO_TITLE_TO_CLIPBOARD);

        shortcutList.add(SHORTCUT_DOWNLOAD_START);
        shortcutList.add(SHORTCUT_DOWNLOAD_STOP);
        shortcutList.add(SHORTCUT_DOWNLOAD_CHANGE);
        shortcutList.add(SHORTCUT_UNDO_DELETE);
        shortcutList.add(SHORTCUT_DOWNLOADS_CLEAN_UP);
    }

    public static synchronized ObservableList<P2ShortcutKey> getShortcutList() {
        return shortcutList;
    }

    public static synchronized boolean checkDoubleShortcutList() {
        HashSet<String> hashSet = new HashSet<>();
        for (P2ShortcutKey ps : shortcutList) {
            if (!hashSet.add(ps.getActShortcut())) {
                hashSet.clear();
                return true;
            }
        }
        hashSet.clear();
        return false;
    }
}
