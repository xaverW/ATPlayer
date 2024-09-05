/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.atplayer.controller.filter;

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.mtfilter.FilterCheck;

public class FilterSamples {

    private FilterSamples() {
    }

    public static AudioFilter getBookmarkFilter() {
        AudioFilter sf = new AudioFilter("nur Bookmarks");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);

        sf.setTimeRangeVis(false);
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setOnlyVis(true);
        sf.setOnlyNew(false);
        sf.setOnlyBookmark(true);
        sf.setNoHistory(false);

        return sf;
    }

    public static void addStandardFilter() {
        ProgData progData = ProgData.getInstance();

        //========================================================
        AudioFilter sf = new AudioFilter("alles anzeigen");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);

        sf.setTimeRangeVis(true);
        sf.setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);

        sf.setDurVis(false);
        sf.setMinDur(0);
        sf.setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);

        sf.setOnlyVis(true);
        sf.setOnlyNew(false);
        sf.setOnlyBookmark(false);
        sf.setNoHistory(false);

        progData.filterWorker.getFilterList().add(sf);

        //========================================================
        // nur Bookmark
        sf = getBookmarkFilter();
        progData.filterWorker.getFilterList().add(sf);

        //========================================================
        sf = new AudioFilter("aktuelle Nachrichten");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Nachrichten");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);

        sf.setTimeRange(10);
        sf.setTimeRangeVis(true);

        sf.setDurVis(false);

        sf.setOnlyVis(true);
        sf.setOnlyNew(false);
        sf.setOnlyBookmark(false);
        sf.setNoHistory(false);

        progData.filterWorker.getFilterList().add(sf);

        //========================================================
        sf = new AudioFilter("Nachrichten mit Europa UND Brexit");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setThemeVis(false);
        sf.setThemeTitleVis(true);
        sf.setThemeTitle("Europa:Brexit");
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);

        sf.setTimeRangeVis(true);
        sf.setDurVis(false);

        sf.setOnlyVis(true);
        sf.setOnlyNew(false);
        sf.setOnlyBookmark(false);
        sf.setNoHistory(false);

        progData.filterWorker.getFilterList().add(sf);

        //========================================================
        sf = new AudioFilter("nur ARD ODER BR");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setChannel("ard,br");
        sf.setThemeVis(false);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);

        sf.setTimeRangeVis(true);
        sf.setDurVis(false);

        sf.setOnlyVis(true);
        sf.setOnlyNew(false);
        sf.setOnlyBookmark(false);
        sf.setNoHistory(false);

        progData.filterWorker.getFilterList().add(sf);

        //========================================================
        sf = new AudioFilter("nur \"neue\" in der ARD");
        sf.clearFilter();

        sf.setChannelVis(true);
        sf.setChannel("ard");
        sf.setThemeVis(false);
        sf.setThemeTitleVis(true);
        sf.setTitleVis(false);
        sf.setSomewhereVis(false);

        sf.setTimeRange(10);
        sf.setTimeRangeVis(true);
        sf.setDurVis(false);

        sf.setOnlyVis(true);
        sf.setOnlyNew(true);
        sf.setOnlyBookmark(false);
        sf.setNoHistory(false);

        progData.filterWorker.getFilterList().add(sf);
    }
}
