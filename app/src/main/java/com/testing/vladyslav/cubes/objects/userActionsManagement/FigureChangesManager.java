package com.testing.vladyslav.cubes.objects.userActionsManagement;

import com.testing.vladyslav.cubes.objects.PixioPoint;

import java.util.ArrayList;
import java.util.Iterator;

public class FigureChangesManager {

    public static final int CAN_NOT_FORWARD_BACKWARD = 0;
    public static final int CAN_BACKWARD = 1;
    public static final int CAN_FORWARD = 2;
    public static final int CAN_FORWARD_BACKWARD = 3;

    private int state;

    private Action cInterface;

    private int currentCommandPointer = -1;

    public interface Action {
        void addCube(PixioPoint center, int color);
        void deleteCube(PixioPoint center);
        void paintCube(PixioPoint center, int color);
    }


    private interface Command{
        void execute();
        void undo();
    }

    private ArrayList<Command> commandsHistory;

    public FigureChangesManager (Action cInterface){
        this.cInterface = cInterface;
        commandsHistory = new ArrayList<>();
    }

    public int getState(){

        state = 0;
        if(canBackwardChanges()) state ++;
        if(canForwardChanges()) state +=2;

        return state;
    }

    public void forward(){
        if(canForwardChanges()){
            commandsHistory.get(++currentCommandPointer).execute();
        }
    }

    public void backward(){
        if(canBackwardChanges()){
            commandsHistory.get(currentCommandPointer --).undo();
        }
    }

    private boolean canForwardChanges(){
        return currentCommandPointer < commandsHistory.size()-1;
    }

    private boolean canBackwardChanges(){
        return currentCommandPointer >= 0;
    }

    public void newCommandAddCube(final PixioPoint cubeCenter, final int color){

        clearForwards();

        commandsHistory.add(new Command() {
            @Override
            public void execute() {
                cInterface.addCube(cubeCenter, color);
            }

            @Override
            public void undo() {
                cInterface.deleteCube(cubeCenter);
            }
        });

        updateCommandPointer();
        commandsHistory.get(currentCommandPointer).execute();

    }

    public void newCommandPaintCube(final PixioPoint cubeCenter, final int oldColor, final int newColor){

        clearForwards();

        commandsHistory.add(new Command() {
            @Override
            public void execute() {
                cInterface.paintCube(cubeCenter, newColor);
            }

            @Override
            public void undo() {
                cInterface.paintCube(cubeCenter, oldColor);
            }
        });

        updateCommandPointer();
        commandsHistory.get(currentCommandPointer).execute();

    }

    public void newCommandDeleteCube(final PixioPoint cubeCenter, final int color){

        clearForwards();

        commandsHistory.add(new Command() {
            @Override
            public void execute() {
                cInterface.deleteCube(cubeCenter);
            }

            @Override
            public void undo() {
                cInterface.addCube(cubeCenter, color);
            }
        });

        updateCommandPointer();
        commandsHistory.get(currentCommandPointer).execute();

    }


    private void clearForwards(){

        if(currentCommandPointer < commandsHistory.size() -1){


            while(currentCommandPointer < commandsHistory.size()-1){

                commandsHistory.remove(commandsHistory.size()-1);

            }

        }

    }
    private void updateCommandPointer(){

        currentCommandPointer = commandsHistory.size()-1;

    }

}

