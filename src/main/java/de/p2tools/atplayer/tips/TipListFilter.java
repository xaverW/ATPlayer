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

public class TipListFilter {

    private TipListFilter() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Der Filter der Sender ist in " +
                "mehrere Bereiche geteilt. " +
                "Oben sind die Textfilter " +
                "(z.B.: Sender, Genre, ..)." +
                "\n\n" +
                "Danach " +
                "kommen die Filter die nach " +
                "Sender-Eigenschaften (z.B.: " +
                "Bitrate, ..) suchen." +
                "\n\n" +
                "Unten sind die Einstellungen " +
                "der Filter. Dort kann ausgewählt " +
                "werden, welche Filter angezeigt " +
                "werden sollen. Dort können auch Filtereinstellungen " +
                "in Profilen gespeichert und " +
                "wieder abgerufen werden." +
                "\n\n" +
                "Ganz unten kann die Blacklist ein- und " +
                "ausgeschaltet werden.";
        String image = "/de/p2tools/atplayer/res/tips/filter/filter-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Welche Filter angezeigt werden " +
                "sollen, kann mit dem \"Zahnrad\" " +
                "ausgewählt werden." +
                "\n\n" +
                "Mit den beiden Pfeilen " +
                "(rechts/links) kann in den " +
                "verwendeten Filtern zurück und " +
                "vorwärts geblättert werden." +
                "\n\n" +
                "Der \"Trichter\" löscht die Filter. " +
                "Der erste Klick darauf, löscht nur " +
                "die Textfilter oben. Der zweite " +
                "Klick löscht auch die weiteren " +
                "Filter darunter.";
        image = "/de/p2tools/atplayer/res/tips/filter/filter-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Filter-Profile sind gespeicherte Filtereinstellungen." +
                "\n\n" +
                "Das beinhaltet, " +
                "welche Filter (Sender, Genre, ..) eingeschaltet sind. Auch der Suchtext eines Filters " +
                "(z.B. \"Rock\") wird im Profil gespeichert." +
                "\n\n" +
                "Mit der Auswahlbox wählt man eins aus, das " +
                "dann auch eingestellt wird. " +
                "Im Filterpanel werden dann die im Filter-Profil gespeicherten " +
                "Einstellungen angezeigt." +
                "\n\n" +
                "Mit dem \"Plus\" wird ein neues Profil angelegt. Der " +
                "\"Pfeil nach oben\" stellt das ausgewählte Profil wieder her. " +
                "Der \"Pfeil nach unten\" speichert die aktuellen Filtereinstellungen in " +
                "dem ausgewählten Profil. In dem Menü darunter können die Profile verwaltet (Sortiert, ..) " +
                "werden.";
        image = "/de/p2tools/atplayer/res/tips/filter/filter-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
