package com.example.nazanin.sheryadetnare;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class LyricsJumbleActivity extends AppCompatActivity {

    private ArrayList<String> answerTextViews=new ArrayList<>();
    private ArrayList<TextView> showableTextviews=new ArrayList<>();
    private boolean undoAnswer=false,gotMoreTime=false,gotHelp=false;
    static int LyricsJumbleScore;
    //    public static final String EXTRA_SCORE = "lyricJumbleExtraScore";
    private ArrayList<Button> mButtons;
    private String originalText="";
    private SeekBar seekBar;
    private String[] withoutSpaces,correctText;
    private int[] selectedStates;
    private Button score,selection,cb = null;
    private ImageButton play,stop,replay,help,timerhelp;
    private Handler waitForColoring;
    private Runnable restart;
    static int genre=1;
    private ArrayList<Button> selections=new ArrayList<>();
    private TextView timer,correctAnswer,beforeGame,answerField,selectedView;
    private int number,count=0,countSize=0,numOfChangableViews=0;
    public static final String EXTRA_SCORE1 = "lyricJumbleExtraScore";
    public static final String EXTRA_SCORE11 = "lyricJumbleCoinsExtraScore";
    private static final int COUNTDOWN_BEFORE_STARTING=4000,COUNTDOWN_WHILE_PLAYING=16000;
    private CountDownTimer whilePlaying;
    private long timeLeftToStart,timeLeftToFinish;
    private ColorStateList textColorDefaultCD;
    private MusicManager music;
    private GridView gridView,gridTextView;
    private GameDbHelper gameDbHelper;
    private Help whichWord;
    private static final int REQUEST_NUMBER=1;
    private  TextView ansPerSet;
    private ArrayList<TextView> fields=new ArrayList<>();
    private int part = 1;
    private TextView textViewCoin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_jumble);
        seekBar=(SeekBar)findViewById(R.id.seekbar);
        help= (ImageButton)findViewById(R.id.help);
        timerhelp=(ImageButton)findViewById(R.id.timerHelp);
        music=new MusicManager(seekBar);
        textViewCoin=(TextView)findViewById(R.id.coin);
//        lyricJumbleScore=0;
        whichWord=new Help();
        waitForColoring=new Handler();
        gameDbHelper=new GameDbHelper(this);
        correctAnswer=(TextView)findViewById(R.id.correctAnswer);
        timer=(TextView)findViewById(R.id.text_view_countdown);
        textColorDefaultCD=timer.getTextColors();
        score=(Button) findViewById(R.id.scorebutton);
        beforeGame=(TextView)findViewById(R.id.beforeGame);
        play=(ImageButton)findViewById(R.id.play);
        stop=(ImageButton)findViewById(R.id.pause);
        replay=(ImageButton)findViewById(R.id.reset);
        play.setOnClickListener(music.play);
        stop.setOnClickListener(music.stop);
        replay.setOnClickListener(music.reset);
        timerhelp.setEnabled(false);
        help.setEnabled(false);
        play.setEnabled(false);
        stop.setEnabled(false);
        replay.setEnabled(false);
        timerhelp.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);
        replay.setVisibility(View.GONE);
        timeLeftToStart=COUNTDOWN_BEFORE_STARTING;
     //   textViewCoin.setText(StartingScreenActivity.coin + "سکه ها:");

        displayQuestionPerSet();
        startGame();
        //  CountBeforeStarting();

    }

    private void displayQuestionPerSet(){
        LinearLayout answer_layout = (LinearLayout)findViewById(R.id.ansfield);

        for(int i = 0 ; i<3; i++)
        {
            ansPerSet= new TextView(this);
            ansPerSet.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ansPerSet.setBackgroundResource(R.drawable.ans_per_set);
            ansPerSet.setId(i);
            ansPerSet.setWidth(50);
            ansPerSet.setHeight(50);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10,10, 10);
            ansPerSet.setLayoutParams(params);
            fields.add(ansPerSet);
            answer_layout.addView(ansPerSet);

        }
    }




    private void startGame(){

        //   game_db_helper gameDbHelper=new game_db_helper(this);
        music.generateRandomId(genre);
        score.setText(""+LyricsJumbleScore);
        music.playTheMusic(this);
        seekBar.setEnabled(true);
        music.song.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                count++;
                if (count == 1) {
                    setupViewForFirstRound();
                }
            }
        });


    }

    public void setupViewForFirstRound(){
        displayWords();
        displayAnswerFields();
        //show and enable buttons after words are displayed
        help.setEnabled(true);
        play.setEnabled(true);
        stop.setEnabled(true);
        replay.setEnabled(true);
        play.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);
        replay.setVisibility(View.VISIBLE);
        timeLeftToFinish=COUNTDOWN_WHILE_PLAYING;
        countWhilePlaying();
    }



    private void countWhilePlaying(){
        gotMoreTime=false;
        whilePlaying= new CountDownTimer(timeLeftToFinish, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftToFinish=millisUntilFinished;
                changeTimer();

            }

            @Override
            public void onFinish() {
                timeLeftToFinish=0;
                timer.setText("00:00");
                fields.get(part-1).setBackgroundResource(R.drawable.red_per_set);
                //    if (gotHelp==true) {
                gridTextView.setAdapter(null);
                showAnswer();
                seekBar.setProgress(0);
                restart = new Runnable() {
                    @Override
                    public void run() {
                        playAgain();
                    }
                };
                waitForColoring.postDelayed(restart, 2000);
            }
            //    }
        }.start();

    }

    private void changeTimer(){

        int minutes = (int) (timeLeftToFinish/ 1000) / 60;
        int seconds = (int) (timeLeftToFinish / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timer.setText(timeFormatted);
        if (timeLeftToFinish <= 8000) {
            timer.setTextColor(Color.RED);
            timerhelp.setEnabled(true);
            timerhelp.setVisibility(View.VISIBLE);
        }
    }

    private void changeText(){

        int seconds = (int) (timeLeftToStart / 1000) % 60;
        //   String timeFormatted = String.format(Locale.getDefault(),"%02d",seconds);
        beforeGame.setText(seconds+"");
    }

    private void displayAnswerFields(){
        for (int i = 0; i <withoutSpaces.length ; i++) {
            answerField = new TextView(this);
            answerField.setRotationY(180);
            answerField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            answerField.setTextColor(Color.YELLOW);
            answerField.setBackgroundResource(R.drawable.textview);
            answerField.setGravity(Gravity.CENTER);
            answerField.setId(i);
            answerField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedView=(TextView)v;
                    undoAnswer=true;
                    numOfChangableViews++;
                    for (int i = 0; i <withoutSpaces.length ; i++) {
                        if (mButtons.get(i).getText().equals(selectedView.getText()) &&
                                mButtons.get(i).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.select).getConstantState())){
                            selectedStates[i]=0;
                            selectedView.setText("");
                            answerTextViews.set(selectedView.getId(),"");
                            mButtons.get(i).setBackgroundResource(R.drawable.words);
                            selections.remove(mButtons.get(i));
                            break;
                        }
                    }
                }
            });
            showableTextviews.add(answerField);
        }
        gridTextView = (GridView) findViewById(R.id.ansgrid);
        gridTextView.setAdapter(new AnswerGrid(showableTextviews));
        gridTextView.setRotationY(180);
    }

    private void displayWords(){

        mButtons=new ArrayList<>();
        originalText=gameDbHelper.giveTheLyric(music.randomRow,genre);
        correctText = originalText.split(" ");
        withoutSpaces = originalText.split(" ");

        //shuffle sentence
        Collections.shuffle(Arrays.asList(withoutSpaces));

        for (int i = 0; i < withoutSpaces.length; i++) {
            cb = new Button(this);
            cb.setText(withoutSpaces[i]);
            cb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            cb.setBackgroundResource(R.drawable.words);
            cb.setId(i);
            selectedStates=new int[withoutSpaces.length];
            selectedStates[i]=0;
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //see which button was clicked
                    selection = (Button)v;
                    //add the selected button into the array and store all selected buttons
                    selections.add(selection);
                    if (undoAnswer==false){
                        countSize++;
                    }

                    int index=selection.getId();
                    answerCycle(index);
                }
            });
            mButtons.add(cb);
        }
        //make gridview and show buttons
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new ButtonGrid(mButtons));
    }

    private void showCorrectSelections(){
        for (int i = 0; i <withoutSpaces.length ; i++) {
            if (!showableTextviews.get(i).getText().equals("")) {
                if (showableTextviews.get(i).getText().equals(correctText[i])) {
                    showableTextviews.get(i).setBackgroundResource(R.drawable.correcttext);
                } else {
                    showableTextviews.get(i).setBackgroundResource(R.drawable.wrongtext);
                }
            }

        }
    }


    private void answerCycle(int index){
        //  int index=selection.getId();
        //   Toast.makeText(LyricsJumbleActivity.this,String.valueOf(index),Toast.LENGTH_SHORT).show();

        if (selectedStates[index]==0){
            selectedStates[index]=1;
            for (int i = 0; i <countSize ; i++) {
                if (undoAnswer==true && gotHelp==true){
                    numOfChangableViews--;
                    if (numOfChangableViews==0){
                        undoAnswer=false;
                    }
                    answerTextViews.set(i,(String) mButtons.get(index).getText());
                    showableTextviews.get(number).setText(answerTextViews.get(i));
                    //   Toast.makeText(LyricsJumbleActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
                    mButtons.get(index).setBackgroundResource(R.drawable.select);
                    gotHelp=false;
                    break;
                }
                else if (undoAnswer == true && answerTextViews.get(i) == "") {
                    numOfChangableViews--;
                    if (numOfChangableViews==0){
                        undoAnswer=false;
                    }
                    answerTextViews.set(i,(String)selection.getText());
                    showableTextviews.get(i).setText(answerTextViews.get(i));
                    selection.setBackgroundResource(R.drawable.select);
                    break;
                }
//                else if (gotHelp==true && !showableTextviews.get(number).getText().equals("")){
//                    Toast.makeText(LyricsJumbleActivity.this,"bb",Toast.LENGTH_SHORT).show();
//                    if (mButtons.get(i).getText().equals(showableTextviews.get(number).getText())) {
//                        Toast.makeText(LyricsJumbleActivity.this,"nn",Toast.LENGTH_SHORT).show();
//                        selectedStates[i] = 0;
//                        mButtons.get(i).setBackgroundResource(R.drawable.words);
//                        selections.remove(mButtons.get(i));
//                        answerTextViews.set(number,(String) mButtons.get(index).getText());
//                        showableTextviews.get(number).setText(answerTextViews.get(number));
//                        //   Toast.makeText(LyricsJumbleActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
//                        mButtons.get(index).setBackgroundResource(R.drawable.select);
//                        gotHelp=false;
//
//                        break;
//                    }
//                }
                else if (showableTextviews.get(i).getText() == "") {
                    if (gotHelp==true){
                        answerTextViews.add(i,(String) mButtons.get(index).getText());
                        showableTextviews.get(number).setText(answerTextViews.get(i));
                        //   Toast.makeText(LyricsJumbleActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
                        mButtons.get(index).setBackgroundResource(R.drawable.select);
                        gotHelp=false;

                        break;
                    }
                    else {
                        answerTextViews.add(i, (String) selection.getText());
                        showableTextviews.get(i).setText(answerTextViews.get(i));
                        selection.setBackgroundResource(R.drawable.select);
                        if (answerTextViews.size() == correctText.length) {
                            gridTextView.setAdapter(null);
                            if (checkAnswerState()) {
                                displayWinnerState();
                                break;
                            } else {
                                displayLooserState();
                                break;
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }
        else if (selectedStates[index]==1){

            for (int i = 0; i <countSize ; i++) {
                if (gotHelp==true && !showableTextviews.get(number).getText().equals("")){
                    if (mButtons.get(i).getText().equals(showableTextviews.get(number).getText())) {
                        //
                        // Toast.makeText(LyricsJumbleActivity.this,"nn",Toast.LENGTH_SHORT).show();
                        selectedStates[i] = 0;
                        mButtons.get(i).setBackgroundResource(R.drawable.words);
                        selections.remove(mButtons.get(i));
                        answerTextViews.set(number,(String) mButtons.get(index).getText());
                        showableTextviews.get(number).setText(answerTextViews.get(number));
                        //   Toast.makeText(LyricsJumbleActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
                        mButtons.get(index).setBackgroundResource(R.drawable.select);
                        gotHelp=false;

                        break;
                    }
                }
            }
        }
    }


//    private void answerCycle(int index){
//        //  int index=selection.getId();
//       //    Toast.makeText(LyricsJumbleActivity.this,String.valueOf(index),Toast.LENGTH_SHORT).show();
//
//        if (selectedStates[index]==0){
//            selectedStates[index]=1;
//            for (int i = 0; i <countSize ; i++) {
//                if (undoAnswer==true && gotHelp){
//                    numOfChangableViews--;
//                    if (numOfChangableViews==0){
//                        undoAnswer=false;
//                    }
//                    answerTextViews.set(i,(String) mButtons.get(index).getText());
//                    showableTextviews.get(number).setText(answerTextViews.get(i));
//                    //   Toast.makeText(LyricsJumbleActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
//                    mButtons.get(index).setBackgroundResource(R.drawable.select);
//                    gotHelp=false;
//                    break;
//                }
//                else if (undoAnswer == true && answerTextViews.get(i) == "") {
//                    numOfChangableViews--;
//                    if (numOfChangableViews==0){
//                        undoAnswer=false;
//                    }
//                    answerTextViews.set(i,(String)selection.getText());
//                    showableTextviews.get(i).setText(answerTextViews.get(i));
//                    selection.setBackgroundResource(R.drawable.select);
//                    break;
//                }
//                else if (showableTextviews.get(i).getText() == "") {
//                  //  Toast.makeText(LyricsJumbleActivity.this,"showable",Toast.LENGTH_SHORT).show();
//                    if (gotHelp){
//                        if (correctText[number].equals(showableTextviews.get(i).getText())) {
////                            for (int j=0;j<mButtons.size();j++) {
////                                if (mButtons.get(j).getText().equals(showableTextviews.get(i).getText())) {
//                                 //   mButtons.get(i).setBackgroundResource(R.drawable.words);
//                                 //   selections.remove(mButtons.get(i));
//                                    answerTextViews.add(i, (String) mButtons.get(index).getText());
//                                    showableTextviews.get(i).setText("");
//                                    //     Toast.makeText(LyricsJumbleActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
//                                    mButtons.get(index).setBackgroundResource(R.drawable.select);
//                                    gotHelp = false;
////                                }
////                                break;
//                          //  }
//                            break;
//                        }
//                    }
//                    else {
//                        answerTextViews.add(i, (String) selection.getText());
//                        showableTextviews.get(i).setText(answerTextViews.get(i));
//                        selection.setBackgroundResource(R.drawable.select);
//                        if (answerTextViews.size() == correctText.length) {
//                            gridTextView.setAdapter(null);
//                            if (checkAnswerState()) {
//                                displayWinnerState();
//                                break;
//                            } else {
//                                displayLooserState();
//                                break;
//                            }
//                        }
//                        else {
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        else if (selectedStates[index]==1){
//
//
//            for (int i = 0; i <countSize ; i++) {
//                if (gotHelp && !showableTextviews.get(number).getText().equals("")){
//                    Toast.makeText(LyricsJumbleActivity.this,mButtons.get(i).getText(),Toast.LENGTH_SHORT).show();
//                    if (mButtons.get(i).getText().equals(showableTextviews.get(number).getText())) {
//                        for (int j = 0; j <mButtons.size() ; j++) {
//                            if (showableTextviews.get(j).getText().equals(mButtons.get(i).getText())){
//                                showableTextviews.get(j).setText("");
//                            }
//                        }
//
//                        Toast.makeText(LyricsJumbleActivity.this,"sec",Toast.LENGTH_SHORT).show();
//                        selectedStates[i] = 0;
//                        mButtons.get(i).setBackgroundResource(R.drawable.words);
//                        selections.remove(mButtons.get(i));
//                        answerTextViews.set(number,(String) mButtons.get(index).getText());
//                        showableTextviews.get(number).setText(answerTextViews.get(number));
//                        Toast.makeText(LyricsJumbleActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
//                        mButtons.get(index).setBackgroundResource(R.drawable.select);
//                        gotHelp=false;
//
//                        break;
//                    }
//                }
//            }
//        }
//    }



    private boolean checkAnswerState(){
        if (Arrays.equals(answerTextViews.toArray(),correctText))
            return true;
        else
            return false;
    }

    private void showAnswer(){
        correctAnswer.setText(originalText);
        correctAnswer.setTextColor(Color.GREEN);
    }

    private void displayLooserState(){
        for (Button s:selections) {
            s.setBackgroundResource(R.drawable.finish);
            showAnswer();
        }
        //
        fields.get(part-1).setBackgroundResource(R.drawable.red_per_set);
        whilePlaying.cancel();
        music.checkForFreeze();
        restart=new Runnable() {
            @Override
            public void run() {
                playAgain();
            }
        };
        waitForColoring.postDelayed(restart,2000);
    }

    private void displayWinnerState(){
        for (Button s:selections) {
            s.setBackgroundResource(R.drawable.win);
            showAnswer();
        }
        fields.get(part-1).setBackgroundResource(R.drawable.green_per_set);
        LyricsJumbleScore+=5;
        sendScoreToMenu();
        StartingScreenActivity.coin++;
    //    textViewCoin.setText(StartingScreenActivity.coin + "");
        sendCoinsToMenu();
        score.setText(""+LyricsJumbleScore);

        whilePlaying.cancel();
        music.checkForFreeze();
        restart=new Runnable() {
            @Override
            public void run() {
                playAgain();
            }
        };
        waitForColoring.postDelayed(restart,2000);
    }

    private void playAgain(){
        //reset everything

        if (part == 3) {
            if(LyricsJumbleScore >StartingScreenActivity.JumbleLyricHighscore) {
                RoundActivity.newHighScore = true;
                RoundActivity.highScr = LyricsJumbleScore;
                sendScoreToMenu();

            }
            else {
                RoundActivity.highScr=StartingScreenActivity.JumbleLyricHighscore;
            }
            part = 1;
            RoundActivity.type = 3;
            RoundActivity.currentScore = LyricsJumbleScore;
          //  Toast.makeText(LyricsJumbleActivity.this,String.valueOf(LyricsJumbleScore),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LyricsJumbleActivity.this, RoundActivity.class);
            startActivity(intent);
            finish();
        } else {
            music.checkForFreeze();
            mButtons.clear();
            showableTextviews.clear();
            gridView.setAdapter(null);
            answerTextViews.clear();
            countSize=0;
            numOfChangableViews=0;
            correctAnswer.setText(null);
            count=0;
            number=0;
            gotHelp=false;
            undoAnswer=false;
            gotMoreTime=false;
            timeLeftToFinish=COUNTDOWN_WHILE_PLAYING;
            timer.setText("00:15");
            timer.setTextColor(textColorDefaultCD);
            timerhelp.setEnabled(false);
            help.setEnabled(false);
            play.setEnabled(false);
            stop.setEnabled(false);
            replay.setEnabled(false);
            timerhelp.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            replay.setVisibility(View.GONE);
            startGame();
            part++;
        }

    }

    private void sendScoreToMenu() {
        Intent resultIntent = new Intent(LyricsJumbleActivity.this,StartingScreenActivity.class);
        resultIntent.putExtra(EXTRA_SCORE1, LyricsJumbleScore);
        setResult(RESULT_OK, resultIntent);
    }


    public void giveMeMoreTime(View view){
        gotMoreTime=true;
        if (StartingScreenActivity.coin>=3) {
            StartingScreenActivity.coin -= 3;
            sendCoinsToMenu();
            whilePlaying.cancel();
            timeLeftToFinish = COUNTDOWN_WHILE_PLAYING;
            timer.setTextColor(textColorDefaultCD);
            countWhilePlaying();
        }

        else {
            Toast.makeText(LyricsJumbleActivity.this,"سکه هات کافی نیس :)",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (StartingScreenActivity.coin>=3) {
//            StartingScreenActivity.coin -= 3;
            int matchNumberWithButtons = 0;
            if (requestCode == REQUEST_NUMBER) {
                if (resultCode == LyricsHelpMenu.RESULT_OK) {
                    number = data.getIntExtra(LyricsHelpMenu.NUMBER, 0);
                    countSize++;
                    gotHelp=true;
                    for (int i = 0; i < withoutSpaces.length; i++) {
                        if (mButtons.get(i).getText().equals(correctText[number])) {
                            selections.add(mButtons.get(i));
                            matchNumberWithButtons = i;
                            break;
                        }
                    }
                    answerCycle(matchNumberWithButtons);
                }
          //  }
        }
    }

    private void sendCoinsToMenu(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE11, StartingScreenActivity.coin);
        setResult(RESULT_OK, resultIntent);
    }


    public void helpMe(View view){
        PopupMenu popup = new PopupMenu(LyricsJumbleActivity.this, help);
        popup.getMenuInflater().inflate(R.menu.help_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.one:
                        Intent intent = new Intent(LyricsJumbleActivity.this, LyricsHelpMenu.class);
                        intent.putExtra("numberOfWords", withoutSpaces.length);
                        startActivityForResult(intent, REQUEST_NUMBER);
                        break;
                    case R.id.trueorfalse:
                        gotHelp=true;
                        showCorrectSelections();
                        break;
                    case R.id.two:
                     //   gotHelp=true;
                        whilePlaying.cancel();
                        playAgain();
                        break;
                }
                return true;
            }
        });
        popup.show();

    }


    public void onDestroy(){
        super.onDestroy();
        music.checkForDestroy();
        if (whilePlaying !=null){
            whilePlaying.cancel();
        }
//        if (beforeStarting != null) {
//            beforeStarting.cancel();
//        }

    }

}