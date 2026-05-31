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

package de.p2tools.atplayer.controller.worker;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.filter.AudioFilter;
import de.p2tools.atplayer.tips.TipsDialog;
import de.p2tools.p2lib.p2event.P2Events;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class Worker {

    final AudioFilter sfTemp = new AudioFilter();
    private final ProgData progData;
    private final ObservableList<String> allChannelList = FXCollections.observableArrayList("");
    private final ObservableList<String> allGenreList = FXCollections.observableArrayList("");

    public Worker(ProgData progData) {
        this.progData = progData;

        progData.pEventHandler.addListener(new P2Listener(P2Events.EVENT_TIMER_ONE_MINUTE) {
            @Override
            public void pingGui() {
                // startet alles das einmal nach dem Start laufen soll
                if (ProgConfig.SYSTEM_SHOW_TIPS.get() && !TipsDialog.TIPS_DIALOG_OPEN) {
                    // dann sollen Tipps angezeigt werden, und nur wenn noch nicht offen
                    new TipsDialog(progData);
                }
            }
        });


    }

    public void saveFilter() {
        progData.filterWorker.getActFilterSettings().copyTo(sfTemp);
    }

    public void resetFilter() {
        allChannelList.setAll(Arrays.asList(progData.audioList.sender));//alle Sender laden
        allGenreList.setAll(Arrays.asList(progData.audioList.genre));//alle Genre laden
        sfTemp.copyTo(progData.filterWorker.getActFilterSettings());
    }

    public ObservableList<String> getAllChannelList() {
        return allChannelList;
    }

    public ObservableList<String> getAllGenreList() {
        return allGenreList;
    }
}
