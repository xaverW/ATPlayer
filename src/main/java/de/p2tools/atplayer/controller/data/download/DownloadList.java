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

package de.p2tools.atplayer.controller.data.download;

import de.p2tools.atplayer.controller.config.ProgConst;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.atdate.AudioList;
import de.p2tools.p2lib.configfile.pdata.P2DataList;
import de.p2tools.p2lib.tools.P2GetList;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.*;

public class DownloadList extends SimpleListProperty<DownloadData> implements P2DataList<DownloadData> {

    public static final String TAG = "DownloadList";
    private final ProgData progData;
    private final DownloadListStarts downloadListStarts;
    private final ObservableList<DownloadData> undoList = FXCollections.observableArrayList();
    private BooleanProperty downloadsChanged = new SimpleBooleanProperty(true);
    FilteredList<DownloadData> filteredList = null;
    SortedList<DownloadData> sortedList = null;

    public DownloadList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
        this.downloadListStarts = new DownloadListStarts(progData, this);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Downloads";
    }

    @Override
    public DownloadData getNewItem() {
        return new DownloadData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(DownloadData.class)) {
            DownloadData d = (DownloadData) obj;
            add(d);
        }
    }

    public synchronized void initDownloads() {
        this.forEach(download -> {
            // cleanUp
            download.setPlacedBack(false);
            download.setFile(download.getDestPathFile());
        });
    }

    public SortedList<DownloadData> getSortedList() {
        initFilterdList();
        return sortedList;
    }

    public FilteredList<DownloadData> getFilteredList() {
        initFilterdList();
        return filteredList;
    }

    private void initFilterdList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
    }

    public ObservableList<DownloadData> getUndoList() {
        return undoList;
    }

    public synchronized void addDownloadsToUndoList(List<DownloadData> list) {
        undoList.clear();
        undoList.addAll(list);
    }

    public synchronized void undoDownloads() {
        if (undoList.isEmpty()) {
            return;
        }
        //aus der Abo-History löschen
        addAll(undoList);
        undoList.clear();
    }

    public boolean getDownloadsChanged() {
        return downloadsChanged.get();
    }

    synchronized void setDownloadsChanged() {
        downloadsChanged.set(!downloadsChanged.get());
    }

    public BooleanProperty downloadsChangedProperty() {
        return downloadsChanged;
    }

    public void sort() {
        Collections.sort(this);
    }

    @Override
    public synchronized boolean add(DownloadData d) {
        return super.add(d);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends DownloadData> elements) {
        return super.addAll(elements);
    }

    public synchronized boolean addWithNo(DownloadData e) {
        final boolean ret = super.add(e);
        setNumbersInList();
        return ret;
    }

    public synchronized void addWithNo(List<DownloadData> list) {
        list.stream().forEach(download -> super.add(download));
        setNumbersInList();
    }

    @Override
    public synchronized boolean removeAll(Collection<?> objects) {
        return super.removeAll(objects);
    }

    public synchronized int countStartedAndRunningDownloads() {
        // es wird nach noch nicht fertigen, gestarteten Downloads gesucht
        int ret = 0;
        for (final DownloadData download : this) {
            if (download.isStateStartedWaiting() || download.isStateStartedRun()) {
                ++ret;
            }
        }
        return ret;
    }

    public synchronized void addAudioInList(AudioList filmlist) {
        // bei einmal Downloads nach einem Programmstart/Neuladen der Audioliste
        // den Film wieder eintragen
        P2Duration.counterStart("addAudioInList");
        int counter = 50;
        for (DownloadData d : this) {
            --counter;
            if (counter < 0) {
                break;
            }
            d.setAudioData(filmlist.getAudioByUrl(d.getUrl()));
        }
        P2Duration.counterStop("addAudioInList");
    }

    public synchronized void preferDownloads(ArrayList<DownloadData> prefDownList) {
        // macht nur Sinn, wenn der Download auf Laden wartet: Init
        // todo auch bei noch nicht gestarteten ermöglichen
        prefDownList.removeIf(d -> d.getState() != DownloadConstants.STATE_STARTED_WAITING);
        if (prefDownList.isEmpty()) {
            return;
        }

        // zum neu nummerieren der alten Downloads
        List<DownloadData> list = new ArrayList<>();
        for (final DownloadData download : this) {
            final int i = download.getNo();
            if (i < ProgConst.NUMBER_NOT_EXISTS) {
                list.add(download);
            }
        }
        prefDownList.forEach(list::remove);
        list.sort((d1, d2) -> (d1.getNo() < d2.getNo()) ? -1 : 1);
        int addNr = prefDownList.size();
        for (final DownloadData download : list) {
            ++addNr;
            download.setNo(addNr);
        }

        // und jetzt die vorgezogenen Downloads nummerieren
        int i = 1;
        for (final DownloadData dataDownload : prefDownList) {
            dataDownload.setNo(i++);
        }
    }

    public synchronized DownloadData getDownloadByUrl(String url) {
        DownloadData ret = null;
        for (final DownloadData download : this) {
            if (download.getUrl().equals(url)) {
                ret = download;
                break;
            }
        }
        return ret;
    }

    public synchronized int countRunningDownloads() {
        int count = 0;
        for (final DownloadData download : this) {
            if (download.isStateStartedRun()) {
                ++count;
            }
        }
        return count;
    }

    public synchronized DownloadData getDownloadUrlFilm(String urlFilm) {
        for (final DownloadData dataDownload : this) {
            if (dataDownload.getUrl().equals(urlFilm)) {
                return dataDownload;
            }
        }
        return null;
    }

    public synchronized void cleanUpList() {
        // fertige Downloads löschen, fehlerhafte zurücksetzen
        boolean found = false;
        Iterator<DownloadData> it = this.iterator();
        while (it.hasNext()) {
            DownloadData download = it.next();
            if (download.isStateInit() ||
                    download.isStateStopped()) {
                continue;
            }
            if (download.isStateFinished()) {
                // alles was fertig/fehlerhaft ist, kommt beim putzen weg
                it.remove();
                found = true;
            } else if (download.isStateError()) {
                // fehlerhafte werden zurückgesetzt
                download.resetDownload();
                found = true;
            }
        }

        if (found) {
            setDownloadsChanged();
        }
    }

    public synchronized List<DownloadData> getListOfStartsNotFinished() {
        return downloadListStarts.getListOfStartsNotFinished();
    }

    public synchronized List<DownloadData> getListOfStartsNotLoading() {
        return downloadListStarts.getListOfStartsNotLoading();
    }

    public synchronized DownloadData getRestartDownload() {
        return downloadListStarts.getRestartDownload();
    }

    public synchronized DownloadData getNextStart() {
        return downloadListStarts.getNextStart();
    }

    public synchronized void resetPlacedBack() {
        // zurückgestellte wieder aktivieren
        forEach(d -> d.setPlacedBack(false));
        setDownloadsChanged();
    }

    // ==============================
    // DownloadListStartStop
//    public synchronized void stopDownloads(DownloadData downloadData) {
//        if (DownloadFactoryStopDownload.stopDownloads(downloadData)) {
//            setDownloadsChanged();
//        }
//    }

    public synchronized void stopDownloads(ArrayList<DownloadData> list) {
        if (DownloadFactoryStopDownload.stopDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public void stopWaitingDownloads() {
        // es werden alle noch nicht gestarteten Downloads gestoppt
        final ArrayList<DownloadData> listStopDownload = new ArrayList<>();
        stream().filter(DownloadData::isStateStartedWaiting).forEach(listStopDownload::add);
        progData.downloadList.stopDownloads(listStopDownload);
    }

    public synchronized void delDownloads(DownloadData download) {
        DownloadFactoryStopDownload.delDownloads(this, new P2GetList<DownloadData>().getArrayList(download));
    }

    public synchronized void delDownloads(ArrayList<DownloadData> list) {
        if (DownloadFactoryStopDownload.delDownloads(this, list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void putBackDownloads(ArrayList<DownloadData> list) {
        if (DownloadFactoryStopDownload.putBackDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public void startDownloads(DownloadData download) {
        DownloadFactoryStartDownload.startDownloads(this,
                new P2GetList<DownloadData>().getArrayList(download));
        setDownloadsChanged();
    }

    public void startDownloads(Collection<DownloadData> list, boolean alsoFinished) {
        if (DownloadFactoryStartDownload.startDownloads(this, list, alsoFinished)) {
            setDownloadsChanged();
        }
    }

    public synchronized void setNumbersInList() {
        int i = getNextNumber();
        for (final DownloadData download : this) {
            if (download.isStarted()) {
                // gestartete Downloads ohne!! Nummer nummerieren
                if (download.getNo() == ProgConst.NUMBER_NOT_EXISTS) {
                    download.setNo(i++);
                }

            } else {
                // nicht gestartete Downloads
                download.setNo(ProgConst.NUMBER_NOT_EXISTS);
            }
        }
    }

    public synchronized void renumberList(int addNr) {
        for (final DownloadData download : this) {
            final int i = download.getNo();
            if (i < ProgConst.NUMBER_NOT_EXISTS) {
                download.setNo(i + addNr);
            }
        }
    }

    private int getNextNumber() {
        int i = 1;
        for (final DownloadData download : this) {
            if (download.getNo() < ProgConst.NUMBER_NOT_EXISTS && download.getNo() >= i) {
                i = download.getNo() + 1;
            }
        }
        return i;
    }

    public synchronized void addNumber(ArrayList<DownloadData> downloads) {
        int i = getNextNumber();
        for (DownloadData download : downloads) {
            download.setNo(i++);
        }
    }
}
