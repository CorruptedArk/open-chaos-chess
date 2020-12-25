/*
 * Open Chaos Chess is a free as in speech version of Chaos Chess
 * Chaos Chess is a chess game where you control the piece that moves, but not how it moves
 *     Copyright (C) 2019  Noah Stanford <noahstandingford@gmail.com>
 *
 *     Open Chaos Chess is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Open Chaos Chess is distributed in the hope that it will be fun,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.corruptedark.openchaoschess;


import android.content.Context;
import android.view.View;

public class MoveThread extends Thread {
    public final int YOU = -1;
    public final int OPPONENT = 1;
    public final int NONE = 0;

    private View view;
    private boolean single;
    private SinglePlayerBoard singlePlayerBoard;
    private MultiPlayerBoard multiPlayerBoard;
    private volatile ColorManager colorManager;

    public MoveThread(View view, SinglePlayerBoard singlePlayerBoard) {
        single = true;
        this.view = view;
        this.singlePlayerBoard = singlePlayerBoard;
    }

    public MoveThread(View view, MultiPlayerBoard multiPlayerBoard) {
        single = false;
        this.view = view;
        this.multiPlayerBoard = multiPlayerBoard;
    }

    public void run() {
        final Square clicked;
        final Square selected;
        Context context;

        if (singlePlayerBoard != null) {
            context = singlePlayerBoard;
        }
        else {
            context = multiPlayerBoard;
        }

        colorManager = ColorManager.getInstance(context);

        if (single) {
            clicked = (Square) view;
            selected = singlePlayerBoard.selected;
            if (clicked.getTeam() == YOU && !singlePlayerBoard.selected.equals(clicked)) {
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
                            selected.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });
                else
                    singlePlayerBoard.selected.post(new Runnable() {
                        @Override
                        public void run() {
                            selected.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2));
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });
                singlePlayerBoard.selected = clicked;
                singlePlayerBoard.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clicked.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SELECTION_COLOR));
                        singlePlayerBoard.boardMain.invalidate();
                    }
                });
            } else if (singlePlayerBoard.selected == clicked) {
                if (singlePlayerBoard.selected.getColor())
                    singlePlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });

                else
                    singlePlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2));
                            singlePlayerBoard.boardMain.invalidate();
                        }
                    });
                singlePlayerBoard.moveSelectedButton_Click(view);

            }
        } else {
            clicked = (Square) view;
            selected = multiPlayerBoard.selected;
            if (clicked.getTeam() == YOU && !multiPlayerBoard.selected.equals(clicked)) {
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
                            selected.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });
                else
                    multiPlayerBoard.selected.post(new Runnable() {
                        @Override
                        public void run() {
                            selected.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2));
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });
                multiPlayerBoard.selected = clicked;
                multiPlayerBoard.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clicked.setBackgroundColor(colorManager.getColorFromFile(ColorManager.SELECTION_COLOR));
                        multiPlayerBoard.boardMain.invalidate();
                    }
                });
            } else if (multiPlayerBoard.selected == clicked) {
                if (multiPlayerBoard.selected.getColor())
                    multiPlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_1));
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });

                else
                    multiPlayerBoard.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clicked.setBackgroundColor(colorManager.getColorFromFile(ColorManager.BOARD_COLOR_2));
                            multiPlayerBoard.boardMain.invalidate();
                        }
                    });
                multiPlayerBoard.moveSelectedButton_Click(view);

            }
        }

    }

}
