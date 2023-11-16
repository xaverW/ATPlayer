/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


import de.p2tools.p2lib.data.PColorData;
import de.p2tools.p2lib.data.PColorList;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.List;

public class ProgColorList extends PColorList {

    // Tabelle Audio
    public static final PColorData AUDIO_NEW = addNewKey("COLOR_AUDIO_NEW",
            Color.rgb(0, 0, 240), Color.rgb(0, 0, 240), "Tabelle Beiträge, neue");
    public static final PColorData AUDIO_HISTORY = addNewKey("COLOR_AUDIO_HISTORY",
            Color.rgb(213, 215, 233), Color.rgb(166, 183, 224), "Tabelle Audios, gesehen");
    public static final PColorData AUDIO_BOOKMARK = addNewKey("COLOR_AUDIO_BOKMARK",
            Color.rgb(255, 236, 151), Color.rgb(177, 164, 105), "Tabelle Audios, Bookmarks");

    public static final PColorData ERROR = new PColorData("COLOR_ERROR",
            Color.rgb(255, 233, 233), Color.rgb(163, 82, 82));

    // DialogDownload
    public static final PColorData DOWNLOAD_NAME_ERROR = new PColorData("COLOR_DOWNLOAD_NAME_ERROR",
            Color.rgb(255, 233, 233), Color.rgb(200, 183, 183));

    // Tabelle Downloads
    public static final PColorData DOWNLOAD_WAIT = addNewKey("COLOR_DOWNLOAD_WAIT",
            Color.rgb(239, 244, 255), Color.rgb(99, 100, 105), "Tabelle Download, noch nicht gestartet");
    public static final PColorData DOWNLOAD_RUN = addNewKey("COLOR_DOWNLOAD_RUN",
            Color.rgb(255, 245, 176), Color.rgb(174, 150, 85), "Tabelle Download, läuft");
    public static final PColorData DOWNLOAD_FINISHED = addNewKey("COLOR_DOWNLOAD_FINISHED",
            Color.rgb(206, 255, 202), Color.rgb(79, 129, 74), "Tabelle Download, fertig");
    public static final PColorData DOWNLOAD_ERROR = addNewKey("COLOR_DOWNLOAD_ERROR",
            Color.rgb(255, 233, 233), Color.rgb(163, 82, 82), "Tabelle Download, fehlerhaft");

    //=======================================================================================
    //Liste der Schrift-Farben -> Rest sind Hintergrundfarben
    public static final List<PColorData> FRONT_COLOR = List.of(AUDIO_NEW);

    private ProgColorList() {
        super();
    }

    public static void setColorTheme() {
        final boolean dark = ProgConfig.SYSTEM_DARK_THEME.get();
        for (int i = 0; i < getInstance().size(); ++i) {
            getInstance().get(i).setColorTheme(dark);
        }
    }

    public static ObservableList<PColorData> getColorListFront() {
        ObservableList<PColorData> list = FXCollections.observableArrayList();
        ObservableList<PColorData> pColorData = getInstance();
        pColorData.stream().filter(pc -> FRONT_COLOR.contains(pc)).forEach(pc -> list.add(pc));

        Comparator<PColorData> comparator = Comparator.comparing(PColorData::getText);
        FXCollections.sort(list, comparator);

        return list;
    }

    public static ObservableList<PColorData> getColorListBackground() {
        ObservableList<PColorData> list = FXCollections.observableArrayList();
        ObservableList<PColorData> pColorData = getInstance();
        pColorData.stream().filter(pc -> !FRONT_COLOR.contains(pc)).forEach(pc -> list.add(pc));

        Comparator<PColorData> comparator = Comparator.comparing(PColorData::getText);
        FXCollections.sort(list, comparator);

        return list;
    }

    public synchronized static PColorList getInstance() {
        return PColorList.getInst();
    }


    public static void setColorData(String key, String value) {
        try {
            ObservableList<PColorData> list = getInstance();
            list.stream().forEach(pColorData -> {
                if (pColorData.getKey().equals(key)) {
                    Color c = Color.web(value);
                    if (value.endsWith("_DARK")) {
                        pColorData.setColorDark(c);
                    } else {
                        pColorData.setColorLight(c);
                    }
                }
            });
        } catch (Exception ex) {
            PLog.errorLog(956410210, "setColorData");
        }
    }
}
