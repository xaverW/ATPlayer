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


package de.p2tools.atplayer.controller.filter;


import de.p2tools.p2lib.mediathek.audiodata.AudioData;
import de.p2tools.p2lib.mediathek.audiodata.AudioDataXml;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;

public class AudioFilterCheck {

    public static final String FILTER_SHOW_DATE_ALL = "";
    public static final int FILTER_ALL_OR_MIN = 0;
    public static final int FILTER_DURATION_MAX_MINUTE = 150;//Filmlänge [Minuten]
    public static final int FILTER_TIME_MAX_SEC = 24 * 60 * 60;//Sendezeit [Minuten], das ist eigentlich bereits 00:00 vom nächsten Tag!!
    public static final int FILTER_TIME_RANGE_MAX_VALUE = 50;//Zeitraum zurück [Tag]

    private AudioFilterCheck() {
    }

    public static boolean checkFilterMatch(Filter sender,
                                           Filter genre,
                                           Filter theme,
                                           Filter themeTitle,
                                           Filter title,
                                           AudioData audioData) {

        if (!sender.isEmpty && !checkMatchChannelSmartLowerCase(sender, audioData)) {
            return false;
        }

        if (!genre.isEmpty && !checkMatchGenreLowerCase(genre, audioData)) {
            return false;
        }

        if (!theme.isEmpty && !checkMatchThemeLowerCase(theme, audioData)) {
            return false;
        }

        if (!themeTitle.isEmpty && !checkMatchThemeTitleLowerCase(themeTitle, audioData)) {
            return false;
        }

        if (!title.isEmpty && !checkMatchTitleLowerCase(title, audioData)) {
            return false;
        }

        return true;
    }

    public static boolean checkMatchChannelSmart(Filter sender, AudioData audioData) {
        // nur ein Suchbegriff muss passen
        for (final String s : sender.filterArr) {
            // dann jeden Suchbegriff checken
            if (s.equalsIgnoreCase(audioData.arr[AudioDataXml.AUDIO_CHANNEL])) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkMatchChannelSmartLowerCase(Filter sender, AudioData audioData) {
        // nur ein Suchbegriff muss passen
        for (final String s : sender.filterArr) {
            // dann jeden Suchbegriff checken
            if (s.equals(audioData.CHANNEL_STR)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkMatchGenreLowerCase(Filter genre, AudioData audioData) {
        if (!FilterCheck.checkLowerCase(genre, audioData.arr[AudioDataXml.AUDIO_GENRE], audioData.GENRE_STR)) {
            return false;
        }
        return true;
    }

    private static boolean checkMatchThemeLowerCase(Filter theme, AudioData audioData) {
        if (!FilterCheck.checkLowerCase(theme, audioData.arr[AudioDataXml.AUDIO_THEME], audioData.THEME_STR)) {
            return false;
        }
        return true;
    }

    private static boolean checkMatchThemeTitleLowerCase(Filter themeTitle, AudioData audioData) {
        if (!FilterCheck.checkLowerCase(themeTitle, audioData.arr[AudioDataXml.AUDIO_THEME], audioData.THEME_STR)
                && !FilterCheck.checkLowerCase(themeTitle, audioData.arr[AudioDataXml.AUDIO_TITLE], audioData.TITLE_STR)) {
            return false;
        }
        return true;
    }

    private static boolean checkMatchTitleLowerCase(Filter title, AudioData audioData) {
        if (!FilterCheck.checkLowerCase(title, audioData.arr[AudioDataXml.AUDIO_TITLE], audioData.TITLE_STR)) {
            return false;
        }
        return true;
    }

    public static boolean checkMatchSomewhereLowerCase(Filter somewhere, AudioData audioData) {
        if (!FilterCheck.checkLowerCase(somewhere, audioData.arr[AudioDataXml.AUDIO_DATE],
                audioData.arr[AudioDataXml.AUDIO_DATE].toLowerCase())
                && !FilterCheck.checkLowerCase(somewhere, audioData.arr[AudioDataXml.AUDIO_GENRE], audioData.GENRE_STR)
                && !FilterCheck.checkLowerCase(somewhere, audioData.arr[AudioDataXml.AUDIO_THEME], audioData.THEME_STR)
                && !FilterCheck.checkLowerCase(somewhere, audioData.arr[AudioDataXml.AUDIO_TITLE], audioData.TITLE_STR)
                && !FilterCheck.check(somewhere, audioData.arr[AudioDataXml.AUDIO_DESCRIPTION])) {
            return false;
        }
        return true;
    }

    public static boolean checkMaxDays(int maxDays, long filmTime) {
        long days = 0;
        try {
            if (maxDays == AudioFilterCheck.FILTER_ALL_OR_MIN) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * maxDays;
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }

        return checkDays(days, filmTime);
    }

    public static boolean checkDays(long days, long filmTime) {
        if (days == 0) {
            return true;
        }

        if (filmTime != 0 && filmTime < days) {
            return false;
        }

        return true;
    }

    public static boolean checkDays(long days, AudioData audioData) {
        if (days == 0) {
            return true;
        }

        final long filmTime = audioData.getDate().getTime();
        if (filmTime != 0 && filmTime < days) {
            return false;
        }

        return true;
    }

    public static boolean checkMatchLengthMin(int filterLangth, long filmLength) {
        return filterLangth == 0 || filmLength == 0 || filmLength >= filterLangth;
    }

    public static boolean checkMatchLengthMax(int filterLaenge, long filmLength) {
        return filterLaenge == AudioFilterCheck.FILTER_DURATION_MAX_MINUTE || filmLength == 0
                || filmLength <= filterLaenge;
    }

    public static boolean checkMatchLength(int filterLeangth_minute_min, int filterLength_minute_max, long filmLength) {
        return checkMatchLengthMin(filterLeangth_minute_min, filmLength)
                && checkMatchLengthMax(filterLength_minute_max, filmLength);
    }

    public static boolean checkMatchFilmTime(int timeMin, int timeMax, boolean invert, int filmTime) {
        if (filmTime == FilmData.FILM_TIME_EMPTY) {
            return true;
        }

        boolean ret = (timeMin == 0 || filmTime >= timeMin) &&
                (timeMax == AudioFilterCheck.FILTER_TIME_MAX_SEC || filmTime <= timeMax);

        if (invert) {
            return !ret;
        } else {
            return ret;
        }
    }

    public static boolean checkMatchMinDur(int minDur, AudioData audioData) {
        if (minDur == AudioFilterCheck.FILTER_ALL_OR_MIN) {
            return true;
        }

        final int durationMinute = audioData.getDurationMinute();
        if (durationMinute != 0 && durationMinute < minDur) {
            return false;
        }

        return true;
    }

    public static boolean checkMatchMinDur(int minDur, int durationMinute) {
        if (minDur == AudioFilterCheck.FILTER_ALL_OR_MIN) {
            return true;
        }

        if (durationMinute != 0 && durationMinute < minDur) {
            return false;
        }

        return true;
    }

    public static boolean checkMatchMaxDur(int maxDur, AudioData audioData) {
        if (maxDur == AudioFilterCheck.FILTER_DURATION_MAX_MINUTE) {
            return true;
        }

        final int durationMinute = audioData.getDurationMinute();
        if (durationMinute != 0 && durationMinute > maxDur) {
            return false;
        }

        return true;
    }

    public static boolean checkMatchMaxDur(int maxDur, int durationMinute) {
        if (maxDur == AudioFilterCheck.FILTER_DURATION_MAX_MINUTE) {
            return true;
        }

        if (durationMinute != 0 && durationMinute > maxDur) {
            return false;
        }

        return true;
    }
}