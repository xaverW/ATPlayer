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

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.pdata.P2Data;
import de.p2tools.p2lib.mtfilm.tools.FilmDate;
import de.p2tools.p2lib.tools.log.P2Log;

public class AudioData extends AudioDataProps implements P2Data {
    public String CHANNEL_STR = "";
    public String GENRE_STR = "";
    public String THEME_STR = "";
    public String TITLE_STR = "";

    public AudioData() {
    }

    @Override
    public String getTag() {
        return "";
    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public Config[] getConfigsArr() {
        return null;
    }

    public void init() {
        setNewAudio(Boolean.parseBoolean(arr[AUDIO_NEW]));
        preserveMemory();
        // Dateigröße
        audioSize.setAudioSize(arr[AUDIO_SIZE_MB]);
        // Dauer
        setLength();
        // Datum
        setDate();
    }

    private void preserveMemory() {
        // ================================
        // Speicher sparen
        arr[AUDIO_DATE] = arr[AUDIO_DATE].intern();
    }

    public void setLowerCase() {
        CHANNEL_STR = getChannel().toLowerCase();
        GENRE_STR = getGenre().toLowerCase();
        THEME_STR = getTheme().toLowerCase();
        TITLE_STR = getTitle().toLowerCase();
    }

    public void clearLowerCase() {
        CHANNEL_STR = "";
        GENRE_STR = "";
        THEME_STR = "";
        TITLE_STR = "";
    }

    private void setLength() {
        long durSecond;
        try {
            durSecond = 0;
            if (!arr[AUDIO_DURATION].isEmpty()) {
                final String[] parts = arr[AUDIO_DURATION].split(":");
                long power = 1;
                for (int i = parts.length - 1; i >= 0; i--) {
                    durSecond += Long.parseLong(parts[i]) * power;
                    power *= 60;
                }
            }
            setDur(durSecond);
        } catch (final Exception ex) {
            setDur(0);
            P2Log.errorLog(201659701, "Dauer: " + arr[AUDIO_DURATION]);
        }
    }

    private void setDur(long durSecond) {
        if (durSecond <= 0) {
            setDurationMinute(0);
            return;
        }
        int d = (int) (durSecond / 60);
        if (d <= 0) {
            d = 1;
        }
        setDurationMinute(d);
    }

    private void setDate() {
        date.setTime(0);
        if (!arr[AUDIO_DATE].isEmpty()) {
            // nur dann gibts ein Datum
            try {
                if (arr[AUDIO_DATE_LONG].isEmpty()) {
                    date = new FilmDate(sdf_date.parse(arr[AUDIO_DATE]).getTime());
                    arr[AUDIO_DATE_LONG] = String.valueOf(date.getTime() / 1000);
                } else {
                    final long l = Long.parseLong(arr[AUDIO_DATE_LONG]);
                    date = new FilmDate(l * 1000 /* sind SEKUNDEN!! */);
                }
            } catch (final Exception ex) {
                P2Log.errorLog(915236701, ex, new String[]{"Datum: " + arr[AUDIO_DATE]});
                date = new FilmDate(0);
                arr[AUDIO_DATE] = "";
            }
        }
    }

    public AudioData getCopy() {
        final AudioData ret = new AudioData();
        System.arraycopy(arr, 0, ret.arr, 0, arr.length);
        ret.date = date;
        ret.no = no;
        ret.audioSize = audioSize;
        ret.setDurationMinute(getDurationMinute());
        return ret;
    }
}
