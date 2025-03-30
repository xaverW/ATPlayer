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
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class AudioFilterRunner {
    private static final AtomicBoolean search = new AtomicBoolean(false);
    private static final AtomicBoolean research = new AtomicBoolean(false);
    private final ProgData progData;
    int count = 0;

    /**
     * hier wird das Filtern der Audioliste "angestoßen"
     *
     * @param progData
     */
    public AudioFilterRunner(ProgData progData) {
        this.progData = progData;
        progData.filterWorker.filterChangeProperty().addListener((observable, oldValue, newValue) -> filter()); // Filmfilter (User) haben sich geändert
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_BLACKLIST_CHANGED) {
            @Override
            public void ping() {
                filterList();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILTER_CHANGED) {
            @Override
            public void ping() {
                filterList();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_DIACRITIC_CHANGED) {
            @Override
            public void ping() {
                filterList();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_HISTORY_CHANGED) {
            @Override
            public void ping() {
                AudioFilter audioFilter = progData.filterWorker.getActFilterSettings();
                if (audioFilter.isNoHistory()) {
                    //nur dann wird History gefiltert
                    filterList();
                }
            }
        });

    }

    public void filter() {
        Platform.runLater(this::filterList);
    }

    private void filterList() {
        // ist etwas "umständlich", scheint aber am flüssigsten zu laufen
        if (!search.getAndSet(true)) {
            research.set(false);
            try {
                Platform.runLater(() -> {
                    P2Log.debugLog("========================================");
                    P2Log.debugLog("         === Filter: " + count++ + " ===");
                    P2Log.debugLog("========================================");

                    P2Duration.counterStart("AudioFilterRunner.filterList");
                    progData.audioListFiltered.filteredListSetPred(
                            PredicateFactory.getPredicate(progData.filterWorker.getActFilterSettings()));
                    P2Duration.counterStop("AudioFilterRunner.filterList");

                    search.set(false);
                    if (research.get()) {
                        filterList();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace(); //todo???
            }
        } else {
            research.set(true);
        }
    }
}
