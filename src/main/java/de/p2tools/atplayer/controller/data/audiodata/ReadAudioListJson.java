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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

public class ReadAudioListJson {

    private String channel = "", genre = "", theme = "";

    public ReadAudioListJson() {
    }

    public void readData(JsonParser jp, AudioList audioList) throws IOException {
        JsonToken jsonToken;
        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected data to start with an Object");
        }

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                for (int k = 0; k < AudioList.AUDIO_LIST_META_MAX_ELEM; ++k) {
                    audioList.metaData[k] = jp.nextTextValue();
                }
                break;
            }
        }
        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                // sind nur die Feldbeschreibungen, brauch mer nicht
                jp.nextToken();
                break;
            }
        }

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }

            if (jp.isExpectedStartArrayToken()) {
                final AudioData audioData = new AudioData();
                try {
                    addValue(audioData, jp);
                    audioData.init(); // damit wird auch das Datum! gesetzt
                    audioList.importAudioOnlyWithNr(audioData);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    private void addValue(AudioData audioData, JsonParser jp) throws IOException {
        for (int i = 0; i < AudioDataXml.JSON_MAX_ELEM; ++i) {
            String str = jp.nextTextValue();

            switch (i) {
                case AudioDataXml.JSON_AUDIO_CHANNEL:
                    if (!str.isEmpty()) {
                        channel = str.intern();
                    }
                    audioData.arr[AudioDataXml.AUDIO_CHANNEL] = channel;
                    break;
                case AudioDataXml.JSON_AUDIO_GENRE:
                    if (!str.isEmpty()) {
                        genre = str.intern();
                    }
                    audioData.arr[AudioDataXml.AUDIO_GENRE] = genre;
                    break;
                case AudioDataXml.JSON_AUDIO_THEME:
                    if (!str.isEmpty()) {
                        theme = str.intern();
                    }
                    audioData.arr[AudioDataXml.AUDIO_THEME] = theme;
                    break;
                case AudioDataXml.JSON_AUDIO_TITLE:
                    audioData.arr[AudioDataXml.AUDIO_TITLE] = str;
                    break;

                case AudioDataXml.JSON_AUDIO_DATE:
                    audioData.arr[AudioDataXml.AUDIO_DATE] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_TIME:
                    audioData.arr[AudioDataXml.AUDIO_TIME] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_DURATION:
                    audioData.arr[AudioDataXml.AUDIO_DURATION] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_SIZE_MB:
                    audioData.arr[AudioDataXml.AUDIO_SIZE_MB] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_DESCRIPTION:
                    audioData.arr[AudioDataXml.AUDIO_DESCRIPTION] = str;
                    break;

                case AudioDataXml.JSON_AUDIO_URL:
                    audioData.arr[AudioDataXml.AUDIO_URL] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_WEBSITE:
                    audioData.arr[AudioDataXml.AUDIO_WEBSITE] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_NEW:
                    audioData.arr[AudioDataXml.AUDIO_NEW] = str;
                    break;
            }
        }
    }
}
