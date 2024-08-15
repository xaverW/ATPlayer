/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.atplayer.controller.data.blackdata;

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.dialog.AddBlackListDialogController;
import de.p2tools.p2lib.atdate.AudioData;

import java.util.List;
import java.util.Optional;

public class BlacklistFactory {
    private BlacklistFactory() {
    }

    public static void addBlack() {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        BlackData blackData;
        final Optional<AudioData> audioData = ProgData.getInstance().audioGuiController.getSel(true);
        if (audioData.isEmpty()) {
            return;
        }

        blackData = new BlackData(audioData.get().getChannel(), audioData.get().getGenre(),
                audioData.get().getTheme(), audioData.get().getTitle(), "");

        AddBlackListDialogController addBlacklistDialogController =
                new AddBlackListDialogController(blackData);
        if (!addBlacklistDialogController.isOk()) {
            //dann doch nicht
            return;
        }
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static void addBlack(String sender, String genre, String theme, String titel) {
        BlackData blackData = new BlackData(sender, genre, theme, titel, "");
        ProgData.getInstance().blackList.addAndNotify(blackData);
    }

    public static boolean blackIsEmpty(BlackData blackData) {
        // true, wenn es das Black schon gibt
        if (blackData.getChannel().isEmpty() &&
                blackData.getGenre().isEmpty() &&
                blackData.getTheme().isEmpty() &&
                blackData.getTitle().isEmpty() &&
                blackData.getThemeTitle().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean blackExistsAlready(BlackData blackData, List<BlackData> list) {
        // true, wenn es das Black schon gibt
        for (final BlackData data : list) {
            if (data.getChannel().equalsIgnoreCase(blackData.getChannel()) &&
                    data.getGenre().equalsIgnoreCase(blackData.getGenre()) &&
                    data.getTheme().equalsIgnoreCase(blackData.getTheme()) &&

                    ((data.getTheme().isEmpty() && blackData.getTheme().isEmpty()) ||
                            data.isThemeExact() == blackData.isThemeExact()) &&

                    data.getTitle().equalsIgnoreCase(blackData.getTitle()) &&
                    data.getThemeTitle().equalsIgnoreCase(blackData.getThemeTitle())) {
                return true;
            }
        }
        return false;
    }
}
