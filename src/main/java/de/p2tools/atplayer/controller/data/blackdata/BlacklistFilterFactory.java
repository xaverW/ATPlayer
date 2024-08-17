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

package de.p2tools.atplayer.controller.data.blackdata;

import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.filter.AudioFilterCheck;
import de.p2tools.p2lib.atdate.AudioData;
import de.p2tools.p2lib.atdate.AudioList;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BlacklistFilterFactory {
    public static final int BLACKLILST_FILTER_OFF = 0;
    public static final int BLACKLILST_FILTER_ON = 1;
    public static final int BLACKLILST_FILTER_INVERS = 2;

    private static boolean dontShowPodcast;
    private static long maxFilmDays = 0; // Zeit in ms ab wann erlaubt, oder 0 wenn alles
    private static long minFilmDuration = 0;
    private static int act = 0;
    private static int now = 0;

    private BlacklistFilterFactory() {
    }

//    public static void markBlackThread(boolean notify) {
//        ProgData.busy.busyOnFx(Busy.BUSY_SRC.GUI, "Blacklist", -1, false);
//        new Thread(() -> {
//            BlacklistFilterFactory.markBlack(notify);
//            ProgData.busy.busyOffFx();
//        }).start();
//    }

    public static synchronized void markBlack(boolean notify) {
        // Filmliste geladen, Button/Menü, ConfigDialog, Filter blkBtn
        // hier werden die Filme gekennzeichnet, ob sie "black" sind und das dauert

        P2Duration.counterStart("markFilmBlack");
        P2Log.sysLog("markFilmBlack -> start");

        final boolean maskerPane;
        if (ProgData.getInstance().maskerPane.isVisible()) {
            maskerPane = true;
            ProgData.getInstance().maskerPane.setMaskerText("Blacklist filtern");
        } else {
            maskerPane = false;
        }

        ProgData.getInstance().blackList.clearCounter();
        loadCurrentBlacklistSettings();

        //Filmliste durchlaufen und geblockte Filme markieren (parallel: Blockiert sich selbst durch Film.setBlocked)
        P2Duration.counterStart("forEach");
        final int sum = ProgData.getInstance().audioList.size();
        act = 0;
        now = 0;

        ProgData.getInstance().audioList.forEach(audioData -> {
            ++act;
            ++now;
            if (now > 5_000) {
                now = 0;
                final double percent = (double) act / sum;
                ProgData.busy.setProgress(percent);
                if (maskerPane) {
                    ProgData.getInstance().maskerPane.setMaskerProgress(percent, "Blacklist filtern");
                }
            }
            audioData.setBlackBlocked(checkAudioIsBlockedCompleteBlackData(audioData,
                    ProgData.getInstance().blackList, true));
        });
        P2Duration.counterStop("forEach");

        //und jetzt die filteredList erstellen
        makeBlackFiltered();

        if (maskerPane) {
            ProgData.getInstance().maskerPane.setMaskerProgress(-1.0, "Blacklist filtern");
        }
        P2Log.sysLog("markFilmBlack -> stop");
        P2Duration.counterStop("markFilmBlack");

        if (notify) {
            PListener.notify(PListener.EVENT_BLACKLIST_CHANGED, BlacklistFilterFactory.class.getSimpleName());
        }
    }

    public static synchronized void makeBlackFiltered() {
        // nach dem markieren der Filme in der Liste, nach dem ein-/ausschalten der Blacklist
        // es wird die Black-gefilterte Filmliste erstellt
        final ProgData progData = ProgData.getInstance();
        final AudioList audioList = progData.audioList;
        final AudioList audioListFiltered = progData.audioListFiltered;

        P2Duration.counterStart("makeBlackFiltered");
        loadCurrentBlacklistSettings();
        audioListFiltered.clear();

        if (audioList != null) {
            audioListFiltered.setMeta(audioList);

            Stream<AudioData> initialStream = audioList.stream();

            if (progData.actFilterWorker.getActFilterSettings().getBlacklistOnOff() == BLACKLILST_FILTER_INVERS) {
                //blacklist ONLY
                P2Log.sysLog("FilmlistBlackFilter - isBlacklistOnly");
                initialStream = initialStream.filter(AudioData::isBlackBlocked);

            } else if (progData.actFilterWorker.getActFilterSettings().getBlacklistOnOff() == BLACKLILST_FILTER_ON) {
                //blacklist ON
                P2Log.sysLog("FilmlistBlackFilter - isBlacklistOn");
                initialStream = initialStream.filter(audioData -> !audioData.isBlackBlocked());

            } else {
                //blacklist OFF
                P2Log.sysLog("FilmlistBlackFilter - isBlacklistOff");
            }

            audioListFiltered.addAll(initialStream.toList());
            // Array mit Sendernamen/Themen füllen
            audioListFiltered.loadSenderAndGenre();
        }
        P2Duration.counterStop("makeBlackFiltered");
    }

    public static synchronized boolean checkAudioIsBlockedCompleteBlackData(AudioData audioData, List<BlackData> list,
                                                                            boolean incCounter) {
        // beim Abo-Suchen wenn eingeschaltet, und beim markieren der Filme: "markFilmBlack()"
        // hier werden der Film gegen alle BLACK-Einstellungen der Blacklist geprüft
        // liefert TRUE -> wenn der Film zur Blacklist passt, also geblockt werden soll (oder nicht WHITE)
        // Counter werden vorher schon gelöscht und werden gesetzt

        if (dontShowPodcast && audioData.isPodcast()) {
            return true;
        }
        if (minFilmDuration != 0 && !checkOkFilmLength(audioData)) {
            return true;
        }
        if (maxFilmDays > 0 && !checkOkDate(audioData)) {
            return true;
        }

        return checkAudioAndCountHits(audioData, list, incCounter);
    }

    public static boolean checkAudioAndCountHits(AudioData audioData, List<BlackData> list, boolean countHits) {
        // Aufruf nach dem Neuladen einer Filmliste aus dem Web (ist der FilmListFilter beim Laden) oder Test/Markieren eines Films
        // nach Treffer **abbrechen**
        // Counter werden vorher schon gelöscht
        audioData.setLowerCase();
        for (final BlackData blackData : list) {
            if (!blackData.isActive()) {
                //dann ist er ausgeschaltet
                continue;
            }

            if (checkAudioIsBlocked(blackData, audioData)) {
                if (countHits) {
                    blackData.incCountHits();
                }
                audioData.clearLowerCase();
                return true;
            }
        }
        audioData.clearLowerCase();
        return false;
    }

    private static void checkAudioAndCountHitsForAll(AudioData audioData, List<BlackData> list) {
        audioData.setLowerCase();
        list.parallelStream().forEach(blackData -> {
            if (BlacklistFilterFactory.checkAudioIsBlocked(blackData, audioData)) {
                blackData.incCountHits();
            }
        });
        audioData.clearLowerCase();
    }

    public static synchronized void countHits(BlackData blackData) {
        // Aufruf mit Button im AddBlackList-Dialog zum Zählen
        // hier wird ein BlackDate gegen die Filmliste gefiltert und die Treffer ermittelt
        List<BlackData> bl = new ArrayList<>();
        bl.add(blackData);
        countHits(bl);
    }

    public static synchronized void countHits(List<BlackData> list) {
        // Aufruf mit Button zum Zählen, Einstellungen
        // hier wird die Blacklist gegen die Filmliste gefiltert und die Treffer
        // für *jeden* Blacklist-Eintrag ermittelt, wird nicht nach einem Treffer abgebrochen
        P2Duration.counterStart("countHits");

        for (BlackData bl : list) {
            bl.clearCounter();
        }

        List<BlackData> copyList = new ArrayList<>(list);
        final int sum = ProgData.getInstance().audioList.size();
        act = 0;
        now = 0;
        final AudioList audioList = ProgData.getInstance().audioList;
        if (audioList != null) {
            // wenn parallel, variieren die Werte etwas??
            audioList.forEach(audioData -> {
                ++act;
                ++now;
                if (now > 1_000) {
                    now = 0;
                    final double percent = (double) act / sum;
                    ProgData.busy.setProgress(percent);
                }
                checkAudioAndCountHitsForAll(audioData, copyList);
            });
        }
        P2Duration.counterStop("countHits");
    }

    private static void loadCurrentBlacklistSettings() {
        // die aktuellen allgemeinen Blacklist-Einstellungen laden
        dontShowPodcast = ProgConfig.SYSTEM_BLACKLIST_SHOW_NO_PODCAST.get();

        try {
            if (ProgConfig.SYSTEM_BLACKLIST_MAX_FILM_DAYS.getValue() == 0) {
                maxFilmDays = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * ProgConfig.SYSTEM_BLACKLIST_MAX_FILM_DAYS.getValue();
                maxFilmDays = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            maxFilmDays = 0;
        }

        minFilmDuration = ProgConfig.SYSTEM_BLACKLIST_MIN_FILM_DURATION.getValue(); // Minuten
    }

    private static boolean checkAudioIsBlocked(BlackData blackData, AudioData audioData) {
        // erst mal "schnell" prüfen->bringt ~20%
        if (blackData.quickChannel) {
            if (audioData.CHANNEL_STR.contains(blackData.fChannel.filterArr[0])) {
                //dann wird geblockt
                return true;
            } else {
                return false;
            }
        }
        if (blackData.quickGenre) {
            if (audioData.GENRE_STR.contains(blackData.fGenre.filterArr[0])) {
                //dann wird geblockt
                return true;
            } else {
                return false;
            }
        }
        if (blackData.quickTheme) {
            if (blackData.fTheme.isExact) {
                if (audioData.THEME_STR.equals(blackData.fTheme.filterArr[0])) {
                    //dann wird geblockt
                    return true;
                } else {
                    return false;
                }

            } else {
                if (audioData.THEME_STR.contains(blackData.fTheme.filterArr[0])) {
                    //dann wird geblockt
                    return !blackData.fTheme.exclude;
                } else {
                    return blackData.fTheme.exclude;
                }
            }
        }
        if (blackData.quickThemTitle) {
            if (audioData.THEME_STR.contains(blackData.fThemeTitle.filterArr[0]) ||
                    audioData.TITLE_STR.contains(blackData.fThemeTitle.filterArr[0])) {
                //dann wird geblockt
                return !blackData.fThemeTitle.exclude;
            } else {
                return blackData.fThemeTitle.exclude;
            }
        }
        if (blackData.quickTitle) {
            if (audioData.TITLE_STR.contains(blackData.fTitle.filterArr[0])) {
                //dann wird geblockt
                return !blackData.fTitle.exclude;
            } else {
                return blackData.fTitle.exclude;
            }
        }

        // wenn Filter passt (Blacklist) dann wird geblockt
        boolean ret = AudioFilterCheck.checkFilterMatch(
                blackData.fChannel,
                blackData.fGenre,
                blackData.fTheme,
                blackData.fThemeTitle,
                blackData.fTitle,
                audioData);

        if (ret) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check film based on date
     *
     * @param audioData item to be checked
     * @return true if film can be displayed
     */
    private static boolean checkOkDate(AudioData audioData) {
        final long filmTime = audioData.getDate().getTime(); // liefert die ms nach "o"
        if (filmTime != 0 && filmTime <= maxFilmDays) {
            return false;
        }

        return true;
    }

    /**
     * Filter based on film length.
     *
     * @param audioData item to check
     * @return true if film should be displayed
     */
    private static boolean checkOkFilmLength(AudioData audioData) {
        return audioData.getDurationMinute() == 0 || audioData.getDurationMinute() >= minFilmDuration;
    }
}
