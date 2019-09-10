package dev.corruptedark.openchaoschess;


import android.view.View;

/**
 * Created by CorruptedArk
 */

public class MoveThread extends Thread {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private View view;
    private boolean single;
    private SinglePlayerBoard singlePlayerBoard;
    private MultiPlayerBoard multiPlayerBoard;
    private int color1;
    private int color2;
    private int selectColor;

    public MoveThread(View view, SinglePlayerBoard singlePlayerBoard, int color1, int color2, int selectColor){
        single = true;
        this.view = view;
        this.singlePlayerBoard = singlePlayerBoard;
        this.color1 = color1;
        this.color2 = color2;
        this.selectColor = selectColor;
    }

    public MoveThread(View view, MultiPlayerBoard multiPlayerBoard, int color1, int color2, int selectColor)
    {
        single = false;
        this.view = view;
        this.multiPlayerBoard = multiPlayerBoard;
        this.color1 = color1;
        this.color2 = color2;
        this.selectColor = selectColor;
    }

    public void run() {
        final Square clicked;
        final Square selected;
        if(single)
        {
            clicked = (Square) view;
            selected = singlePlayerBoard.selected;
            if (clicked.getTeam() == YOU && !singlePlayerBoard.selected.equals(clicked))
            {
                singlePlayerBoard.cantMoveThatLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        singlePlayerBoard.cantMoveThatLabel.setVisibility(View.INVISIBLE);
                    }
                });
                if (singlePlayerBoard.selected.getColor())
                    singlePlayerBoard.selected.post(new Runnable() {
                        @Override
                        public void run() {
                            selected.setBackgroundColor(color1);
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });
                else
                    singlePlayerBoard.selected.post(new Runnable() {
                        @Override
                        public void run() {
                            selected.setBackgroundColor(color2);
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });
                singlePlayerBoard.selected = clicked;
                singlePlayerBoard.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clicked.setBackgroundColor(selectColor);
                        singlePlayerBoard.boardMain.invalidate();
                    }
                });
            }
            else if (singlePlayerBoard.selected == clicked)
            {
                if (singlePlayerBoard.selected.getColor())
                    singlePlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(color1);
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });

                else
                    singlePlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(color2);
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });
                singlePlayerBoard.moveSelectedButton_Click(view);

            }
        }
        else
        {
            clicked = (Square) view;
            selected = multiPlayerBoard.selected;
            if (clicked.getTeam() == YOU && !multiPlayerBoard.selected.equals(clicked))
            {
                multiPlayerBoard.cantMoveThatLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        multiPlayerBoard.cantMoveThatLabel.setVisibility(View.INVISIBLE);
                    }
                });
                if (multiPlayerBoard.selected.getColor())
                    multiPlayerBoard.selected.post(new Runnable() {
                        @Override
                        public void run() {
                            selected.setBackgroundColor(color1);
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });
                else
                    multiPlayerBoard.selected.post(new Runnable() {
                        @Override
                        public void run() {
                            selected.setBackgroundColor(color2);
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });
                multiPlayerBoard.selected = clicked;
                multiPlayerBoard.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clicked.setBackgroundColor(selectColor);
                        multiPlayerBoard.boardMain.invalidate();
                    }
                });
            }
            else if (multiPlayerBoard.selected == clicked)
            {
                if (multiPlayerBoard.selected.getColor())
                    multiPlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(color1);
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });

                else
                    multiPlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(color2);
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });
                multiPlayerBoard.moveSelectedButton_Click(view);

            }
        }

    }

}
