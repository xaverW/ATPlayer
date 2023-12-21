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


package de.p2tools.atplayer.gui.tools;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.ptipofday.P2TipOfDay;
import de.p2tools.p2lib.guitools.ptipofday.P2TipOfDayDialog;
import de.p2tools.p2lib.guitools.ptipofday.P2TipOfDayFactory;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TipOfDayFactory {

    private static final String START = "                                                     " + P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipOfDayFactory() {
    }

    public static void showDialog(ProgData progData, boolean showAlways) {
        if (!showAlways && !ProgConfig.TIP_OF_DAY_SHOW.getValue()) {
            //dann wills der User nicht :(
            PLog.sysLog("TipOfDay: Will der User nicht");
            return;
        }

        if (showAlways ||
                !ProgConfig.TIP_OF_DAY_DATE.get().equals(P2DateConst.F_FORMAT_yyyy_MM_dd.format(new Date())) &&
                        P2TipOfDayFactory.containsToolTipNotShown(ProgConfig.TIP_OF_DAY_WAS_SHOWN.get(), listSize)) {

            //nur wenn "immer" / heute noch nicht und nicht angezeigte ToolTips enthalten sind
            ProgConfig.TIP_OF_DAY_DATE.setValue(P2DateConst.F_FORMAT_yyyy_MM_dd.format(new Date()));

            final List<P2TipOfDay> pTipOfDayArrayList = new ArrayList<>();
            addTips(pTipOfDayArrayList);
            new P2TipOfDayDialog(progData.primaryStage, pTipOfDayArrayList,
                    ProgConfig.TIP_OF_DAY_WAS_SHOWN, ProgConfig.TIP_OF_DAY_SHOW, 500);
        } else {
            PLog.sysLog("TipOfDay: Heute schon gemacht oder keine neuen Tips");
        }
    }

    private static void addTips(List<P2TipOfDay> pToolTipList) {
        // private final int listSize = 1
        String text = START;
        text += "Der Infobereich unter der Audioliste\n" +
                "kann mit dem Tastenkürzel \"alt+i\"\n" +
                "oder über den Button \"I\" rechts\n" +
                "ein- und ausgeblendet werden.";
        String image = "/de/p2tools/atplayer/res/tooltips/Info.png";
        P2TipOfDay pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Hier können vorherige Filtereinstellung\n" +
                "zurückgeholt werden. Der Filter kann\n" +
                "hier auch gelöscht werden.";
        image = "/de/p2tools/atplayer/res/tooltips/ClearFilter.png";
        pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "können spezielle Einstellungen\n" +
                "zum Filtern vorgenommen werden.";
        image = "/de/p2tools/atplayer/res/tooltips/Filter.png";
        pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "können Download-Einstellungen\n" +
                "(Pfad, Zieldateiname, ..)\n" +
                "vorgenommen werden.";
        image = "/de/p2tools/atplayer/res/tooltips/DownloadPath.png";
        pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);

        pToolTipList.add(P2TipOfDay.getTipWebsite(ProgConfig.SYSTEM_PROG_OPEN_URL));

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die verwendete\n" +
                "Schriftgröße im Programm,\n" +
                "eingestellt werden.";
        image = "/de/p2tools/atplayer/res/tooltips/Keysize.png";
        pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die Audioliste auf aktuelle\n" +
                "oder Audios mit bestimmter Größe\n" +
                "eingeschränkt werden.";
        image = "/de/p2tools/atplayer/res/tooltips/LoadFilmlist.png";
        pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann der Videoplayer\n" +
                "zum Abspielen der Audios\n" +
                "geändert werden.";
        image = "/de/p2tools/atplayer/res/tooltips/Videoplayer.png";
        pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Im Menü (unter \"Hilfe\") kann\n" +
                "der Dialog:\n" +
                "\"Einstellungen zurücksetzen\"\n" +
                "geöffnet werden. Damit können\n" +
                "alle Programmeinstellungen\n" +
                "zurückgesetzt werden. Das\n" +
                "Programm startet dann wieder\n" +
                "wie beim ersten Mal.";
        image = "/de/p2tools/atplayer/res/tooltips/Reset.png";
        pToolTip = new P2TipOfDay(text, image);
        pToolTipList.add(pToolTip);
    }
}
