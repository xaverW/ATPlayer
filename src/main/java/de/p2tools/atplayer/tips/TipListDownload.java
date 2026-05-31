/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.atplayer.tips;

import java.util.ArrayList;
import java.util.List;

public class TipListDownload {

    private TipListDownload() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();


        String text = "Ein Favorit ist ein Sender der in Tabelle \"Sender\" " +
                "dazu ausgewählt wurde. Sie werden hier in der Tabelle " +
                "angezeigt." +
                "\n\n" +
                "Die Sender können gestartet und aus den Favoriten gelöscht " +
                "werden.\n" +
                "Es können auch eigene Sender (die nicht in der Senderliste " +
                "enthalten sind) angelegt werden.";
        String image = "/de/p2tools/atplayer/res/tips/download/download-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Oben wird der Sender gestartet und gestoppt\n" +
                "Das \"Plus\" darunter legt einen eigenen Sender an." +
                "\n\n" +
                "Das \"X\" löscht den Sender aus den Favoriten und mit dem " +
                "\"Zahnrad\" können die Einstellungen des Favoriten geändert " +
                "werden." +
                "\n\n" +
                "Das \"i\" zeigt Infos zum Sender an.";
        image = "/de/p2tools/atplayer/res/tips/download/download-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
