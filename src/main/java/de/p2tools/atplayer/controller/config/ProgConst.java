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

package de.p2tools.atplayer.controller.config;

public class ProgConst {

    public static final String PROGRAM_NAME = "ATPlayer";
    public static final String USER_AGENT_DEFAULT = "";
    public static final int MAX_USER_AGENT_SIZE = 100;

    // settings file
    public static final String CONFIG_FILE = "atp.xml";
    public static final String STYLE_FILE = "style.css";
    //    public static final String CONFIG_FILE_COPY = "atp.xml_copy_";
    public static final String CONFIG_DIRECTORY = "p2Atplayer"; // im Homeverzeichnis

    public static final String FILE_HISTORY = "history.txt";
    public static final String FILE_BOOKMARKS = "bookmarks.txt";
    public static final String LOG_DIR = "Log";
    public static final String CSS_FILE = "de/p2tools/atplayer/atp.css";
    public static final String CSS_FILE_DARK_THEME = "de/p2tools/atplayer/atp-dark.css";

    public static final String PROGRAM_ICON = "de/p2tools/atplayer/res/p2a_logo_32.png";

    public static final int SYSTEM_LOAD_FILMLIST_MAX_DAYS = 100; // Filter, nur Audios der letzten xx Tage laden
    public static final int SYSTEM_LOAD_FILMLIST_MIN_DURATION = 30; // Filter, nur Audios mit mind. xx Minuten länge laden

    public static final int SYSTEM_FILTER_MAX_WAIT_TIME = 2_000; // 1.000 ms
    // beim Programmstart wird die Audioliste geladen wenn sie älter ist als ..
    public static final int ALTER_FILMLISTE_TAGE_FUER_AUTOUPDATE = 1; // Tage

    // Website ATPlayer
    public static final String URL_WEBSITE = "https://www.p2tools.de";
    public static final String URL_WEBSITE_ATPLAYER = "https://www.p2tools.de/atplayer/";
    public static final String URL_WEBSITE_DOWNLOAD = "https://www.p2tools.de/atplayer/download.html";
    public static final String URL_WEBSITE_HELP = "https://www.p2tools.de/atplayer/manual/";

    public static final int SYSTEM_BLACKLIST_MAX_FILM_DAYS = 300; // Filter Blacklist: nur Filme der letzten xx Tage laden
    public static final int SYSTEM_BLACKLIST_MIN_FILM_DURATION = 100; // Filter Blacklist: nur Filme mit mind. xx Minuten länge laden

    // ProgrammUrls
    public static final String ADRESSE_WEBSITE_VLC = "https://www.videolan.org";

    // Dateien/Verzeichnisse
    public static final double GUI_AUDIO_DIVIDER_LOCATION = 0.7;
    public static final double GUI_FILTER_DIVIDER_LOCATION = 0.3;

    public final static int MAX_COPY_OF_BACKUPFILE = 5; // Maximum number of backup files to be stored.

    public static final int MIN_TABLE_HEIGHT = 200;
    public static final int MIN_TEXTAREA_HEIGHT_LOW = 50;

    public static final int MAX_SENDER_FILME_LADEN = 2;
    public static final int MIN_DATEI_GROESSE_FILM = 256 * 1000;
    public static final int MAX_DEST_PATH_IN_DIALOG_DOWNLOAD = 10;
    public static final double GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION = 0.3;
    public static final int LAENGE_DATEINAME_MAX = 200; // Standardwert für die Länge des Zieldateinamens
    public static final int LAENGE_FELD_MAX = 100; // Standardwert für die Länge des Feldes des

    public static final int MAX_FILTER_GO_BACK = 20;

    // Startnummer/Filmnummer/... wenn nicht vorhanden
    public static final int NUMBER_NOT_EXISTS = Integer.MAX_VALUE;

    public static final String ICON_COLOR_DARK_1 = "#ffffff";
    public static final String ICON_COLOR_DARK_2 = "#000080";
    public static final String ICON_COLOR_LIGHT_1 = "#333333";
    public static final String ICON_COLOR_LIGHT_2 = "#4d66cc";

    public static final String GUI_COLOR_DARK_1 = "#cccccc";
    public static final String GUI_COLOR_DARK_2 = "#000080";
    public static final String GUI_COLOR_LIGHT_1 = "#666666";
    public static final String GUI_COLOR_LIGHT_2 = "#4d66cc";

    public static final String GUI_BACKGROUND_DARK_1 = "#333333";
    public static final String GUI_BACKGROUND_DARK_2 = "#a1a1a1";
    public static final String GUI_BACKGROUND_LIGHT_1 = "#cccccc";
    public static final String GUI_BACKGROUND_LIGHT_2 = "#d8d8d8";

    public static final String GUI_TITLE_BAR_DARK_1 = "#666666";
    public static final String GUI_TITLE_BAR_DARK_2 = "#000080";
    public static final String GUI_TITLE_BAR_LIGHT_1 = "#d8d8d8";
    public static final String GUI_TITLE_BAR_LIGHT_2 = "#99b3ff";

    public static final String GUI_TITLE_BAR_SEL_DARK_1 = "#333333";
    public static final String GUI_TITLE_BAR_SEL_DARK_2 = "#414180";
    public static final String GUI_TITLE_BAR_SEL_LIGHT_1 = "#999999";
    public static final String GUI_TITLE_BAR_SEL_LIGHT_2 = "#4d66cc";

    public static final boolean GUI_BACKGROUND_TRANSPARENT_DARK_1 = true;
    public static final boolean GUI_BACKGROUND_TRANSPARENT_DARK_2 = false;
    public static final boolean GUI_BACKGROUND_TRANSPARENT_LIGHT_1 = true;
    public static final boolean GUI_BACKGROUND_TRANSPARENT_LIGHT_2 = false;

    public static final boolean GUI_TITLE_BAR_TRANSPARENT_DARK_1 = true;
    public static final boolean GUI_TITLE_BAR_TRANSPARENT_DARK_2 = false;
    public static final boolean GUI_TITLE_BAR_TRANSPARENT_LIGHT_1 = true;
    public static final boolean GUI_TITLE_BAR_TRANSPARENT_LIGHT_2 = false;

    public static final boolean GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_1 = false;
    public static final boolean GUI_TITLE_BAR_SEL_TRANSPARENT_DARK_2 = false;
    public static final boolean GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_1 = false;
    public static final boolean GUI_TITLE_BAR_SEL_TRANSPARENT_LIGHT_2 = false;


}
