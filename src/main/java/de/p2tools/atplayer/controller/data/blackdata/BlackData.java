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

import de.p2tools.p2lib.mtfilter.Filter;

public class BlackData extends BlackDataProps {

    public Filter fChannel = new Filter();
    public Filter fGenre = new Filter();
    public Filter fTheme = new Filter();
    public Filter fThemeTitle = new Filter();
    public Filter fTitle = new Filter();
    public boolean quickChannel = false;
    public boolean quickGenre = false;
    public boolean quickTheme = false;
    public boolean quickThemTitle = false;
    public boolean quickTitle = false;

    public BlackData() {
        super();
        initFilter();
    }

    public BlackData(String sender, String genre, String theme, String title, String themeTitle) {
        super();
        initFilter();

        setChannel(sender);
        setGenre(genre);
        setTheme(theme);
        setTitle(title);
        setThemeTitle(themeTitle);
    }

    private void createFilter() {
        fChannel.filter = getChannel();
        fChannel.isExact = false;
        fChannel.makeFilterArray();

        fGenre.filter = getGenre();
        fGenre.isExact = false;
        fGenre.makeFilterArray();

        fTheme.filter = getTheme();
        fTheme.isExact = isThemeExact();
        fTheme.makeFilterArray();

        fThemeTitle.filter = getThemeTitle();
        fThemeTitle.makeFilterArray();

        fTitle.filter = getTitle();
        fTitle.makeFilterArray();

        setQuickChannel();
        setQuickGenre();
        setQuickTheme();
        setQuickThemeTitle();
        setQuickTitle();
    }

    private void setQuickChannel() {
        if (fGenre.isEmpty &&
                fTheme.isEmpty &&
                fThemeTitle.isEmpty &&
                fTitle.isEmpty &&
                fChannel.isQuick &&
                !fChannel.exclude) {
            quickChannel = true;

        } else {
            quickChannel = false;
        }
    }

    private void setQuickGenre() {
        if (fChannel.isEmpty &&
                fTheme.isEmpty &&
                fThemeTitle.isEmpty &&
                fTitle.isEmpty &&
                fGenre.isQuick &&
                !fGenre.exclude) {
            quickGenre = true;

        } else {
            quickGenre = false;
        }
    }

    private void setQuickTheme() {
        if (fChannel.isEmpty &&
                fGenre.isEmpty &&
                fThemeTitle.isEmpty &&
                fTitle.isEmpty &&
                fTheme.isQuick &&
                !fTheme.exclude) {
            quickTheme = true;

        } else {
            quickTheme = false;
        }
    }

    private void setQuickThemeTitle() {
        if (fChannel.isEmpty &&
                fGenre.isEmpty &&
                fTheme.isEmpty &&
                fTitle.isEmpty &&
                fThemeTitle.isQuick &&
                !fThemeTitle.exclude) {
            quickThemTitle = true;

        } else {
            quickThemTitle = false;
        }
    }

    private void setQuickTitle() {
        if (fChannel.isEmpty &&
                fGenre.isEmpty &&
                fTheme.isEmpty &&
                fThemeTitle.isEmpty &&
                fTitle.isQuick &&
                !fTitle.exclude) {
            quickTitle = true;

        } else {
            quickTitle = false;
        }
    }

    private void initFilter() {
        channelProperty().addListener(l -> createFilter());
        genreProperty().addListener(l -> createFilter());

        themeProperty().addListener(l -> createFilter());
        themeExactProperty().addListener(l -> createFilter());

        themeTitleProperty().addListener(l -> createFilter());
        titleProperty().addListener(l -> createFilter());
    }
}
