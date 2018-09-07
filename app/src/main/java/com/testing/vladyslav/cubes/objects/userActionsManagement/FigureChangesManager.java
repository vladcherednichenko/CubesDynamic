package com.testing.vladyslav.cubes.objects.userActionsManagement;

import com.testing.vladyslav.cubes.objects.PixioPoint;
import com.testing.vladyslav.cubes.util.PixioColor;

import java.util.ArrayList;

public class FigureChangesManager {

    private Action cInterface;

    public static int ADDCUBECOMMAND = 0;
    public static int PAINTCUBECOMMAND = 1;
    public static int DELETECUBECOMMAND = 2;

    public interface Action {
        void addCube(PixioPoint center, PixioColor color);
        void deleteCube(PixioPoint center);
        void paintCube(PixioPoint center, PixioColor color);
    }

    private interface Command{
        void execute();
        void undo();
    }

    private ArrayList<Command> changesStack;
    private ArrayList<Command> undoChangesStack;

    public FigureChangesManager (Action cInterface){
        this.cInterface = cInterface;
    }

    public void commandAddCube(PixioPoint cubeCenter, PixioColor color){

    }

    public void commandPaintCube(PixioPoint cubeCenter, PixioColor oldColor, PixioColor newColor){

    }

    public void commandDeleteCube(PixioPoint cubeCenter){

    }

}

