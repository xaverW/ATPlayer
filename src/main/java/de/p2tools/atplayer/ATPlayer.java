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
package de.p2tools.atplayer;

import de.p2tools.atplayer.controller.ProgQuit;
import de.p2tools.atplayer.controller.ProgStartAfterGui;
import de.p2tools.atplayer.controller.ProgStartBeforeGui;
import de.p2tools.atplayer.controller.config.PShortKeyFactory;
import de.p2tools.atplayer.controller.config.ProgColorList;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibInit;
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ATPlayer extends Application {

    protected ProgData progData;
    private Scene scene = null;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        P2Duration.counterStart("ATPlayer");
        progData = ProgData.getInstance();
        progData.primaryStage = primaryStage;

        ProgStartBeforeGui.workBeforeGui();
        initRootLayout();
        ProgStartAfterGui.doWorkAfterGui();

        P2Duration.onlyPing("Gui steht!");
        P2Duration.counterStop("ATPlayer");
    }

    private void initRootLayout() {
        try {
            progData.ATPlayerController = new ATPlayerController();

            scene = new Scene(progData.ATPlayerController,
                    P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, true),
                    P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, false));//Größe der scene!= Größe stage!!!
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest(e -> {
                //beim Beenden
                e.consume();
                ProgQuit.quit(false);
            });

            primaryStage.setOnShowing(e -> P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null));
            primaryStage.setOnShown(e -> P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null));
            ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> ProgColorList.setColorTheme());

            PShortKeyFactory.addShortKey(scene);
            P2LibInit.addP2CssToScene(scene); // und jetzt noch CSS einstellen

            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
