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

import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.atdata.AudioDataProps;
import de.p2tools.p2lib.atdata.AudioDataXml;
import de.p2tools.p2lib.mtfilter.FilmFilterCheck;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;

import java.util.function.Predicate;

public class AudioPredicateFactory {
    private AudioPredicateFactory() {
    }

    public static Predicate<AudioData> getPredicate(AudioFilter audioFilter) {

        Filter fChannel;
        Filter fGenre;
        Filter fTheme;
        Filter fTitle;
        Filter fSomewhere;

        String filterChannel = audioFilter.getChannel();
        String filterGenre = audioFilter.getGenre();
        String filterTheme = audioFilter.getTheme();
        String filterTitle = audioFilter.getTitle();
        String filterSomewhere = audioFilter.getSomewhere();

        // Sender
        fChannel = new Filter(filterChannel, true);
        // Genre
        fGenre = new Filter(filterGenre, true);
        // Thema
        fTheme = new Filter(filterTheme, true);
        // Titel
        fTitle = new Filter(filterTitle, true);
        // Irgendwo
        fSomewhere = new Filter(filterSomewhere, true);

        //Sendedatum
        final boolean onlyNew = audioFilter.isOnlyNew();
        final boolean onlyBookmark = audioFilter.isOnlyBookmark();
        final boolean noHistory = audioFilter.isNoHistory();
        long days;
        try {
            if (audioFilter.getTimeRange() == FilterCheck.FILTER_ALL_OR_MIN) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * audioFilter.getTimeRange();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }

        Predicate<AudioData> predicate = audioData -> true;

        if (onlyNew) {
            predicate = predicate.and(AudioDataProps::isNewAudio);
        }

        if (onlyBookmark) {
            predicate = predicate.and(AudioDataProps::isBookmark);
        }

        if (noHistory) {
            predicate = predicate.and(audioData -> !audioData.isShown());
        }

        //anz Tage Sendezeit
        if (days != 0) {
            final long d = days;
            predicate = predicate.and(f -> FilmFilterCheck.checkDays(d, f.getDate().getTime()));
        }

        //Filmlänge
        if (audioFilter.getMinDur() != FilterCheck.FILTER_ALL_OR_MIN) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchMinDur(audioFilter.getMinDur(), f.getDurationMinute()));
        }
        if (audioFilter.getMaxDur() != FilterCheck.FILTER_DURATION_MAX_MINUTE) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchMaxDur(audioFilter.getMaxDur(), f.getDurationMinute()));
        }

        //Textfilter
        if (!fChannel.isEmpty) {
            predicate = predicate.and(f -> checkMatchChannelSmart(fChannel, f.arr[AudioDataXml.AUDIO_CHANNEL]));
        }

        if (!fGenre.isEmpty) {
            predicate = predicate.and(f -> FilterCheck.check(fGenre, f.arr[AudioDataXml.AUDIO_GENRE]));
        }

        if (!fTheme.isEmpty) {
            predicate = predicate.and(f -> FilterCheck.check(fTheme, f.arr[AudioDataXml.AUDIO_THEME]));
        }

        if (!fTitle.isEmpty) {
            predicate = predicate.and(f -> FilterCheck.check(fTitle, f.arr[AudioDataXml.AUDIO_TITLE]));
        }

        if (!fSomewhere.isEmpty) {
            predicate = predicate.and(f -> checkMatchSomewhere(fSomewhere, f));
        }

        return predicate;
    }

    public static boolean checkMatchChannelSmart(Filter sender, String channel) {
        // nur ein Suchbegriff muss passen
        for (final String s : sender.filterArr) {
            // dann jeden Suchbegriff checken
            if (s.equalsIgnoreCase(channel)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkMatchSomewhere(Filter somewhere, AudioData audioData) {
        if (!FilterCheck.check(somewhere, audioData.arr[AudioDataXml.AUDIO_DATE])
                && !FilterCheck.check(somewhere, audioData.arr[AudioDataXml.AUDIO_GENRE])
                && !FilterCheck.check(somewhere, audioData.arr[AudioDataXml.AUDIO_THEME])
                && !FilterCheck.check(somewhere, audioData.arr[AudioDataXml.AUDIO_TITLE])
                && !FilterCheck.check(somewhere, audioData.arr[AudioDataXml.AUDIO_DESCRIPTION])) {
            return false;
        }
        return true;
    }

}
