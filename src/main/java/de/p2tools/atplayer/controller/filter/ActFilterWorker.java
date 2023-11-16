/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.atplayer.controller.filter;

import de.p2tools.atplayer.controller.config.ProgData;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public final class ActFilterWorker {

    // ist der aktuell angezeigte Filter
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung"; // dient nur der Info im Config-File
    final int MAX_FILTER_HISTORY = 10;
    final int MAX_FILTER_GO_BACK = 5;
    private final ProgData progData;
    private final BooleanProperty filterChange = new SimpleBooleanProperty(true);
    private final BooleanProperty backwardPossible = new SimpleBooleanProperty(false);
    private final BooleanProperty forwardPossible = new SimpleBooleanProperty(false);

    // ist die Liste der zuletzt verwendeten Filter
    private final ObservableList<AudioFilter> audioFilterListBackward =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, AudioFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (AudioFilter tp) -> new Observable[]{tp.nameProperty()});
    private final ObservableList<AudioFilter> audioFilterListForward =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, AudioFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (AudioFilter tp) -> new Observable[]{tp.nameProperty()});

    private final ObservableList<String> lastGenreFilter = FXCollections.observableArrayList("");
    private final ObservableList<String> lastThemaTitleFilter = FXCollections.observableArrayList("");
    private final ObservableList<String> lastTitleFilter = FXCollections.observableArrayList("");
    private final ObservableList<String> lastSomewhereFilter = FXCollections.observableArrayList("");
    private final ChangeListener<Boolean> filterChangeListener;
    private AudioFilter actAudioFilterSettings = new AudioFilter(SELECTED_FILTER_NAME); // ist der "aktuelle" Filter im Programm
    private AudioFilter oldActAudioFilterSettings = new AudioFilter(SELECTED_FILTER_NAME); // ist der "aktuelle" Filter im Programm

    private boolean theme = false, themeTitle = false, title = false, somewhere = false;

    public ActFilterWorker(ProgData progData) {
        this.progData = progData;

        filterChangeListener = (observable, oldValue, newValue) -> {
            postFilterChange();
        };
        actAudioFilterSettings.filterChangeProperty().addListener(filterChangeListener); // wenn der User den Filter ändert
        audioFilterListBackward.addListener((ListChangeListener<AudioFilter>) c -> {
            if (audioFilterListBackward.size() > 1) {
                backwardPossible.setValue(true);
            } else {
                backwardPossible.setValue(false);
            }
        });
        audioFilterListForward.addListener((ListChangeListener<AudioFilter>) c -> {
            if (audioFilterListForward.size() > 0) {
                forwardPossible.setValue(true);
            } else {
                forwardPossible.setValue(false);
            }
        });
    }

    public void initFilter() {
        addBackward();
    }

    public BooleanProperty filterChangeProperty() {
        return filterChange;
    }

    public BooleanProperty backwardPossibleProperty() {
        return backwardPossible;
    }

    public BooleanProperty forwardPossibleProperty() {
        return forwardPossible;
    }

    /**
     * liefert den aktuell angezeigten Filter
     *
     * @return
     */
    public AudioFilter getActFilterSettings() {
        return actAudioFilterSettings;
    }

    /**
     * setzt die aktuellen Filtereinstellungen aus einem Filter (gespeicherten Filter)
     *
     * @param sf
     */
    public synchronized void setActFilterSettings(AudioFilter sf) {
        if (sf == null) {
            return;
        }
        actAudioFilterSettings.filterChangeProperty().removeListener(filterChangeListener);
        sf.copyTo(actAudioFilterSettings);
        postFilterChange();
        actAudioFilterSettings.filterChangeProperty().addListener(filterChangeListener);
    }

    public synchronized void clearFilter() {
        actAudioFilterSettings.filterChangeProperty().removeListener(filterChangeListener);
        actAudioFilterSettings.clearFilter();
        audioFilterListForward.clear();
        audioFilterListBackward.clear();

        postFilterChange();
        actAudioFilterSettings.filterChangeProperty().addListener(filterChangeListener);
    }

    public void goBackward() {
        if (audioFilterListBackward.size() <= 1) {
            // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
            return;
        }

        AudioFilter sf = audioFilterListBackward.remove(audioFilterListBackward.size() - 1); // ist die aktuelle Einstellung
        audioFilterListForward.add(sf);
        sf = audioFilterListBackward.remove(audioFilterListBackward.size() - 1); // ist die davor
        setActFilterSettings(sf);
    }

    public void goForward() {
        if (audioFilterListForward.isEmpty()) {
            // dann gibts keine
            return;
        }

        final AudioFilter sf = audioFilterListForward.remove(audioFilterListForward.size() - 1);
        setActFilterSettings(sf);
    }

    public ObservableList<String> getLastGenreFilter() {
        return lastGenreFilter;
    }

    public ObservableList<String> getLastThemaTitleFilter() {
        return lastThemaTitleFilter;
    }

    public synchronized void addLastThemeTitleFilter(String filter) {
        addLastFilter(lastThemaTitleFilter, filter);
    }

    public ObservableList<String> getLastTitleFilter() {
        return lastTitleFilter;
    }

    public synchronized void addLastTitleFilter(String filter) {
        addLastFilter(lastTitleFilter, filter);
    }

    public ObservableList<String> getLastSomewhereFilter() {
        return lastSomewhereFilter;
    }

    public synchronized void addLastSomewhereFilter(String filter) {
        addLastFilter(lastSomewhereFilter, filter);
    }

    private synchronized void addLastFilter(ObservableList<String> list, String filter) {
        if (filter.isEmpty()) {
            return;
        }

        if (!list.stream().filter(f -> f.equals(filter)).findAny().isPresent()) {
            list.add(filter);
        }
        while (list.size() >= MAX_FILTER_HISTORY) {
            list.remove(1);
        }
    }

    private void setFilterChange() {
        addLastThemeTitleFilter(progData.actFilterWorker.getActFilterSettings().getTheme());
        addLastTitleFilter(progData.actFilterWorker.getActFilterSettings().getTitle());
        addLastSomewhereFilter(progData.actFilterWorker.getActFilterSettings().getSomewhere());

        //hier erst mal die actFilter vergleichen, ob geändert
        if (!oldActAudioFilterSettings.isSame(actAudioFilterSettings, true)) {
            actAudioFilterSettings.copyTo(oldActAudioFilterSettings);
            this.filterChange.set(!filterChange.get());
        }
    }

    private void addBackward() {
        final AudioFilter sf = new AudioFilter();
        actAudioFilterSettings.copyTo(sf);
        if (audioFilterListBackward.isEmpty()) {
            audioFilterListBackward.add(sf);
            return;
        }

        AudioFilter sfB = audioFilterListBackward.get(audioFilterListBackward.size() - 1);
        if (sf.isSame(sfB, false)) {
            // dann hat sich nichts geändert (z.B. mehrmals gelöscht)
            return;
        }

        //Textfilter
//        if (!sf.isThemeExact() && checkText(sfB.themeProperty(), sf.themeProperty(), sfB, sf, theme)) {
//            setFalse();
//            theme = true;
//            return;
//        }
        if (checkText(sfB.themeProperty(), sf.themeProperty(), sfB, sf, themeTitle)) {
            setFalse();
            themeTitle = true;
            return;
        }
        if (checkText(sfB.titleProperty(), sf.titleProperty(), sfB, sf, title)) {
            setFalse();
            title = true;
            return;
        }
        if (checkText(sfB.somewhereProperty(), sf.somewhereProperty(), sfB, sf, somewhere)) {
            setFalse();
            somewhere = true;
            return;
        }

        //dann wars kein Textfilter
        audioFilterListBackward.add(sf);
    }

    private void setFalse() {
        theme = false;
        themeTitle = false;
        title = false;
        somewhere = false;
    }

    private boolean checkText(StringProperty old, StringProperty nnew, AudioFilter oldSf, AudioFilter newSf,
                              boolean check) {
        if (old.get().equals(nnew.get())) {
            return false;
        }
        if (check && !old.get().isEmpty() && !nnew.get().isEmpty() &&
                (old.get().contains(nnew.get()) || nnew.get().contains(old.get()))) {
            // dann hat sich nur ein Teil geändert und wird ersetzt
            old.setValue(nnew.getValue());
        } else {
            audioFilterListBackward.add(newSf);
        }
        return true;
    }

    private void postFilterChange() {
        addBackward();
        setFilterChange();
    }
}
