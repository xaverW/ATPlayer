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

package de.p2tools.atplayer.controller.filter;

import de.p2tools.atplayer.controller.config.PEvents;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class AudioFilter extends AudioFilterProps {

    private final PauseTransition pause = new PauseTransition(Duration.millis(200));
    private boolean filterIsOff = false; // Filter ist EIN - meldet Änderungen

    public AudioFilter() {
        initFilter();
        setName("Filter");
    }

    public AudioFilter(String name) {
        initFilter();
        setName(name);
    }

    public void reportFilterReturn() {
        // sind die ComboBoxen wenn return gedrückt wird
        P2Log.debugLog("reportFilterReturn");
        pause.stop();
        ProgData.getInstance().filterWorker.addBackward();
        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED);
    }

    private void reportFilterChange() {
        // sind die anderen Filter (ändern, ein-ausschalten), wenn Pause abgelaufen ist / gestoppt ist
        if (!filterIsOff) {
            ProgData.getInstance().filterWorker.addBackward();
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED);
        }
    }

    public void switchFilterOff(boolean switchOff) {
        pause.stop();
        this.filterIsOff = switchOff;
    }

    private void initFilter() {
        pause.setOnFinished(event -> reportFilterChange());
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        pause.setOnFinished(event -> ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED));
        ProgConfig.SYSTEM_FILTER_WAIT_TIME.addListener((observable, oldValue, newValue) -> {
            P2Log.debugLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
            pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        });

        clearFilter();
        nameProperty().addListener(l -> setFilterChange(false));
        channelProperty().addListener(l -> setFilterChange(true));
        channelVisProperty().addListener(l -> setFilterChange(true));
        genreProperty().addListener(l -> setFilterChange(true));
        genreVisProperty().addListener(l -> setFilterChange(true));
        themeProperty().addListener(l -> setFilterChange(false));
        themeVisProperty().addListener(l -> setFilterChange(false));
        themeTitleProperty().addListener(l -> setFilterChange(false));
        themeTitleVisProperty().addListener(l -> setFilterChange(false));
        titleProperty().addListener(l -> setFilterChange(false));
        titleVisProperty().addListener(l -> setFilterChange(false));
        somewhereProperty().addListener(l -> setFilterChange(false));
        somewhereVisProperty().addListener(l -> setFilterChange(false));

        timeRangeProperty().addListener(l -> setFilterChange(true));
        timeRangeVisProperty().addListener(l -> setFilterChange(true));
        durVisProperty().addListener(l -> setFilterChange(true));
        minDurProperty().addListener(l -> setFilterChange(true));
        maxDurProperty().addListener(l -> setFilterChange(true));

        onlyVisProperty().addListener(l -> setFilterChange(true));
        onlyNewProperty().addListener(l -> setFilterChange(true));
        onlyBookmarkProperty().addListener(l -> setFilterChange(true));
        noHistoryProperty().addListener(l -> setFilterChange(true));

        podcastOnOffProperty().addListener(l -> setFilterChange(true));
        podcastVisProperty().addListener(l -> setFilterChange(true));

        blacklistOnOffProperty().addListener(l -> reportBlacklistChange());
    }

    private void reportBlacklistChange() {
        if (!filterIsOff) { // todo ??
            BlacklistFilterFactory.makeBlackFiltered();
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_FILTER_CHANGED);
        }
    }

    private void setFilterChange(boolean startNow) {
        // wird ausgelöst, wenn ein Filter ein/ausgeschaltet wird oder was eingetragen wird
        if (!startNow && ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            //dann wird erst nach "RETURN" gestartet
            pause.stop();

        } else {
            // dann wird sofort gestartet (nach Pause)
            pause.playFromStart();
        }
    }

    public void clearFilter() {
        // alle Filter löschen, Button Black bleibt, wie er ist
        setChannel("");
        setGenre("");
        setTheme("");
        setThemeTitle("");
        setTitle("");
        setSomewhere("");

        setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);
        setMinDur(FilterCheck.FILTER_ALL_OR_MIN);
        setMaxDur(AudioFilterCheck.FILTER_DURATION_MAX_MINUTE);

        setOnlyNew(false);
        setOnlyBookmark(false);
        setNoHistory(false);

        setPodcastOnOff(AudioFilter.PODCAST_FILTER_OFF__SHOW_ALL);
    }
}
