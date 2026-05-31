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
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;

import java.util.function.Predicate;

public class PredicateFactory {
    private PredicateFactory() {
    }

    public static Predicate<AudioData> getPredicate(AudioFilter audioFilter) {

        Filter fChannel;
        Filter fGenre;
        Filter fTheme;
        Filter fThemeTitle;
        Filter fTitle;
        Filter fSomewhere;

        String filterChannel = audioFilter.isChannelVis() ? audioFilter.getChannel() : "";
        String filterGenre = audioFilter.isGenreVis() ? audioFilter.getGenre() : "";
        String filterTheme = audioFilter.isThemeVis() ? audioFilter.getTheme() : "";
        String filterThemeTitle = audioFilter.isThemeTitleVis() ? audioFilter.getThemeTitle() : "";
        String filterTitle = audioFilter.isTitleVis() ? audioFilter.getTitle() : "";
        String filterSomewhere = audioFilter.isSomewhereVis() ? audioFilter.getSomewhere() : "";

        // Sender
        fChannel = new Filter(filterChannel, true);
        // Genre
        fGenre = new Filter(filterGenre, true);
        // Thema
        fTheme = new Filter(filterTheme, true);
        // Thema-Titel
        fThemeTitle = new Filter(filterThemeTitle, true);
        // Titel
        fTitle = new Filter(filterTitle, true);
        // Irgendwo
        fSomewhere = new Filter(filterSomewhere, true);

        //Sendedatum
        final boolean onlyNew = audioFilter.isOnlyVis() && audioFilter.isOnlyNew();
        final boolean onlyBookmark = audioFilter.isOnlyVis() && audioFilter.isOnlyBookmark();
        final boolean noHistory = audioFilter.isOnlyVis() && audioFilter.isNoHistory();

        final int podcast = audioFilter.getPodcastOnOff();
        final boolean podcastVis = audioFilter.isPodcastVis();

        long days;
        try {
            if (!audioFilter.isTimeRangeVis()) {
                days = 0;
            } else {
                if (audioFilter.getTimeRange() == FilterCheck.FILTER_ALL_OR_MIN) {
                    days = 0;
                } else {
                    final long max = 1000L * 60L * 60L * 24L * audioFilter.getTimeRange();
                    days = System.currentTimeMillis() - max;
                }
            }
        } catch (final Exception ex) {
            days = 0;
        }

        Predicate<AudioData> predicate = audioData -> true;

        if (onlyNew) {
            predicate = predicate.and(AudioData::isNewAudio);
        }

        if (onlyBookmark) {
            predicate = predicate.and(AudioData::isBookmark);
        }

        if (noHistory) {
            predicate = predicate.and(audioData -> !audioData.isShown());
        }

        if (podcastVis) {
            if (podcast == AudioFilter.PODCAST_FILTER_ON__SHOW_ONLY_POD) {
                predicate = predicate.and(AudioData::isPodcast);
            } else if (podcast == AudioFilter.PODCAST_FILTER_INVERS__SHOW_NO_POD) {
                predicate = predicate.and(audioData -> !audioData.isPodcast());
            }
        }

        //anz Tage Sendezeit
        if (days != 0) {
            final long d = days;
            predicate = predicate.and(f -> AudioFilterCheck.checkDays(d, f.getDate().getTime()));
        }

        //Filmlänge
        if (audioFilter.getMinDur() != FilterCheck.FILTER_ALL_OR_MIN) {
            predicate = predicate.and(f -> AudioFilterCheck.checkMatchMinDur(audioFilter.getMinDur(), f.getDurationMinute()));
        }
        if (audioFilter.getMaxDur() != AudioFilterCheck.FILTER_DURATION_MAX_MINUTE) {
            predicate = predicate.and(f ->
                    AudioFilterCheck.checkMatchMaxDur(audioFilter.getMaxDur(), f.getDurationMinute()));
        }

        //Textfilter
        if (!fChannel.isEmpty) {
            predicate = predicate.and(audioData -> AudioFilterCheck.checkMatchChannelSmart(fChannel, audioData));
        }

        if (!fGenre.isEmpty) {
            predicate = predicate.and(f -> FilterCheck.check(fGenre, f.arr[AudioDataXml.AUDIO_GENRE]));
        }

        if (!fTheme.isEmpty) {
            predicate = predicate.and(f -> FilterCheck.check(fTheme, f.arr[AudioDataXml.AUDIO_THEME]));
        }

        if (!fThemeTitle.isEmpty) {
            predicate = predicate.and(f -> FilterCheck.check(fThemeTitle, f.arr[AudioDataXml.AUDIO_THEME]) ||
                    FilterCheck.check(fThemeTitle, f.arr[AudioDataXml.AUDIO_TITLE]));
        }

        if (!fTitle.isEmpty) {
            predicate = predicate.and(f -> FilterCheck.check(fTitle, f.arr[AudioDataXml.AUDIO_TITLE]));
        }

        if (!fSomewhere.isEmpty) {
            predicate = predicate.and(audioData -> AudioFilterCheck.checkMatchSomewhere(fSomewhere, audioData));
        }

        return predicate;
    }
}
