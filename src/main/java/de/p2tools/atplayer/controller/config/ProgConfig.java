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

import de.p2tools.atplayer.controller.downloadtools.DownloadState;
import de.p2tools.atplayer.controller.filter.ActFilterWorker;
import de.p2tools.atplayer.controller.filter.AudioFilter;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.data.PDataProgConfig;
import de.p2tools.p2lib.mtdownload.GetProgramStandardPath;
import de.p2tools.p2lib.mtdownload.MLBandwidthTokenBucket;
import de.p2tools.p2lib.tools.PStringUtils;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.*;
import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgConfig extends PDataProgConfig {

    // Programm-Configs, änderbar nur im Konfig-File
    // ============================================
    // 250 Sekunden, wie bei Firefox
    public static int SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT = 250;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND = addIntProp("__system-parameter__download-timeout-second_250__", SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT);
    // max. Startversuche für fehlgeschlagene Downloads (insgesamt: restart * restart_http Versuche)
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT = 3;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = addIntProp("__system-parameter__download-max-restart_5__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT);
    // max. Startversuche für fehlgeschlagene Downloads, direkt beim Download
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT = 5;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP = addIntProp("__system-parameter__download-max-restart-http_10__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT);
    // Beim Dialog "Download weiterführen" wird nach dieser Zeit der Download weitergeführt
    public static int SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT = 60;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS = addIntProp("__system-parameter__download-continue-second_60__", SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT);
    // Beim Dialog "Automode" wird nach dieser Zeit der das Programm beendet
    public static int SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT = 15;
    public static IntegerProperty SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS = addIntProp("__system-parameter__automode-quitt-second_60__", SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT);
    // Downloadfehlermeldung wird xx Sedunden lang angezeigt
    public static int SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT = 30;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND = addIntProp("__system-parameter__download-errormsg-in-second_30__", SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT);
    // Downloadprogress im Terminal anzeigen
    public static BooleanProperty SYSTEM_PARAMETER_DOWNLOAD_PROGRESS = addBoolProp("__system-parameter__download_progress_", Boolean.TRUE);
    public static String PARAMETER_INFO = P2LibConst.LINE_SEPARATOR + "\t"
            + "\"__system-parameter__xxx\" können nur im Konfigfile geändert werden" + P2LibConst.LINE_SEPARATOR
            + "\t" + "und sind auch nicht für ständige Änderungen gedacht." + P2LibConst.LINE_SEPARATOR
            + "\t" + "Wird eine Zeile gelöscht, wird der Parameter wieder mit dem Standardwert angelegt."
            + P2LibConst.LINE_SEPARATOR
            + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Timeout für direkte Downloads, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, am Ende aller Downloads" + P2LibConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: " +
            SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, direkt beim Download," + P2LibConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Beim Dialog \"Download weiterführen\" wird nach dieser Zeit der Download weitergeführt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Beim Dialog \"Automode\" wird nach dieser Zeit der das Programm beendet, Standardwert: "
            + SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Downloadfehlermeldung wird xx Sedunden lang angezeigt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Downloadprogress im Terminal (-auto) anzeigen: "
            + SYSTEM_PARAMETER_DOWNLOAD_PROGRESS.getValue() + P2LibConst.LINE_SEPARATOR;
    // ===========================================
    // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
    public static StringProperty SYSTEM_PROG_VERSION = addStrProp("system-prog-version", ProgramToolsFactory.getProgVersion());
    public static StringProperty SYSTEM_PROG_BUILD_NO = addStrProp("system-prog-build-no", ProgramToolsFactory.getBuild());
    public static StringProperty SYSTEM_PROG_BUILD_DATE = addStrProp("system-prog-build-date", ProgramToolsFactory.getCompileDate());//z.B.: 27.07.2


    //Configs zur Anzeige der Diacritics in der Audioliste
    //TRUE: dann werden Diacritics entfernt
    public static BooleanProperty SYSTEM_REMOVE_DIACRITICS = addBoolProp("system-remove-diacritics", Boolean.FALSE);

    // Configs zum Aktualisieren beim Programmupdate
    public static BooleanProperty SYSTEM_AFTER_UPDATE_FILTER = addBoolProp("system-after-update-filter", Boolean.FALSE);

    // Configs zur Programmupdatesuche
    public static StringProperty SYSTEM_UPDATE_DATE = addStrProp("system-update-date"); // Datum der letzten Prüfung

    public static BooleanProperty SYSTEM_UPDATE_SEARCH_ACT = addBoolProp("system-update-search-act", Boolean.TRUE); //Infos und Programm
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_BETA = addBoolProp("system-update-search-beta", Boolean.FALSE); //beta suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_DAILY = addBoolProp("system-update-search-daily", Boolean.FALSE); //daily suchen

    public static StringProperty SYSTEM_UPDATE_LAST_INFO = addStrProp("system-update-last-info");
    public static StringProperty SYSTEM_UPDATE_LAST_ACT = addStrProp("system-update-last-act");
    public static StringProperty SYSTEM_UPDATE_LAST_BETA = addStrProp("system-update-last-beta");
    public static StringProperty SYSTEM_UPDATE_LAST_DAILY = addStrProp("system-update-last-daily");

    // ConfigDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_FILM = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_DOWNLOAD = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_PLAY = new SimpleIntegerProperty(-1);

    //Download
    public static StringProperty DOWNLOAD_DIALOG_PATH_SAVING = addStrProp("download-dialog-path-saving"); // gesammelten Downloadpfade im Downloaddialog
    public static IntegerProperty DOWNLOAD_MAX_BANDWIDTH_BYTE = addIntProp("download-max-bandwidth-byte", MLBandwidthTokenBucket.BANDWIDTH_RUN_FREE);
    public static StringProperty DOWNLOAD_DIALOG_ERROR_SIZE = addStrProp("download-dialog-error-size", "");
    public static IntegerProperty DOWNLOAD_MAX_DOWNLOADS = addIntProp("download-max-downloads", 1);
    public static IntegerProperty DOWNLOAD_CONTINUE = addIntProp("download-contineu", DownloadState.DOWNLOAD_RESTART__ASK);
    public static StringProperty DOWNLOAD_DIALOG_CONTINUE_SIZE = addStrProp("download-dialog-continue-size");
    public static BooleanProperty DOWNLOAD_SHOW_NOTIFICATION = addBoolProp("download-show-notification", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOW = addBoolProp("download-dialog-start-download-now", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOT = addBoolProp("download-dialog-start-download-not", Boolean.FALSE);
    public static IntegerProperty DOWNLOAD_BANDWIDTH_KBYTE = addIntProp("download-bandwidth-byte"); // da wird die genutzte Bandbreite gespeichert
    public static DoubleProperty DOWNLOAD_GUI_FILTER_DIVIDER = addDoubleProp("download-gui-filter-divider", ProgConst.GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION);
    public static StringProperty DOWNLOAD_GUI_TABLE_WIDTH = addStrProp("download-gui-table-width");
    public static StringProperty DOWNLOAD_GUI_TABLE_SORT = addStrProp("download-gui-table-sort");
    public static StringProperty DOWNLOAD_GUI_TABLE_UP_DOWN = addStrProp("download-gui-table-up-down");
    public static StringProperty DOWNLOAD_GUI_TABLE_VIS = addStrProp("download-gui-table-vis");
    public static StringProperty DOWNLOAD_GUI_TABLE_ORDER = addStrProp("download-gui-table-order");
    public static StringProperty DOWNLOAD_DIALOG_ADD_SIZE = addStrProp("download-dialog-add-size", "700:700");
    public static StringProperty DOWNLOAD_DIALOG_ADD_MORE_SIZE = addStrProp("download-dialog-add-more-size", "800:700");
    public static List<String> DOWNLOAD_DIALOG_DOWNLOAD_PATH = addListProp("download-dialog-download-path"); // gesammelten Downloadpfade im Downloaddialog

    //Download-SetDate
    public static String DOWNLOAD_FILE_PATH_INIT = PSystemUtils.getStandardDownloadPath();
    public static StringProperty DOWNLOAD_FILE_PATH = addStrProp("download-file-path", DOWNLOAD_FILE_PATH_INIT);
    public static String DOWNLOAD_FILE_NAME_INIT = "%t-%T-%Z.mp4";
    public static StringProperty DOWNLOAD_FILE_NAME = addStrProp("download-file-name", DOWNLOAD_FILE_NAME_INIT);
    public static BooleanProperty DOWNLOAD_ADD_INFO_FILE = addBoolProp("download-add-info-file", Boolean.FALSE);

    public static StringProperty SYSTEM_PROG_OPEN_DIR = addStrProp("system-prog-open-dir");
    public static StringProperty SYSTEM_PROG_OPEN_URL = addStrProp("system-prog-open-url");
    public static StringProperty SYSTEM_PROG_PLAY = addStrProp("system-prog-play", GetProgramStandardPath.getTemplatePathVlc());
    public static String SYSTEM_PROG_PLAY_PARAMETER_INIT = "%f";
    public static StringProperty SYSTEM_PROG_PLAY_PARAMETER = addStrProp("system-prog-play-parameter", SYSTEM_PROG_PLAY_PARAMETER_INIT);
    public static IntegerProperty SYSTEM_SAVE_MAX_SIZE = addIntProp("system-save-max-size", 150);
    public static IntegerProperty SYSTEM_SAVE_MAX_FIELD = addIntProp("system-save-max-field", 50);

    // AudioInfoDialog
    public static BooleanProperty AUDIO_INFO_DIALOG_SHOW_URL = addBoolProp("audio-info-dialog-show-url", Boolean.FALSE);
    public static BooleanProperty AUDIO_INFO_DIALOG_SHOW_WEBSITE_URL = addBoolProp("audio-info-dialog-show-website-url", Boolean.FALSE);
    public static BooleanProperty AUDIO_INFO_DIALOG_SHOW_DESCRIPTION = addBoolProp("audio-info-dialog-show-description", Boolean.TRUE);

    // Configs
    public static StringProperty SYSTEM_AUDIOLIST_DATE_TIME = addStrProp("system-audiolist-date", ""); // DateTimeFormatter DT_FORMATTER_dd_MM_yyyy___HH__mm
    public static StringProperty SYSTEM_USERAGENT = addStrProp("system-useragent", ProgConst.USER_AGENT_DEFAULT); //Useragent für direkte Downloads
    public static BooleanProperty SYSTEM_USE_REPLACETABLE = addBoolProp("system-use-replacetable", SystemUtils.IS_OS_LINUX ? Boolean.TRUE : Boolean.FALSE);
    public static BooleanProperty SYSTEM_ONLY_ASCII = addBoolProp("system-only-ascii", Boolean.FALSE);
    public static BooleanProperty SYSTEM_STYLE = addBoolProp("system-style", Boolean.FALSE);
    public static IntegerProperty SYSTEM_STYLE_SIZE = addIntProp("system-style-size", 14);
    public static StringProperty SYSTEM_LOG_DIR = addStrProp("system-log-dir", "");
    public static BooleanProperty SYSTEM_LOG_ON = addBoolProp("system-log-on", Boolean.TRUE);
    public static BooleanProperty SYSTEM_DARK_THEME = addBoolProp("system-dark-theme", Boolean.FALSE);
    public static BooleanProperty SYSTEM_THEME_CHANGED = addBoolProp("system-theme-changed");
    public static BooleanProperty SYSTEM_SSL_ALWAYS_TRUE = addBoolProp("system-ssl-always-true");
    public static BooleanProperty TIP_OF_DAY_SHOW = addBoolProp("tip-of-day-show", Boolean.TRUE);//Tips anzeigen
    public static StringProperty TIP_OF_DAY_WAS_SHOWN = addStrProp("tip-of-day-was-shown");//bereits angezeigte Tips
    public static StringProperty TIP_OF_DAY_DATE = addStrProp("tip-of-day-date"); //Datum des letzten Tips
    public static IntegerProperty SYSTEM_FILTER_WAIT_TIME = addIntProp("system-filter-wait-time", 100);
    public static BooleanProperty SYSTEM_FILTER_RETURN = addBoolProp("system-filter-return", Boolean.FALSE);
    public static StringProperty SYSTEM_DOWNLOAD_DIR_NEW_VERSION = addStrProp("system-download-dir-new-version", "");

    // Fenstereinstellungen
    public static StringProperty SYSTEM_SIZE_GUI = addStrProp("system-size-gui", "1000:800");
    public static StringProperty SYSTEM_SIZE_DIALOG_AUDIO_INFO = addStrProp("system-size-dialog-audio-info", "600:800");

    // Einstellungen Audioliste
    public static BooleanProperty SYSTEM_LOAD_FILMS_ON_START = addBoolProp("system-load-films-on-start", Boolean.TRUE);
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MAX_DAYS = addIntProp("system-load-filmlist-max-days", 0); //es werden nur die x letzten Tage geladen
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MIN_DURATION = addIntProp("system-load-filmlist-min-duration", 0); //es werden nur Audios mit mind. x Minuten geladen

    // Gui Audios
    public static DoubleProperty AUDIO_GUI_DIVIDER = addDoubleProp("audio-gui-divider", ProgConst.GUI_AUDIO_DIVIDER_LOCATION);
    public static BooleanProperty AUDIO_GUI_DIVIDER_ON = addBoolProp("audio-gui-divider-on", Boolean.TRUE);
    public static StringProperty AUDIO_GUI_TABLE_WIDTH = addStrProp("audio-gui-table-width");
    public static StringProperty AUDIO_GUI_TABLE_SORT = addStrProp("audio-gui-table-sort");
    public static StringProperty AUDIO_GUI_TABLE_UP_DOWN = addStrProp("audio-gui-table-up-down");
    public static StringProperty AUDIO_GUI_TABLE_VIS = addStrProp("audio-gui-table-vis");
    public static StringProperty AUDIO_GUI_TABLE_ORDER = addStrProp("audio-gui-table-order");
    public static DoubleProperty AUDIO_GUI_INFO_DIVIDER = addDoubleProp("audio-gui-info-divider", 0.7);

    // Gui Filter
    public static BooleanProperty AUDIO_GUI_FILTER_DIVIDER_ON = addBoolProp("film-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty AUDIO_GUI_FILTER_DIVIDER = addDoubleProp("film-gui-filter-divider", ProgConst.GUI_FILTER_DIVIDER_LOCATION);

    // ConfigDialog
    public static StringProperty CONFIG_DIALOG_SIZE = addStrProp("config-dialog-size", "900:700");
    public static BooleanProperty CONFIG_DIALOG_ACCORDION = addBoolProp("config_dialog-accordion", Boolean.TRUE);

    // StartDialog
    public static StringProperty START_DIALOG_DOWNLOAD_PATH = addStrProp("start-dialog-download-path", PSystemUtils.getStandardDownloadPath());

    // Shorcuts Hauptmenü
    public static String SHORTCUT_QUIT_PROGRAM_INIT = "Ctrl+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM = addStrProp("SHORTCUT_QUIT_PROGRAM", SHORTCUT_QUIT_PROGRAM_INIT);
    public static String SHORTCUT_QUIT_PROGRAM_WAIT_INIT = "Ctrl+Shift+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM_WAIT = addStrProp("SHORTCUT_QUIT_PROGRAM_WAIT", SHORTCUT_QUIT_PROGRAM_WAIT_INIT);

    // Shortcuts, Programmweit
    public static String SHORTCUT_CENTER_INIT = "Ctrl+W";
    public static StringProperty SHORTCUT_CENTER_GUI = addStrProp("SHORTCUT_CENTER_GUI", SHORTCUT_CENTER_INIT);

    // Shortcuts Filmmenü
    public static String SHORTCUT_SHOW_INFOS_INIT = "Alt+I";
    public static StringProperty SHORTCUT_SHOW_INFOS = addStrProp("SHORTCUT_SHOW_INFO", SHORTCUT_SHOW_INFOS_INIT);

    public static String SHORTCUT_AUDIO_SHOWN_INIT = "Ctrl+G";
    public static StringProperty SHORTCUT_AUDION_SHOWN = addStrProp("SHORTCUT_AUDIO_SHOWN", SHORTCUT_AUDIO_SHOWN_INIT);

    public static String SHORTCUT_AUDIO_NOT_SHOWN_INIT = "Ctrl+Shift+G";
    public static StringProperty SHORTCUT_AUDIO_NOT_SHOWN = addStrProp("SHORTCUT_AUDIO_NOT_SHOWN", SHORTCUT_AUDIO_NOT_SHOWN_INIT);

    public static String SHORTCUT_AUDIO_BOOKMARK_INIT = "Ctrl+B";
    public static StringProperty SHORTCUT_AUDION_BOOKMARK = addStrProp("SHORTCUT_AUDIO_BOOKMARK", SHORTCUT_AUDIO_BOOKMARK_INIT);

    public static String SHORTCUT_AUDIO_NOT_BOOKMARK_INIT = "Ctrl+Shift+B";
    public static StringProperty SHORTCUT_AUDIO_NOT_BOOKMARK = addStrProp("SHORTCUT_AUDIO_NOT_BOOKMARK", SHORTCUT_AUDIO_NOT_BOOKMARK_INIT);

    public static String SHORTCUT_SHOW_FILTER_INIT = "Alt+F";
    public static StringProperty SHORTCUT_SHOW_FILTER = addStrProp("SHORTCUT_SHOW_FILTER", SHORTCUT_SHOW_FILTER_INIT);

    public static String SHORTCUT_PLAY_ALL_INIT = "ALT+P";
    public static StringProperty SHORTCUT_PLAY_ALL = addStrProp("SHORTCUT_PLAY_ALL", SHORTCUT_PLAY_ALL_INIT);

    public static String SHORTCUT_INFO_AUDIO_INIT = "Ctrl+I";
    public static StringProperty SHORTCUT_INFO_AUDIO = addStrProp("SHORTCUT_INFO_FILM", SHORTCUT_INFO_AUDIO_INIT);

    public static String SHORTCUT_PLAY_INIT = "Ctrl+P";
    public static StringProperty SHORTCUT_PLAY = addStrProp("SHORTCUT_PLAY", SHORTCUT_PLAY_INIT);

    public static String SHORTCUT_SAVE_INIT = "Ctrl+S";
    public static StringProperty SHORTCUT_SAVE = addStrProp("SHORTCUT_SAVE", SHORTCUT_SAVE_INIT);

    private static ProgConfig instance;

    static {
        check(SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND, SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS, SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS, SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND, SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT, 5, 200);
    }

    private ProgConfig() {
        super("ProgConfig");
    }

    public static final ProgConfig getInstance() {
        return instance == null ? instance = new ProgConfig() : instance;
    }

    public static void addConfigData(ConfigFile configFile) {
        ProgData progData = ProgData.getInstance();

        // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
        ProgConfig.SYSTEM_PROG_VERSION.set(ProgramToolsFactory.getProgVersion());
        ProgConfig.SYSTEM_PROG_BUILD_NO.set(ProgramToolsFactory.getBuild());
        ProgConfig.SYSTEM_PROG_BUILD_DATE.set(ProgramToolsFactory.getCompileDate());

        configFile.addConfigs(ProgConfig.getInstance());//Progconfig
        configFile.addConfigs(ProgColorList.getInstance());//Color

        final AudioFilter akt_sf = progData.actFilterWorker.getActFilterSettings();//akt-Filter
        akt_sf.setName(ActFilterWorker.SELECTED_FILTER_NAME);// nur zur Info im Config-File
        configFile.addConfigs(akt_sf);

        configFile.addConfigs(progData.replaceList);
        configFile.addConfigs(progData.downloadList);
    }

    public static void logAllConfigs() {
        final ArrayList<String> list = new ArrayList<>();

        list.add(PARAMETER_INFO);

        list.add(P2Log.LILNE2);
        list.add("Programmeinstellungen");
        list.add("===========================");
        Arrays.stream(ProgConfig.getInstance().getConfigsArr()).forEach(c -> {
            String s = c.getKey();
            if (s.startsWith("_")) {
                while (s.length() < 55) {
                    s += " ";
                }
            } else {
                while (s.length() < 35) {
                    s += " ";
                }
            }

            list.add(s + "  " + c.getActValueString());
        });
        list.add(P2Log.LILNE2);
        PStringUtils.appendString(list, "|  ", "=");

        list.add(P2Log.LILNE1);
        P2Log.debugLog(list);
    }

    private static synchronized void check(IntegerProperty mlConfigs, int init, int min, int max) {
        final int v = mlConfigs.getValue();
        if (v < min || v > max) {
            mlConfigs.setValue(init);
        }
    }
}
