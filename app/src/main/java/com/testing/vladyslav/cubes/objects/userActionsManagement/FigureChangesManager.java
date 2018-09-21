package com.testing.vladyslav.cubes.objects.userActionsManagement;

import com.testing.vladyslav.cubes.objects.PixioPoint;
import com.testing.vladyslav.cubes.util.Geometry;

import java.util.ArrayList;

public class FigureChangesManager {

    public static final int CAN_NOT_FORWARD_BACKWARD = 0;
    public static final int CAN_BACKWARD = 1;
    public static final int CAN_FORWARD = 2;
    public static final int CAN_FORWARD_BACKWARD = 3;

    public PixioPoint strideVector = new PixioPoint(0f, 0f, 0f);

    private int state;

    private Action cInterface;
    private ChangeCommitListener changeCommitListener;

    public void setChangeCommitListener(ChangeCommitListener listener){
        this.changeCommitListener = listener;
    }

    private int currentCommandPointer = -1;

    public interface ChangeCommitListener {
        void onChangeCommit();
    }

    public interface Action {
        void addCube(PixioPoint center, int color);
        void deleteCube(PixioPoint center);
        void paintCube(PixioPoint center, int color);
        void strideFigure(Geometry.Vector strideVector);
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

//    public void newCommandAddCube(final PixioPoint cubeCenter, final int color){
//
//        clearForwards();
//
//        commandsHistory.add(new Command() {
//            @Override
//            public void execute() {
//                cInterface.addCube(cubeCenter, color);
//                commitChange();
//            }
//
//            @Override
//            public void undo() {
//                cInterface.deleteCube(cubeCenter);
//                commitChange();
//            }
//        });
//
//        updateCommandPointer();
//        commandsHistory.get(currentCommandPointer).execute();
//
//    }
//
//    public void newCommandPaintCube(final PixioPoint cubeCenter, final int oldColor, final int newColor){
//
//        clearForwards();
//
//        commandsHistory.add(new Command() {
//            @Override
//            public void execute() {
//                cInterface.paintCube(cubeCenter, newColor);
//                commitChange();
//            }
//
//            @Override
//            public void undo() {
//                cInterface.paintCube(cubeCenter, oldColor);
//                commitChange();
//            }
//        });
//
//        updateCommandPointer();
//        commandsHistory.get(currentCommandPointer).execute();
//
//    }
//
//    public void newCommandDeleteCube(final PixioPoint cubeCenter, final int color){
//
//        clearForwards();
//
//        commandsHistory.add(new Command() {
//            @Override
//            public void execute() {
//                cInterface.deleteCube(cubeCenter);
//                commitChange();
//            }
//
//            @Override
//            public void undo() {
//                cInterface.addCube(cubeCenter, color);
//                commitChange();
//            }
//        });
//
//        updateCommandPointer();
//        commandsHistory.get(currentCommandPointer).execute();
//
//    }

    public void newCommandAddCube(final PixioPoint cubeCenter, final int color, final Geometry.Vector strideVector){

        clearForwards();

        commandsHistory.add(new Command() {
            @Override
            public void execute() {
                cInterface.addCube(cubeCenter, color);
                if(strideVector != null){
                    cInterface.strideFigure(strideVector);
                }
                commitChange();
            }

            @Override
            public void undo() {
                if(strideVector!= null){
                    cInterface.strideFigure(strideVector.invertedVector());
                }
                cInterface.deleteCube(cubeCenter);
                commitChange();
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
                commitChange();
            }

            @Override
            public void undo() {
                cInterface.paintCube(cubeCenter, oldColor);
                commitChange();
            }
        });

        updateCommandPointer();
        commandsHistory.get(currentCommandPointer).execute();

    }

    public void newCommandDeleteCube(final PixioPoint cubeCenter, final int color, final Geometry.Vector strideVector){

        clearForwards();

        commandsHistory.add(new Command() {
            @Override
            public void execute() {
                if(strideVector != null){
                    cInterface.strideFigure(strideVector);
                }
                cInterface.deleteCube(cubeCenter);
                commitChange();
            }

            @Override
            public void undo() {
                if(strideVector != null){
                    cInterface.strideFigure(strideVector.invertedVector());
                }
                cInterface.addCube(cubeCenter, color);
                commitChange();
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

    private void commitChange(){

        if(changeCommitListener != null){
            changeCommitListener.onChangeCommit();
        }

    }

}

