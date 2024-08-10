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
package de.p2tools.atplayer.controller.data.audiodata;


import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;

public class AudioList extends SimpleListProperty<AudioData> {

    public static final int AUDIO_LIST_META_MAX_ELEM = 2;
    public static final String AUDIO_LIST_TAG = "AudioList";

    public static int META_GMT = 0;
    public static int META_LOCAL = 1;

    public int nr = 1;
    public String[] metaData = new String[]{"GMT", "LocalDate"}; // AudioDataXml.AUDIO_LIST_META_MAX_ELEM
    public String[] sender = {""};
    public String[] genre = {""};
    int count = 0;
    FilteredList<AudioData> filteredList = null;
    SortedList<AudioData> sortedList = null;

    public AudioList() {
        super(FXCollections.observableArrayList());
    }

    public synchronized void setMeta(AudioList filaudioDatalist) {
        System.arraycopy(filaudioDatalist.metaData, 0, metaData, 0, AUDIO_LIST_META_MAX_ELEM);
    }

    public SortedList<AudioData> getSortedList() {
        initFilterdList();
        return sortedList;
    }

    public FilteredList<AudioData> getFilteredList() {
        initFilterdList();
        return filteredList;
    }

    private void initFilterdList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
    }

    public synchronized void filteredListSetPred(Predicate<AudioData> predicate) {
        P2Log.debugLog("=================> Filter: " + ++count);
        P2Duration.counterStart("FilmList.filteredListSetPred");
        getFilteredList().setPredicate(predicate);
        P2Duration.counterStop("FilmList.filteredListSetPred");
    }

    public synchronized void importAudioOnlyWithNr(AudioData audioData) {
        // hier nur beim Laden aus einer fertigen Audioliste mit der GUI
        // die Audios sind schon sortiert, nur die Nummer muss noch ergänzt werden
        audioData.no = nr++;
        add(audioData);
    }

    @Override
    public synchronized void clear() {
        nr = 1;
        super.clear();
    }

    public synchronized AudioData getAudioByUrl(final String url) {
        final Optional<AudioData> opt =
                parallelStream().filter(f -> f.arr[AudioDataXml.AUDIO_URL].equalsIgnoreCase(url)).findAny();
        return opt.orElse(null);
    }

    public synchronized void getTheme(String sender, LinkedList<String> list) {
        stream().filter(film -> film.arr[AudioDataXml.AUDIO_CHANNEL].equals(sender))
                .filter(film -> !list.contains(film.arr[AudioDataXml.AUDIO_THEME]))
                .forEach(film -> list.add(film.arr[AudioDataXml.AUDIO_THEME]));
    }

    /**
     * Erstellt ein StringArray der Themen eines Senders oder wenn "sender" leer, aller Sender. Ist
     * für die Filterfelder in GuiAudio.
     */
    public synchronized void loadSenderAndGenre() {
        P2Duration.counterStart("loadSenderAndGenre");

        final LinkedHashSet<String> setSender = new LinkedHashSet<>(21);
        // der erste Sender ist ""
        setSender.add("");
        this.forEach((audioData) -> setSender.add(audioData.getChannel()));
        sender = setSender.toArray(new String[0]);

        final LinkedHashSet<String> setGenre = new LinkedHashSet<>(15);
        setGenre.add("");
        this.forEach((audioData) -> setGenre.add(audioData.getGenre()));
        genre = setGenre.toArray(new String[0]);

        P2Duration.counterStop("loadSenderAndGenre");
    }

    /**
     * Get the age of the film list.
     *
     * @return Age in seconds.
     */
    public int getAge() {
        return AudioListFactory.getAge(metaData);
    }
}
