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


package de.p2tools.atplayer.controller.downloadtools;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.data.download.DownloadConstants;
import de.p2tools.p2lib.tools.log.P2Log;

public class DownloadProgParameterFactory {
    private DownloadProgParameterFactory() {
    }

    public static String getProgParameterArray(String url) {
        //Zieldatei und Pfad bauen und eintragen
        String progArray = "";
        try {
            progArray = getPlayParameterArray();
            progArray = replaceExec(url, progArray);
        } catch (final Exception ex) {
            P2Log.errorLog(987512098, ex);
        }
        return progArray;
    }

    private static String getPlayParameterArray() {
        String ret = ProgConfig.SYSTEM_PROG_PLAY.getValueSafe();
        String progParameter = ProgConfig.SYSTEM_PROG_PLAY_PARAMETER.getValueSafe();

        final String[] ar = progParameter.split(" ");
        for (final String s : ar) {
            ret = ret + DownloadConstants.TRENNER_PROG_ARRAY + s;
        }
        return ret;
    }

    private static String replaceExec(String url, String execString) {
        execString = execString.replace("%f", url);
        return execString;
    }
}
