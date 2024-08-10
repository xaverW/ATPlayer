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

import de.p2tools.p2lib.mtfilm.tools.FilmDate;

public class AudioDataProps extends AudioDataXml {

    public int no;
    public FilmDate date = new FilmDate(0);
    public AudioSize audioSize = new AudioSize(); // Dateigröße in MByte

    private int durationMinute = 0;
    private boolean newAudio = false;
    private boolean shown = false;
    private boolean bookmark = false;
    private boolean blackBlocked = false;

    public int getDurationMinute() {
        return durationMinute;
    }

    public void setDurationMinute(int durationMinute) {
        this.durationMinute = durationMinute;
    }

    public int getNo() {
        return no;
    }

    public String getChannel() {
        return arr[AUDIO_CHANNEL];
    }

    public String getGenre() {
        return arr[AUDIO_GENRE];
    }

    public String getTheme() {
        return arr[AUDIO_THEME];
    }

    public String getTitle() {
        return arr[AUDIO_TITLE];
    }

    public FilmDate getDate() {
        return date;
    }

    public String getTime() {
        return arr[AUDIO_TIME];
    }

    public String getDuration() {
        return arr[AUDIO_DURATION];
    }

    public AudioSize getAudioSize() {
        return audioSize;
    }

    public String getDescription() {
        return arr[AUDIO_DESCRIPTION];
    }

    public void setDescription(String text) {
        arr[AUDIO_DESCRIPTION] = text;
    }

    public String getUrl() {
        return arr[AUDIO_URL];
    }

    public String getWebsite() {
        return arr[AUDIO_WEBSITE];
    }

    public boolean isNewAudio() {
        return newAudio;
    }

    public void setNewAudio(final boolean newAudio) {
        this.newAudio = newAudio;
    }

    public String getFilmDateLong() {
        // beschleunigt etwas das Laden der Audioliste
        return arr[AUDIO_DATE_LONG];
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isBlackBlocked() {
        return blackBlocked;
    }

    public void setBlackBlocked(boolean blackBlocked) {
        this.blackBlocked = blackBlocked;
    }
}
