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

import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.icons.P2Icon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProgIcons {
    public static String ICON_PATH = "res/program/";
    public static String ICON_PATH_LONG = "de/p2tools/atplayer/res/program/";

    private static final List<PIcon> iconList = new ArrayList<>();

    public static PIcon ICON_DIALOG_ON = new PIcon(ICON_PATH_LONG, ICON_PATH, "dialog-ein.png", 16, 16);
    public static PIcon IMAGE_ACHTUNG_64 = new PIcon(ICON_PATH_LONG, ICON_PATH, "achtung_64.png", 64, 64);

    public static PIcon ICON_BUTTON_RESET = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-reset.png", 16, 16);
    public static PIcon ICON_BUTTON_PROPOSE = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-propose.png", 16, 16);
    public static PIcon ICON_BUTTON_BACKWARD = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-backward.png", 16, 16);
    public static PIcon ICON_BUTTON_FORWARD = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-forward.png", 16, 16);
    public static PIcon ICON_BUTTON_QUIT = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-quit.png", 16, 16);
    public static PIcon ICON_BUTTON_FILE_OPEN = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-file-open.png", 16, 16);
    public static PIcon ICON_DIALOG_QUIT = new PIcon(ICON_PATH_LONG, ICON_PATH, "dialog-quit.png", 64, 64);
    public static PIcon IMAGE_TABLE_FILM_PLAY = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-film-play.png", 14, 14);
    public static PIcon IMAGE_TABLE_FILM_SAVE = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-film-save.png", 14, 14);
    public static PIcon IMAGE_TABLE_FILM_BOOKMARK = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-film-bookmark.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-start.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_DEL = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-del.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_STOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-stop.png", 14, 14);
    public static PIcon IMAGE_TABLE_DOWNLOAD_OPEN_DIR = new PIcon(ICON_PATH_LONG, ICON_PATH, "table-download-open-dir.png", 14, 14);

    public static PIcon ICON_BUTTON_STOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-stop.png", 16, 16);
    public static PIcon ICON_BUTTON_NEXT = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-next.png", 16, 16);
    public static PIcon ICON_BUTTON_PREV = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-prev.png", 16, 16);
    public static PIcon ICON_BUTTON_REMOVE = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-remove.png", 16, 16);
    public static PIcon ICON_BUTTON_ADD = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-add.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_DOWN = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-down.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_UP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-up.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_TOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-top.png", 16, 16);
    public static PIcon ICON_BUTTON_MOVE_BOTTOM = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-move-bottom.png", 16, 16);
    public static PIcon ICON_BUTTON_CLEAN = new PIcon(ICON_PATH_LONG, ICON_PATH, "clean_16.png", 16, 16);
    public static PIcon ICON_BUTTON_DOWNLOAD_CLEAN = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-download-clean.png", 16, 16);
    public static PIcon ICON_BUTTON_DOWNLOAD_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-download-start.png", 16, 16);
    public static PIcon ICON_BUTTON_DOWNLOAD_START_ALL = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-download-start-all.png", 16, 16);
    public static PIcon ICON_BUTTON_DOWNLOAD_STOP = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-download-stop.png", 16, 16);
    public static PIcon ICON_BUTTON_DOWNLOAD_DEL = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-download-del.png", 16, 16);
    public static PIcon ICON_BUTTON_DOWNLOAD_EDIT = new PIcon(ICON_PATH_LONG, ICON_PATH, "button-download-edit.png", 16, 16);

    public static PIcon ICON_TOOLBAR_AUDIO_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-audio-start.png", 32, 32);
    public static PIcon ICON_TOOLBAR_AUDIO_ALL_START = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-audio-all-start.png", 32, 32);
    public static PIcon ICON_TOOLBAR_AUDIO_REC = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-audio-rec.png", 32, 32);
    public static PIcon ICON_TOOLBAR_FILTER = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-filter.png", 32, 32);
    public static PIcon ICON_TOOLBAR_INFO = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-info.png", 32, 32);
    public static PIcon ICON_TOOLBAR_AUDIO_BOOKMARK_FILTER = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-audio-bookmark-filter.png", 32, 32);
    public static PIcon ICON_TOOLBAR_AUDIO_DEL_ALL_BOOKMARK = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-audio-del-all-bookmark.png", 32, 32);


    public static PIcon FX_ICON_TOOLBAR_MENU = new PIcon(ICON_PATH_LONG, ICON_PATH, "toolbar-menu.png", 18, 15);

    public static void initIcons() {
        iconList.forEach(p -> {
            String url = p.genUrl(PIcon.class, ATPlayerController.class, ProgConst.class, ProgIcons.class, P2LibConst.class);
            if (url.isEmpty()) {
                // dann wurde keine gefunden
                System.out.println("ProgIconsInfo: keine URL, icon: " + p.getPathFileNameDark() + " - " + p.getFileName());
            }
        });
    }

    public static class PIcon extends P2Icon {
        public PIcon(String longPath, String path, String fileName, int w, int h) {
            super(longPath, path, fileName, w, h);
            iconList.add(this);
        }

        public boolean searchUrl(String p, Class<?>... clazzAr) {
            URL url;
            url = ATPlayerController.class.getResource(p);
            if (set(url, p, "ATPlayerController.class.getResource")) return true;
            url = ProgConst.class.getResource(p);
            if (set(url, p, "ProgConst.class.getResource")) return true;
            url = ProgIcons.class.getResource(p);
            if (set(url, p, "ProgIcons.class.getResource")) return true;
            url = this.getClass().getResource(p);
            if (set(url, p, "this.getClass().getResource")) return true;

            url = ClassLoader.getSystemResource(p);
            if (set(url, p, "ClassLoader.getSystemResource")) return true;
            url = P2LibConst.class.getClassLoader().getResource(p);
            if (set(url, p, "P2LibConst.class.getClassLoader().getResource")) return true;
            url = ProgConst.class.getClassLoader().getResource(p);
            if (set(url, p, "ProgConst.class.getClassLoader().getResource")) return true;
            url = this.getClass().getClassLoader().getResource(p);
            if (set(url, p, "this.getClass().getClassLoader().getResource")) return true;

            return false;
        }
    }
}
