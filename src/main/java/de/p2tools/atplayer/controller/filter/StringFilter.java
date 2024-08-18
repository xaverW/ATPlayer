package de.p2tools.atplayer.controller.filter;

import de.p2tools.p2lib.configfile.config.Config;
import de.p2tools.p2lib.configfile.configlist.ConfigStringList;
import de.p2tools.p2lib.configfile.pdata.P2DataSample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class StringFilter extends P2DataSample<StringFilter> implements Comparable<StringFilter> {

    public static String TAG = "StringFilter";

    private final ObservableList<String> filterListAudioTheme = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListAudioTitle = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListAudioSomewhere = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListDownloadTheme = FXCollections.observableArrayList("");
    private final ObservableList<String> filterListDownloadTitle = FXCollections.observableArrayList("");


    public StringFilter() {
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigStringList("filterListAudioTheme", filterListAudioTheme));
        list.add(new ConfigStringList("filterListAudioTitle", filterListAudioTitle));
        list.add(new ConfigStringList("filterListAudioSomewhere", filterListAudioSomewhere));

        list.add(new ConfigStringList("filterListDownloadTheme", filterListDownloadTheme));
        list.add(new ConfigStringList("filterListDownloadTitel", filterListDownloadTitle));
        return list.toArray(new Config[]{});
    }

    public ObservableList<String> getFilterListAudioTheme() {
        return filterListAudioTheme;
    }

    public ObservableList<String> getFilterListAudioTitle() {
        return filterListAudioTitle;
    }

    public ObservableList<String> getFilterListAudioSomewhere() {
        return filterListAudioSomewhere;
    }

    public ObservableList<String> getFilterListDownloadTheme() {
        return filterListDownloadTheme;
    }

    public ObservableList<String> getFilterListDownloadTitle() {
        return filterListDownloadTitle;
    }
}
