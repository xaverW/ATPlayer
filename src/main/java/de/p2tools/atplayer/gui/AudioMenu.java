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

package de.p2tools.atplayer.gui;


import de.p2tools.atplayer.controller.audio.AudioTools;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.filter.AudioFilter;
import de.p2tools.atplayer.controller.filter.AudioFilterSample;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class AudioMenu extends VBox {
    private static final String AUDIO_FILTER_BOOKMARK_TEXT = "Alle angelegte Bookmarks anzeigen\n" +
            "der zweite Klick stellt den\n" +
            "eingestellten Filter wieder her";
    private AudioFilter storedActFilterSettings = null;

    public AudioMenu() {
        init();
    }

    private void init() {
        getStyleClass().add("button-menu");
        setSpacing(15);
        setAlignment(Pos.TOP_CENTER);
        initButton();
    }

    private void initButton() {
        // Button
        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(0);
        vBoxSpace.setMinHeight(0);
        getChildren().add(vBoxSpace);

        final ToolBarButton btnFilter = new ToolBarButton(this,
                "Filter anzeigen", "Filter anzeigen", ProgIcons.ICON_TOOLBAR_FILTER.getImageView());
        final ToolBarButton btnInfo = new ToolBarButton(this,
                "Infos Anzeigen", "Infos anzeigen", ProgIcons.ICON_TOOLBAR_INFO.getImageView());
        btnFilter.setOnAction(a -> ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.AUDIO_GUI_FILTER_DIVIDER_ON.getValue()));
        btnInfo.setOnAction(a -> ProgConfig.AUDIO_GUI_DIVIDER_ON.setValue(!ProgConfig.AUDIO_GUI_DIVIDER_ON.getValue()));

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        this.getChildren().add(vBoxSpace);

        final ToolBarButton btnPlay = new ToolBarButton(this,
                "Abspielen", "Markiertes Audio abspielen", ProgIcons.ICON_TOOLBAR_AUDIO_START.getImageView());
        final ToolBarButton btnPlayAll = new ToolBarButton(this,
                "Abspielen", "Markierte Audios abspielen", ProgIcons.ICON_TOOLBAR_AUDIO_ALL_START.getImageView());
        final ToolBarButton btnSave = new ToolBarButton(this,
                "Speichern", "Markierte Audios speichern", ProgIcons.ICON_TOOLBAR_AUDIO_REC.getImageView());

        btnPlay.setOnAction(a -> AudioTools.playAudio());
        btnPlayAll.setOnAction(a -> AudioTools.playAllAudios());
        btnSave.setOnAction(a -> AudioTools.saveAllAudios());

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        this.getChildren().add(vBoxSpace);

        final ToolBarButton btDelAllBookmark = new ToolBarButton(this,
                "Alle Bookmarks löschen", "Alle angelegten Bookmarks löschen", ProgIcons.ICON_TOOLBAR_AUDIO_DEL_ALL_BOOKMARK.getImageView());
        final ToolBarButton btFilterBookmark = new ToolBarButton(this,
                "Bookmarks anzeigen", AUDIO_FILTER_BOOKMARK_TEXT, ProgIcons.ICON_TOOLBAR_AUDIO_BOOKMARK_FILTER.getImageView());

        btDelAllBookmark.setOnAction(a -> ProgData.getInstance().historyListBookmarks.clearAll(ProgData.getInstance().primaryStage));
        btFilterBookmark.setOnAction(a -> {
            AudioFilter sf = ProgData.getInstance().actFilterWorker.getActFilterSettings();
            AudioFilter filter = AudioFilterSample.getBookmarkFilter();

            if (sf.isSame(filter, false)) {
                // dann ist der BlackFilter aktiv, dann zurückschalten
                if (storedActFilterSettings != null) {
                    // dann haben wir einen gespeicherten Filter
                    ProgData.getInstance().actFilterWorker.setActFilterSettings(storedActFilterSettings);
                    storedActFilterSettings = null;
                } else {
                    // dann gibts keinen gespeicherten, dann einfach löschen
                    ProgData.getInstance().actFilterWorker.getActFilterSettings().clearFilter();
                }
            } else {
                // dann ist es ein anderer Filter, Black einschalten und ActFilter merken
                storedActFilterSettings = ProgData.getInstance().actFilterWorker.getActFilterSettings().getCopy();
                ProgData.getInstance().actFilterWorker.setActFilterSettings(filter);
            }
        });
    }
}
