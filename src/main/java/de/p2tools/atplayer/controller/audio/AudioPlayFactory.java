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


package de.p2tools.atplayer.controller.audio;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadConstants;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.tools.date.P2Date;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AudioPlayFactory {
    private AudioPlayFactory() {
    }

    public static void playAudio() {
        final Optional<AudioData> filmSelection = ProgData.getInstance().audioGuiController.getSel();
        if (filmSelection.isEmpty()) {
            return;
        }
        playAudio(filmSelection.get());
    }

    public static void playAllAudios() {
        List<AudioData> audioDataList = ProgData.getInstance().audioGuiController.getSelList();
        if (audioDataList.isEmpty()) {
            return;
        }

        final String url = buildUrl(audioDataList);
        play(url);
        ProgData.getInstance().historyList.addFilmDataListToHistory(audioDataList);
    }

    public static void playAudio(AudioData audioData) {
        if (audioData == null) {
            return;
        }


        final String url = audioData.getUrl();
        play(url);

        List<AudioData> audioDataList = new ArrayList<>();
        audioDataList.add(audioData);
        ProgData.getInstance().historyList.addFilmDataListToHistory(audioDataList);
    }

    public static void playAudio(DownloadData downloadData) {
        if (downloadData == null) {
            return;
        }

        String url = downloadData.getDestPathFile();
        play(url);

        List<DownloadData> list = new ArrayList<>();
        list.add(downloadData);
        ProgData.getInstance().historyList.addDownloadDataListToHistory(list);
    }

    private static String buildUrl(List<AudioData> films) {
        // die URL bauen
        if (films.size() <= 1) {
            return films.get(0).getUrl();
        }

        StringBuilder url = new StringBuilder();
        boolean append = false;
        for (AudioData audioData : films) {
            String u = audioData.getUrl();
            if (!append) {
                append = true;
            } else {
                url.append(DownloadConstants.TRENNER_PROG_ARRAY);
            }
            url.append(u);
        }
        return url.toString();
    }


    private static String getProgParameterArray(String url) {
        //Zieldatei und Pfad bauen und eintragen
        String progArray = "";
        try {
            progArray = getPlayParameterArray();
            progArray = replaceExec(url, progArray);
        } catch (final Exception ex) {
            P2Log.errorLog(987512098, ex);
        }
        return progArray;
    }

    private static String getPlayParameterArray() {
        StringBuilder ret = new StringBuilder(ProgConfig.SYSTEM_PROG_PLAY.getValueSafe());
        String progParameter = ProgConfig.SYSTEM_PROG_PLAY_PARAMETER.getValueSafe();

        final String[] ar = progParameter.split(" ");
        for (final String s : ar) {
            ret.append(DownloadConstants.TRENNER_PROG_ARRAY).append(s);
        }
        return ret.toString();
    }

    private static String replaceExec(String url, String execString) {
        execString = execString.replace("%f", url);
        return execString;
    }

    private static void play(String file) {
        String strProgCallArray = "";
        strProgCallArray = getProgParameterArray(file);
        String[] arrProgCallArray = strProgCallArray.split(DownloadConstants.TRENNER_PROG_ARRAY);

        P2Open.playStoredFilm(arrProgCallArray, ProgConfig.SYSTEM_PROG_PLAY,
                file, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        startMsg(file, arrProgCallArray);
    }

    static void startMsg(String url, String[] arrProgCallArray) {
        ArrayList<String> list = new ArrayList<>();
        list.add(P2Log.LILNE2);
        list.add("Audio abspielen");
        list.add("URL: " + url);
        list.add("Startzeit: " + P2DateConst.F_FORMAT_HH__mm__ss.format(new P2Date()));
        list.add("Programmaufruf[]: " + arrProgCallArray);
        list.add(P2Log.LILNE3);
        P2Log.sysLog(list);
    }
}
