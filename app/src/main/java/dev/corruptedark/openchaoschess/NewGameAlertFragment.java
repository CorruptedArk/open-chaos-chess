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


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;



public class NewGameAlertFragment extends DialogFragment {

    private final String NEW_GAME = "New Game?";
    private final String YES = "Yes";
    private final String NO = "No";

    private MultiPlayerBoard context;
    private String tag;
    private boolean isHost;

    public NewGameAlertFragment(MultiPlayerBoard context, String tag, boolean isHost) {
        this.context = context;
        this.tag = tag;
        this.isHost = isHost;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder newGameAlertBuilder = new AlertDialog.Builder(context);

        newGameAlertBuilder.setTitle(NEW_GAME);

        newGameAlertBuilder.setPositiveButton(
                YES,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"Pressed Yes", Toast.LENGTH_SHORT).show();
                        context.multiPlayerService.sendData(YES);
                        Log.v(tag, "Sent yes");
                        context.tieLabel.setVisibility(View.INVISIBLE);
                        context.wonLabel.setVisibility(View.INVISIBLE);
                        context.lostLabel.setVisibility(View.INVISIBLE);
                        context.cantMoveThatLabel.setVisibility(View.INVISIBLE);
                        context.notYourTurnLabel.setVisibility(View.INVISIBLE);
                        context.gameOverLabel.setVisibility(View.INVISIBLE);
                        context.thatSucksLabel.setVisibility(View.INVISIBLE);
                        context.noiceLabel.setVisibility(View.INVISIBLE);

                        while (context.moveThread != null && context.moveThread.isAlive()) ;

                        context.selected = context.defaultSquare;
                        context.clearPieces();
                        context.multiGame.newGame(isHost);

                        context.startNewGame(context.getIntent().getBooleanExtra("knightsOnly", false));
                        context.yourPointLabel.setText(getResources().getText(R.string.your_points).toString() + " " + context.multiGame.getYourPoints());
                        context.opponentPointLabel.setText(getResources().getText(R.string.opponent_points).toString() + " " + context.multiGame.getOpponentPoints());
                        if (isHost) {
                            context.multiGame.setTurn(context.YOU);
                        } else {
                            context.multiGame.setTurn(context.OPPONENT);
                            context.moveOpponent();
                        }

                    }
                }
        );

        newGameAlertBuilder.setNegativeButton(
                NO,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"Pressed No", Toast.LENGTH_SHORT).show();
                        context.multiPlayerService.sendData(NO);
                        Log.v(tag,"Sent no");
                        context.onBackPressed();
                    }
                }
        );

        return newGameAlertBuilder.create();
    }

}
