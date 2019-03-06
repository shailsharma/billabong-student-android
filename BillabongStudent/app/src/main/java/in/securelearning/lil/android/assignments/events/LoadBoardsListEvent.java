package in.securelearning.lil.android.assignments.events;

import java.util.ArrayList;

import in.securelearning.lil.android.base.dataobjects.Board;

/**
 * Enter Copyright Javadoc Comments here
 * <p>
 * Created by Pushkar Raj 7/28/2016.
 */
public class LoadBoardsListEvent {
    private final ArrayList<Board> mBoards;

    public LoadBoardsListEvent(ArrayList<Board> boards) {
        this.mBoards = boards;
    }

    public ArrayList<Board> getBoardsList() {
        return mBoards;
    }
}
