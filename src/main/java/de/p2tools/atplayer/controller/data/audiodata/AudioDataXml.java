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

package de.p2tools.atplayer.controller.data.audiodata;

import org.apache.commons.lang3.time.FastDateFormat;

public class AudioDataXml {
    static final FastDateFormat sdf_date = FastDateFormat.getInstance("dd.MM.yyyy");

    // für JSON
    public static final int JSON_AUDIO_CHANNEL = 0;
    public static final int JSON_AUDIO_GENRE = 1;
    public static final int JSON_AUDIO_THEME = 2;
    public static final int JSON_AUDIO_TITLE = 3;
    public static final int JSON_AUDIO_DATE = 4;
    public static final int JSON_AUDIO_TIME = 5;
    public static final int JSON_AUDIO_DURATION = 6;
    public static final int JSON_AUDIO_SIZE_MB = 7;
    public static final int JSON_AUDIO_DESCRIPTION = 8;
    public static final int JSON_AUDIO_URL = 9;
    public static final int JSON_AUDIO_WEBSITE = 10;
    public static final int JSON_AUDIO_NEW = 11;
    public static final int JSON_MAX_ELEM = 12;

    public static final String JSON_TAG = "Audios";
    public static final String[] JSON_COLUMN_NAMES = {
            "Sender",
            "Genre",
            "Thema",
            "Titel",
            "Datum",
            "Zeit",
            "Dauer",
            "Größe",
            "Beschreibung",
            "Url",
            "Website",
            "Neu",
    };

    // für die Tabelle der Audios
    public static final int AUDIO_NR = 0;
    public static final int AUDIO_CHANNEL = 1;
    public static final int AUDIO_GENRE = 2;
    public static final int AUDIO_THEME = 3;
    public static final int AUDIO_TITLE = 4;
    public static final int AUDIO_PLAY = 5;
    public static final int AUDIO_RECORD = 6;
    public static final int AUDIO_DATE = 7;
    public static final int AUDIO_TIME = 8;
    public static final int AUDIO_DURATION = 9;
    public static final int AUDIO_SIZE_MB = 10;
    public static final int AUDIO_DESCRIPTION = 11;
    public static final int AUDIO_URL = 12;
    public static final int AUDIO_WEBSITE = 13;
    public static final int AUDIO_NEW = 14;
    public static final int AUDIO_DATE_LONG = 15;
    public static final int MAX_ELEM = 16;

    public static final String[] COLUMN_NAMES = {
            "Nr",
            "Sender",
            "Genre",
            "Thema",
            "Titel",
            "",
            "",
            "Datum",
            "Zeit",
            "Dauer [min]",
            "Größe [MB]",
            "Beschreibung",
            "Url",
            "Website",
            "Neu",
            "DatumL",
    };

    public final String[] arr = new String[]{
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""}; // ist einen Tick schneller, hoffentlich :)
}
