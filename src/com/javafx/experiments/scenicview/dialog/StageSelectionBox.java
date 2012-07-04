package com.javafx.experiments.scenicview.dialog;

import java.util.*;

import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import com.javafx.experiments.scenicview.ScenicView;
import com.javafx.experiments.scenicview.connector.*;
import com.javafx.experiments.scenicview.connector.helper.*;
import com.javafx.experiments.scenicview.connector.helper.WindowChecker.WindowFilter;

public class StageSelectionBox {
    private static final int SCENE_WIDTH = 200;
    private static final int SCENE_HEIGHT = 300;
    private static final int LEFT_AND_RIGHT_MARGIN = 10;
    private static final int SPACER_Y = 10;
    private final VBox panel;
    private final Stage stage;
    private final Scene scene;
    private final ListView<String> windowList;

    private StageSelectionBox(final String title, final double x, final double y, final Stage stageScenic, final ScenicView scenicView, final List<AppController> activeApps, final List<AppController> allApps) {
        this.panel = new VBox();
        this.panel.getStyleClass().add("stageSelection");

        // Find Local App

        final List<StageController> active = new ArrayList<StageController>();
        for (int i = 0; i < allApps.size(); i++) {
            active.addAll(allApps.get(i).getStages());
        }
        this.windowList = new ListView<String>();
        // this.windowList.setCellFactory(new Callback<ListView<String>,
        // ListCell<String>>() {
        // @Override public ListCell<String> call(final ListView<String> list) {
        // final ListCell<String> cell = new ListCell<String>() {
        //
        // @Override public void updateItem(final String item, final boolean
        // empty) {
        //
        // super.updateItem(item, empty);
        //
        // if (item != null) {
        // this.setText(item);
        //
        // setOnMouseClicked(new EventHandler<MouseEvent>() {
        // @Override public void handle(final MouseEvent mouseEvent) {
        // if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
        // if (mouseEvent.getClickCount() == 2) {
        // final int index = windowList.getSelectionModel().getSelectedIndex();
        // onSelected(scenicView, (Stage) stages.get(index), local);
        // }
        // }
        // }
        // });
        //
        // }
        // }
        // };
        // return cell;
        // }
        // });
        windowList.setFocusTraversable(false);
        windowList.setEditable(false);
        windowList.setId("stageSelectionList");
        windowList.setPrefHeight(221.0D);
        windowList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final ObservableList<String> stageNames = FXCollections.observableArrayList();
        final List<Integer> selected = new ArrayList<Integer>();
        for (int i = 0; i < active.size(); i++) {
            final StageController c = active.get(i);
            stageNames.add(c.getAppController().toString() + " - " + c.getID().getName());
            final int index = indexOf(activeApps, c.getAppController());
            if (index != -1 && indexOf(activeApps.get(index).getStages(), c) != -1) {
                selected.add(i);
            }
        }
        windowList.setItems(stageNames);
        final Integer[] selInd = selected.toArray(new Integer[selected.size()]);
        for (int i = 0; i < selInd.length; i++) {
            windowList.getSelectionModel().select(selInd[i]);
        }

        final Label select = new Label("Select a stage");
        final Button ok = new Button("Ok");
        ok.disableProperty().bind(windowList.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
        ok.setOnAction(new EventHandler<ActionEvent>() {

            @Override public void handle(final ActionEvent arg0) {
                final List<Integer> selected = windowList.getSelectionModel().getSelectedIndices();
                final List<StageController> controllers = new ArrayList<StageController>();
                for (final Iterator<Integer> iterator = selected.iterator(); iterator.hasNext();) {
                    final int index = iterator.next();
                    controllers.add(active.get(index));
                }
                final List<AppController> selectedApp = new ArrayList<AppController>();
                for (final Iterator<AppController> iterator = allApps.iterator(); iterator.hasNext();) {
                    final AppController c = iterator.next();
                    final List<StageController> sc = c.getStages();
                    for (final Iterator<StageController> iterator2 = sc.iterator(); iterator2.hasNext();) {
                        final StageController scc = iterator2.next();
                        if (indexOf(controllers, scc) != -1) {
                            if (!selectedApp.contains(scc.getAppController())) {
                                selectedApp.add(scc.getAppController());
                            }
                        } else {
                            iterator2.remove();
                        }
                    }
                }
                onSelected(scenicView, selectedApp);

            }
        });
        VBox.setMargin(select, new Insets(SPACER_Y, LEFT_AND_RIGHT_MARGIN, 0, LEFT_AND_RIGHT_MARGIN));
        VBox.setMargin(this.windowList, new Insets(SPACER_Y, LEFT_AND_RIGHT_MARGIN, 0, LEFT_AND_RIGHT_MARGIN));
        VBox.setMargin(ok, new Insets(SPACER_Y, LEFT_AND_RIGHT_MARGIN, SPACER_Y, LEFT_AND_RIGHT_MARGIN));
        VBox.setVgrow(this.windowList, Priority.ALWAYS);
        this.panel.setAlignment(Pos.TOP_CENTER);

        this.panel.getChildren().addAll(select, this.windowList, ok);

        this.scene = SceneBuilder.create().width(SCENE_WIDTH).height(SCENE_HEIGHT).root(this.panel).stylesheets(ScenicView.STYLESHEETS).build();

        this.stage = StageBuilder.create().style(StageStyle.UTILITY).title(title).build();
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setScene(this.scene);
        this.stage.getIcons().add(ScenicView.APP_ICON);
        this.stage.setResizable(false);
        this.stage.setX(x);
        this.stage.setY(y);
        this.stage.show();
    }

    private int indexOf(final List<StageController> stages, final StageController c) {
        for (int i = 0; i < stages.size(); i++) {
            if (stages.get(i).getID().equals(c.getID())) {
                return i;
            }
        }
        return -1;
    }

    private int indexOf(final List<AppController> apps, final AppController c) {
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).getID() == c.getID()) {
                return i;
            }
        }
        return -1;
    }

    private void onSelected(final ScenicView scenicView, final List<AppController> controllers) {
        scenicView.setNewApps(controllers);
        this.stage.close();
    }

    public static StageSelectionBox make(final String title, final ScenicView scenicView, final List<AppController> active) {
        final Stage stage = (Stage) scenicView.getScene().getWindow();

        final List<Window> stages = WindowChecker.getValidWindows(new WindowFilter() {

            @Override public boolean accept(final Window window) {
                if (window instanceof Stage && window != stage) {
                    return true;
                }
                return false;
            }
        });
        final AppController local = new AppControllerImpl();
        for (final Iterator<Window> iterator = stages.iterator(); iterator.hasNext();) {
            final Stage window = (Stage) iterator.next();
            final StageController c = new StageControllerImpl(window.getScene().getRoot(), local);
            c.getID().setName(window.getTitle());
            local.getStages().add(c);
        }
        final List<AppController> controllers = new ArrayList<AppController>();
        controllers.add(local);
        return make(title, scenicView, active, controllers);
    }

    public static StageSelectionBox make(final String title, final ScenicView scenicView, final List<AppController> active, final List<AppController> controllers) {
        final Stage stage = (Stage) scenicView.getScene().getWindow();
        return new StageSelectionBox(title, stage == null ? 0 : stage.getX() + (stage.getWidth() / 2) - (SCENE_WIDTH / 2), stage == null ? 0 : stage.getY() + (stage.getHeight() / 2) - (SCENE_HEIGHT / 2), stage, scenicView, active, controllers);
    }

}