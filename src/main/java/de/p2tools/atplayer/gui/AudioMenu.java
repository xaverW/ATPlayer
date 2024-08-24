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


import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.atplayer.ATPlayerFactory;
import de.p2tools.atplayer.controller.audio.AudioPlayFactory;
import de.p2tools.atplayer.controller.audio.AudioSaveFactory;
import de.p2tools.atplayer.controller.config.PShortKeyFactory;
import de.p2tools.atplayer.controller.config.PShortcut;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.blackdata.BlacklistFactory;
import de.p2tools.atplayer.controller.filter.AudioFilter;
import de.p2tools.atplayer.controller.filter.AudioFilterSample;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class AudioMenu {
    private static final String AUDIO_FILTER_BOOKMARK_TEXT = "Alle angelegte Bookmarks anzeigen\n" +
            "der zweite Klick stellt den\n" +
            "eingestellten Filter wieder her";
    private AudioFilter storedActFilterSettings = null;
    final private ProgData progData;
    final private VBox vBox;
    private static final String FILM_FILTER_BOOKMARK_TEXT = "Alle angelegte Bookmarks anzeigen\n" +
            "der zweite Klick stellt den\n" +
            "eingestellten Filter wieder her";

    public AudioMenu(VBox vBox) {
        this.vBox = vBox;
        progData = ProgData.getInstance();
    }

    public void init() {
        vBox.getChildren().clear();

        initFilmMenu();
        initButton();
    }

    private void initButton() {
        // Button
        VBox vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(0);
        vBoxSpace.setMinHeight(0);
        vBox.getChildren().add(vBoxSpace);

        final ToolBarButton btnPlay = new ToolBarButton(vBox,
                "Abspielen", "Markiertes Audio abspielen", ProgIcons.ICON_TOOLBAR_START.getImageView());
        final ToolBarButton btnPlayAll = new ToolBarButton(vBox,
                "Abspielen", "Markierte Audios abspielen", ProgIcons.ICON_TOOLBAR_START_ALL.getImageView());
        final ToolBarButton btnSave = new ToolBarButton(vBox,
                "Speichern", "Markierte Audios speichern", ProgIcons.ICON_TOOLBAR_REC.getImageView());

        btnPlay.setOnAction(a -> AudioPlayFactory.playAudio());
        btnPlayAll.setOnAction(a -> AudioPlayFactory.playAllAudios());
        btnSave.setOnAction(a -> AudioSaveFactory.saveAllAudios());

        vBoxSpace = new VBox();
        vBoxSpace.setMaxHeight(10);
        vBoxSpace.setMinHeight(10);
        vBox.getChildren().add(vBoxSpace);


        final ToolBarButton btBookmark = new ToolBarButton(vBox,
                "Bookmarks anlegen", "Bookmarks für die markierten Filme anlegen", ProgIcons.ICON_TOOLBAR_BOOKMARK.getImageView());
        final ToolBarButton btDelBookmark = new ToolBarButton(vBox,
                "Bookmarks löschen", "Bookmarks für die markierten Filme löschen", ProgIcons.ICON_TOOLBAR_DEL_BOOKMARK.getImageView());
        final ToolBarButton btDelAllBookmark = new ToolBarButton(vBox,
                "Alle Bookmarks löschen", "Alle angelegten Bookmarks löschen", ProgIcons.ICON_TOOLBAR_DEL_ALL_BOOKMARK.getImageView());
        final ToolBarButton btFilterBookmark = new ToolBarButton(vBox,
                "Bookmarks anzeigen", FILM_FILTER_BOOKMARK_TEXT, ProgIcons.ICON_TOOLBAR_BOOKMARK_FILTER.getImageView());

        btBookmark.setOnAction(a -> {
            progData.audioGuiController.bookmarkAudio(true);
            ;
        });
        btDelBookmark.setOnAction(a -> {
            progData.audioGuiController.bookmarkAudio(false);
        });
        btDelAllBookmark.setOnAction(a -> {
            progData.historyListBookmarks.clearAll(progData.primaryStage);
        });
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

    private void initFilmMenu() {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Filmmenü anzeigen"));
        mb.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().addAll("btnFunction", "btnFunc-0");

        final MenuItem mbPlay = new MenuItem("Film abspielen");
        mbPlay.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.AUDIO) {
                return;
            }
            final Optional<AudioData> filmSelection = ProgData.getInstance().audioGuiController.getSel(true);
            filmSelection.ifPresent(AudioPlayFactory::playAudio);
        });
        P2ShortcutWorker.addShortCut(mbPlay, PShortcut.SHORTCUT_PLAY);

        final MenuItem mbPlayAll = new MenuItem("Alle markierten Audios abspielen");
        mbPlayAll.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.AUDIO) {
                return;
            }
            AudioPlayFactory.playAllAudios();
        });
        P2ShortcutWorker.addShortCut(mbPlayAll, PShortcut.SHORTCUT_PLAY_ALL);

        final MenuItem mbSave = new MenuItem("Film speichern");
        mbSave.setOnAction(e -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.AUDIO) {
                return;
            }
            AudioSaveFactory.saveAudio();
        });
        P2ShortcutWorker.addShortCut(mbSave, PShortcut.SHORTCUT_SAVE);

        mb.getItems().addAll(mbPlay, mbPlayAll, mbSave);

        final MenuItem miFilmShown = new MenuItem("Filme als gesehen markieren");
        miFilmShown.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.AUDIO) {
                return;
            }
            progData.audioGuiController.setShown(true);
        });
        P2ShortcutWorker.addShortCut(miFilmShown, PShortcut.SHORTCUT_AUDIO_SHOWN);

        final MenuItem miFilmNotShown = new MenuItem("Filme als ungesehen markieren");
        miFilmNotShown.setOnAction(a -> {
            if (ATPlayerController.paneShown != ATPlayerController.PANE_SHOWN.AUDIO) {
                return;
            }
            progData.audioGuiController.setShown(false);
        });
        P2ShortcutWorker.addShortCut(miFilmNotShown, PShortcut.SHORTCUT_AUDIO_NOT_SHOWN);

        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_INFO_AUDIO.getActShortcut());
        miFilmInfo.setOnAction(a -> {
            progData.audioGuiController.showAudioInfo();
        });

        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_COPY_AUDIO_THEME_TO_CLIPBOARD.getActShortcut());
        miCopyTheme.setOnAction(a -> progData.audioGuiController.copyFilmThemeTitle(true));

        final MenuItem miCopyTitle = new MenuItem("Titel in die Zwischenablage kopieren" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_COPY_AUDIO_TITLE_TO_CLIPBOARD.getActShortcut());
        miCopyTitle.setOnAction(a -> progData.audioGuiController.copyFilmThemeTitle(false));

        //Blacklist
        Menu submenuBlacklist = new Menu("Blacklist");
        final MenuItem miBlack = new MenuItem("Blacklist-Eintrag für den Film erstellen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_ADD_BLACKLIST.getActShortcut());
        miBlack.setOnAction(event -> BlacklistFactory.addBlackFilm(true));

        final MenuItem miBlackTheme = new MenuItem("Thema direkt in die Blacklist einfügen" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_ADD_BLACKLIST_THEME.getActShortcut());
        miBlackTheme.setOnAction(event -> {
            BlacklistFactory.addBlackThemeFilm();
        });
        submenuBlacklist.getItems().addAll(miBlack, miBlackTheme);

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miFilmShown, miFilmNotShown, miFilmInfo,
                miCopyTheme, miCopyTitle, submenuBlacklist);

        // Bookmarks
        Menu submenuBookmark = new Menu("Bookmarks");
        final MenuItem miBookmarkAdd = new MenuItem("Neue Bookmarks anlegen");
        miBookmarkAdd.setOnAction(a -> progData.audioGuiController.bookmarkAudio(true));
        final MenuItem miBookmarkDel = new MenuItem("Bookmarks löschen");
        miBookmarkDel.setOnAction(a -> progData.audioGuiController.bookmarkAudio(false));
        final MenuItem miBookmarkDelAll = new MenuItem("Alle angelegten Bookmarks löschen");
        miBookmarkDelAll.setOnAction(a -> progData.historyListBookmarks.clearAll(progData.primaryStage));

        submenuBookmark.getItems().addAll(miBookmarkAdd, miBookmarkDel, miBookmarkDelAll);
        mb.getItems().add(submenuBookmark);

        final MenuItem miShowFilter = new MenuItem("Filter ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_FILTER.getActShortcut());
        miShowFilter.setOnAction(a -> ATPlayerFactory.setFilter());

        final MenuItem miShowInfo = new MenuItem("Infos ein-/ausblenden" +
                PShortKeyFactory.SHORT_CUT_LEER + PShortcut.SHORTCUT_SHOW_INFOS.getActShortcut());
        miShowInfo.setOnAction(a -> ATPlayerFactory.setInfos());

        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miShowFilter, miShowInfo);
        vBox.getChildren().add(mb);
    }
}
