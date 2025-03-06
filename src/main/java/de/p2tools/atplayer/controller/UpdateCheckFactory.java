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


package de.p2tools.atplayer.controller;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.update.SearchProgramUpdate;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.ArrayList;
import java.util.List;

public class UpdateCheckFactory {
    private UpdateCheckFactory() {
    }

    public static void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        P2Duration.onlyPing("checkProgUpdate");
        if (ProgConfig.SYSTEM_SEARCH_UPDATE.getValue() &&
                !isUpdateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck();

        } else {
            // will der User nicht --oder-- wurde heute schon gemacht
            List<String> list = new ArrayList<>(5);
            list.add("Kein Update-Check:");
            if (!ProgConfig.SYSTEM_SEARCH_UPDATE.getValue()) {
                list.add("  der User will nicht");
            }
            if (isUpdateCheckTodayDone()) {
                list.add("  heute schon gemacht");
            }
            P2Log.sysLog(list);
        }
    }

    private static boolean isUpdateCheckTodayDone() {
        return ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.get().equals(P2LDateFactory.getNowStringR());
    }

    private static void runUpdateCheck() {
        ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.setValue(P2LDateFactory.getNowStringR());
        new SearchProgramUpdate().searchNewProgramVersion(false);
    }
}
