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

package de.p2tools.atplayer.controller.audio;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.atdata.AudioFactory;
import de.p2tools.p2lib.atdata.AudioList;
import de.p2tools.p2lib.atdata.ReadAudioListJson;
import de.p2tools.p2lib.mtdownload.MLHttpClient;
import de.p2tools.p2lib.mtfilm.tools.InputStreamProgressMonitor;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import de.p2tools.p2lib.mtfilm.tools.ProgressMonitorInputStream;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.date.P2DateGmtFactory;
import de.p2tools.p2lib.tools.date.P2LDateTimeFactory;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.P2Log;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadAudioList {

    private List<String> logList = new ArrayList<>();

    public ReadAudioList() {
    }

    public boolean readDb(boolean localList, Path path) {
        boolean ret;
        PDuration.counterStart("readDb");

        try {
            if (localList) {
                if (!Files.exists(path) || path.toFile().length() == 0) {
                    return false;
                }
                logList.add("Audioliste lesen");
                logList.add("   --> Lesen von: " + path);

                LoadAudioFactoryDto.audioListAkt.clear();
                LoadAudioFactoryDto.audioListNew.clear();
                processFromFile(path.toString(), LoadAudioFactoryDto.audioListNew);
                setDate();

                logList.add("##   Audioliste gelesen, OK");
                logList.add("##   Anzahl gelesen: " + LoadAudioFactoryDto.audioListNew.size());
                ret = true;

            } else {
                // Hash füllen
                fillHash(logList, LoadAudioFactoryDto.audioListAkt);
                fillHash(logList, LoadAudioFactoryDto.audioListNew);
                LoadAudioFactoryDto.audioListAkt.clear();
                LoadAudioFactoryDto.audioListNew.clear();

                //dann aus dem Web mit der URL laden
                logList.add("## Audioliste aus URL laden: " + AudioFactory.AUDIOLIST_URL);
                processFromWeb(new URL(AudioFactory.AUDIOLIST_URL), LoadAudioFactoryDto.audioListNew);

                if (LoadAudioFactoryDto.audioListNew.isEmpty()) {
                    // dann hats nicht geklappt
                    ret = false;
                } else {
                    setDate();
                    // unerwünschte löschen
                    removeUnwanted(logList, LoadAudioFactoryDto.audioListNew);
                    // neue Filme markieren
                    findAndMarkNewFilms(logList, LoadAudioFactoryDto.audioListNew);

                    // und dann auch speichern
                    logList.add("##");
                    logList.add("## Audioliste schreiben (" + LoadAudioFactoryDto.audioListNew.size() + " Audios) :");
                    logList.add("##    --> Start Schreiben nach: " + path);
                    new WriteAudioList().writeData(path, LoadAudioFactoryDto.audioListNew);
                    logList.add("##    --> geschrieben!");
                    logList.add("##");

                    ret = true;
                }
            }
        } catch (final Exception ex) {
            logList.add("##   Audioliste lesen hat nicht geklappt");
            P2Log.errorLog(645891204, ex);
            ret = false;
        }

        P2Log.sysLog(logList);
        PDuration.counterStop("readDb");
        return ret;
    }

    private void setDate() {
        // Datum setzen
        LocalDateTime date = P2DateGmtFactory.getLocalDateTimeFromGmt(
                LoadAudioFactoryDto.audioListNew.metaData[AudioList.META_GMT]);
        String dateStr = P2LDateTimeFactory.toString(date, P2DateConst.DT_FORMATTER_dd_MM_yyyy___HH__mm);
        ProgConfig.SYSTEM_AUDIOLIST_DATE_TIME.setValue(dateStr);
    }

    private void processFromFile(String source, AudioList audioList) {
        try (InputStream in = AudioFactory.selectDecompressor(source, new FileInputStream(source));
             JsonParser jp = new JsonFactory().createParser(in)) {
            new ReadAudioListJson().readData(jp, audioList);

        } catch (final FileNotFoundException ex) {
            logList.add("Audioliste existiert nicht: " + source + "\n" + ex.getLocalizedMessage());
            P2Log.errorLog(894512369, "Audioliste existiert nicht: " + source);
            audioList.clear();

        } catch (final Exception ex) {
            logList.add("Audioliste: " + source + "\n" + ex.getLocalizedMessage());
            P2Log.errorLog(945123641, ex, "Audioliste: " + source);
            audioList.clear();
        }
    }

    private void processFromWeb(URL source, AudioList audioList) {
        final Request.Builder builder = new Request.Builder().url(source);
        builder.addHeader("User-Agent", LoadFactoryConst.userAgent);

        // our progress monitor callback
        final InputStreamProgressMonitor monitor = new InputStreamProgressMonitor() {
            private int oldProgress = 0;

            @Override
            public void progress(long bytesRead, long size) {
                final int iProgress = (int) (bytesRead * 100/* zum Runden */ / size);
                if (iProgress != oldProgress) {
                    oldProgress = iProgress;
//                    notifyProgress(1.0 * iProgress / 100);
                }
            }
        };

        try (Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
             ResponseBody body = response.body()) {
            if (body != null && response.isSuccessful()) {

                try (InputStream input = new ProgressMonitorInputStream(body.byteStream(), body.contentLength(), monitor)) {
                    try (InputStream is = AudioFactory.selectDecompressor(source.toString(), input);
                         JsonParser jp = new JsonFactory().createParser(is)) {
                        new ReadAudioListJson().readData(jp, audioList);
                    }
                }
            }
        } catch (final Exception ex) {
            P2Log.errorLog(820147395, ex, "FilmListe: " + source);
            audioList.clear();
        }
    }

    private void fillHash(List<String> logList, AudioList audioList) {
        //alle historyURLs in den hash schreiben
        logList.add("## " + P2Log.LILNE3);
        logList.add("## Hash füllen, Größe vorher: " + LoadAudioFactoryDto.hashSet.size());

        LoadAudioFactoryDto.hashSet.addAll(audioList.stream().map(AudioData::getUrl).toList());
        logList.add("##                   nachher: " + LoadAudioFactoryDto.hashSet.size());
        logList.add("## " + P2Log.LILNE3);
    }

    private void removeUnwanted(List<String> logList, AudioList audioList) {
        if (LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MAX_DAYS == 0 &&
                LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MIN_DURATION == 0) {
            // dann alles
            return;
        }

        logList.add("## unerwünschte löschen");
        Iterator<AudioData> it = audioList.iterator();
        LocalDate minDate = LocalDate.now().minusDays(LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
        while (it.hasNext()) {
            AudioData audioData = it.next();
            if (LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MAX_DAYS > 0 &&
                    audioData.getDate().getLocalDate().isBefore(minDate)) {
                it.remove();
                continue;
            }
            if (LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MIN_DURATION > 0 &&
                    audioData.getDurationMinute() != 0 &&
                    audioData.getDurationMinute() < LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MIN_DURATION) {
                it.remove();
            }
        }
    }

    private void findAndMarkNewFilms(List<String> logList, AudioList audioList) {
        logList.add("## neue Audios markieren");
        audioList.stream() //genauso schnell wie "parallel": ~90ms
                .peek(film -> film.setNewAudio(false))
                .filter(film -> !LoadAudioFactoryDto.hashSet.contains(film.getUrl()))
                .forEach(film -> {
                    film.setNewAudio(true);
                });

        LoadAudioFactoryDto.hashSet.clear();
    }
}
