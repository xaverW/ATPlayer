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

package de.p2tools.atplayer.gui.filter;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class AudioFilterController extends FilterController {

    private AudioFilterTextFilter audioFilterTextFilter;
    private AudioFilterEdit audioFilterEdit;
    private AudioFilterProfiles audioFilterProfiles;
    private AudioFilterrBlacklist audioFilterrBlacklist;

    public AudioFilterController() {
        super(ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON);

        audioFilterTextFilter = new AudioFilterTextFilter();
        audioFilterEdit = new AudioFilterEdit();
        audioFilterProfiles = new AudioFilterProfiles();
        audioFilterrBlacklist = new AudioFilterrBlacklist();

        Separator sp1 = new Separator();
        sp1.getStyleClass().add("pseperator1");
        sp1.setMinHeight(0);

        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator3");
        sp2.setMinHeight(0);

        final VBox vBoxFilter = getVBoxFilter(true);
        vBoxFilter.setSpacing(10);
        vBoxFilter.getChildren().addAll(audioFilterTextFilter,
                P2GuiTools.getVBoxGrower(),
                audioFilterEdit,
                sp2,
                audioFilterProfiles);

        getVBoxBottom().getChildren().add(audioFilterrBlacklist);
    }
}
