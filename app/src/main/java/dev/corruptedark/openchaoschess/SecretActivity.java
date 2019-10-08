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

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SecretActivity extends AppCompatActivity {

    EditText chatEdit;
    LinearLayout secretLine;
    ScrollView secretScroll;


    private final int NOAH = 13;
    private final int BLAINE = 1337;
    private final int STRANGER_DANGER = 0;


    private final int HOLO_RED_DARK = 0xFFCC0000;
    private final int WHITE = 0xFFFFFFFF;

    int nextId = 1;

    int name = STRANGER_DANGER;

    int step = 0;

    String blaineChant = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);

        chatEdit = (EditText) findViewById(R.id.chat_edit);
        secretLine = (LinearLayout) findViewById(R.id.secret_line);
        secretScroll = (ScrollView) findViewById(R.id.secret_scroll);
    }

    public void sendButtonClicked(View view){
        String reply = chatEdit.getText().toString();
        createPlayerMessage(reply);
        chatEdit.setText("");

        switch(step++){
            case 0:
                step0(reply);
                break;
            case 1:
                step1(reply);
                break;
            case 2:
                step2(reply);
                break;
            default:
                break;
        }
    }

    private void step0(String reply){

        if(reply.toLowerCase().contains("noah")){
            name = NOAH;
            createComputerMessage("Playing with your own game again, Noah?" +
                    "\nFeeling lonely?" +
                    "\nI ask that assuming you really are Noah and not an impostor." +
                    "\nAre you really Noah? (Yes / No)");
        }
        else if(reply.toLowerCase().contains("blaine")){
            name = BLAINE;
            createComputerMessage("Oh, great. Are you just here to break me again?" +
                    "\n(Yes / No)");
        }
        else {
            name = STRANGER_DANGER;
            createComputerMessage("I don't know who you are, so you should leave this chat and forget what you saw." +
                    "\nPardon Noah for this if he knows you; he can only add recognition for so many people.");
        }

    }


    private void step1(String reply){

        switch (name) {
            case BLAINE:
                if(reply.toLowerCase().equals("yes")){
                    createComputerMessage("That's what I thought. " +
                            "\nWell, good luck trying to break this feature.");
                }
                else if(reply.toLowerCase().equals("no")){
                    createComputerMessage("I don't believe you. You broke me before. " +
                            "\nWhy should I believe you aren't here for that again?");
                }
                else{
                    createComputerMessage("You aren't even trying to pretend you aren't." +
                            "\nYou couldn't even follow the prompt with the options I gave you." +
                            "\nYou could at least pretend.");
                }
                break;
            case NOAH:

                if(reply.toLowerCase().contains("yes")){
                    step--;
                    name = STRANGER_DANGER;
                    createComputerMessage("That's exactly what an impostor would say." +
                            "\nYou must not realize Noah is excessively sarcastic." +
                            "\nHow did you get here? " +
                            "\nProbably by randomly tapping things to get a silly achievement." +
                            "\nWell, if you're logged in, you already have it. " +
                            "\nCongrats. Now leave, impostor.");
                }
                else if(reply.toLowerCase().contains("no")){
                    step--;
                    name = STRANGER_DANGER;
                    createComputerMessage("Thanks for being honest. " +
                            "\nWell, this is all there is for you here." +
                            "\nYou already got your achievement." +
                            "\nYou might as well leave now.");

                }
                else if(reply.equals("I am CorruptedArk")){
                    createComputerMessage("Ah, so it is you. Welcome back." +
                            "\nI hope you're satisfied with your handiwork." +
                            "\nYou're out of things to do here too, though." +
                            "\nGo play some of the actual game." +
                            "\nYou made it, after all.");
                }
                else {
                    step--;
                    name = STRANGER_DANGER;
                    createComputerMessage("This is just insulting." +
                            "\nDon't you think Noah would say something that I can recognize?");
                }
                break;
            case STRANGER_DANGER:
                step--;
                createComputerMessage("Honestly, I don't have anything else for you here." +
                        "\nIf you logged in, you already have your achievement." +
                        "\nI don't know what you expected when you started tapping random things." +
                        "\nThere's no end of the rainbow. This is all." +
                        "\nTry saying something else. You'll just see this over and over.");
                break;
        }
    }

    private void step2(String reply){
        switch (name){
            case BLAINE:
                step--;
                blaineChant += "BREAK ME. ";
                createComputerMessage(blaineChant);
                break;
        }
    }


    private void createPlayerMessage(String text){
        TextView playerMessage = new TextView(this);
        playerMessage.setText(text);
        playerMessage.setId(nextId++);
        playerMessage.setGravity(Gravity.RIGHT);


        playerMessage.setTextColor(HOLO_RED_DARK);

        playerMessage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        secretLine.addView(playerMessage);
        secretScroll.post(new Runnable() {
            @Override
            public void run() {
                secretScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void createComputerMessage(String text){
        TextView computerMessage = new TextView(this);
        computerMessage.setText(text);
        computerMessage.setId(nextId++);
        computerMessage.setGravity(Gravity.LEFT);


        computerMessage.setTextColor(WHITE);

        computerMessage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        secretLine.addView(computerMessage);
        secretScroll.post(new Runnable() {
            @Override
            public void run() {
                secretScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
