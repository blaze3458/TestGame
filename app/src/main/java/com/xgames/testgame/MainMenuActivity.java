package com.xgames.testgame;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String HORIZONTAL = "HORIZONTAL";
    private static final String VERTICAL = "VERTICAL";
    private static final String CROSSLEFTTUP = "CROSSLEFTUP";
    private static final String CROSSLEFTDOWN = "CROSSLEFTDOWN";
    private static final String CROSSRIGHTUP = "CROSSRIGHTUP";
    private static final String CROSSRIGHTDOWN = "CROSSRIGHTDOWN";

    private ProgressDialog progressDialog;

    String[][] LetterArray;
    Boolean[][] wordControl;

    ArrayList<String> directions;
    ArrayList<String> addedWord;

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findViewById(R.id.startGame).setOnClickListener(this);
        findViewById(R.id.settingsButton).setOnClickListener(this);
        findViewById(R.id.highScoresButton).setOnClickListener(this);
        findViewById(R.id.exitGame).setOnClickListener(this);

        initSettings();
    }

    @Override
    public void onBackPressed(){
        onExitGame();
    }

    private void initSettings(){
        directions = new ArrayList<>();
        addedWord = new ArrayList();
        settings = new Settings(this);
        if(settings.getHorizontal())
            directions.add(HORIZONTAL);

        if(settings.getVertical())
            directions.add(VERTICAL);

        if(settings.getCross()) {
            directions.add(CROSSLEFTTUP);
            directions.add(CROSSLEFTDOWN);
            directions.add(CROSSRIGHTUP);
            directions.add(CROSSRIGHTDOWN);
        }
        LetterArray = new String[15][15];
        wordControl = new Boolean[15][15];
    }

    @Override
    protected void onResume(){
        super.onResume();
        initSettings();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.startGame){
            initializeSingleGame();
        }
        else if(i == R.id.settingsButton){
            startActivity(new Intent(this,SettingsActivity.class));
        }
        else if(i == R.id.highScoresButton){
            startActivity(new Intent(this,HighscoreActivity.class));
        }
        else if(i == R.id.exitGame){
            onExitGame();
        }
    }

    private void initializeSingleGame(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MyThread t = new MyThread();
        t.start();
    }

    public void startGame(){
        progressDialog.dismiss();

        Intent i = new Intent(MainMenuActivity.this,MainActivity.class);
        i.putExtra("CrossWord",LetterArray);
        i.putExtra("KeyWords",addedWord);
        startActivity(i);
    }

    private void onExitGame(){
        AlertDialog.Builder alert =  new AlertDialog.Builder(this);
        alert.setMessage("Oyundan çıkmak istiyor musunuz?");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exitGame();
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void exitGame(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class MyThread extends Thread{
        String[] letter = {"A","B","C","Ç","D","E","F","G","Ğ","H","I","İ","J","K","L","M","N","O","Ö",
                "P","R","S","Ş","T","U","Ü","V","Y","Z"};
        String[] vocabulary = {"GARİPSEMEK","UĞRAŞMAK","AYDINLANMAK","KAZANMAK",
                "TAKILMAK","TAKOMETRE","GELMEK","GEÇMEK","ALINGANLIK"};

        int v_size = vocabulary.length;
        int l_size = letter.length;
        int d_size;

        int selected;
        public MyThread(){
            initializeLetterArray();
            selected = 0;
            d_size = directions.size();
            if(addedWord.size() > 0)
                addedWord.clear();
        }

        private void initializeLetterArray(){
            for(int i = 0; i<15; i++){
                for(int k = 0; k<15; k++){
                    String l = letter[getRandom(1,l_size)];
                    LetterArray[i][k] = l;
                    wordControl[i][k] = false;
                }
            }
        }

        @Override
        public void run(){
            int limit = 0;
            if(settings.getEasy())
                limit = 3;
            else if(settings.getMedium())
                limit = 6;
            else if(settings.getHard())
                limit = 9;

            while(selected != limit){
                Boolean injected = false;
                String word = vocabulary[getRandom(0,v_size)];
                String direct = directions.get(getRandom(0,d_size));
                int trySet = 0;

                while(addedWord.contains(word)) {
                    word = vocabulary[getRandom(0,v_size)];
                }

                word = word.toUpperCase();
                while(!injected){
                    int index_x = getRandom(0,15);
                    int index_y = getRandom(0,15);
                    int word_l = word.length();
                    char[] wChar = word.toCharArray();

                    if(trySet > 5000)
                        direct = VERTICAL;

                    if(direct.equals(VERTICAL)) {
                        if(!CheckEmptyCellVertical(index_x,index_y,word_l))
                            continue;

                        for(int k = 0; k < word_l; k++) {
                            LetterArray[index_y+k][index_x] = String.valueOf(wChar[k]);
                            wordControl[index_y+k][index_x] = true;
                        }
                    }
                    else if(direct.equals(HORIZONTAL)) {
                        if(!CheckEmptyCellHorizontal(index_x,index_y,word_l))
                            continue;

                        for(int k = 0; k < word_l; k++) {
                            LetterArray[index_y][index_x+k] = String.valueOf(wChar[k]);
                            wordControl[index_y][index_x+k] = true;
                        }
                    }
                    else if(direct.equals(CROSSLEFTTUP)){
                        if(!CheckEmptyCellCrossLeftUp(index_x,index_y,word_l)) {
                            trySet++;
                            continue;
                        }

                        for(int k = 0; k < word_l; k++) {
                            LetterArray[index_y-k][index_x-k] = String.valueOf(wChar[k]);
                            wordControl[index_y-k][index_x-k] = true;
                        }
                    }
                    else if(direct.equals(CROSSLEFTDOWN)) {
                        if(!CheckEmptyCellCrossLeftDown(index_x,index_y,word_l)) {
                            trySet++;
                            continue;
                        }
                        for(int k = 0; k < word_l; k++) {
                            LetterArray[index_y+k][index_x-k] = String.valueOf(wChar[k]);
                            wordControl[index_y+k][index_x-k] = true;
                        }
                    }
                    else if(direct.equals(CROSSRIGHTUP)) {
                        if(!CheckEmptyCellCrossRightUp(index_x,index_y,word_l)) {
                            trySet++;
                            continue;
                        }

                        for(int k = 0; k < word_l; k++) {
                            LetterArray[index_y-k][index_x+k] = String.valueOf(wChar[k]);
                            wordControl[index_y-k][index_x+k] = true;
                        }
                    }
                    else if(direct.equals(CROSSRIGHTDOWN)) {
                        if (!CheckEmptyCellCrossRightDown(index_x, index_y, word_l)){
                            trySet++;
                            continue;
                        }

                        for (int k = 0; k < word_l; k++) {
                            LetterArray[index_y + k][index_x + k] = String.valueOf(wChar[k]);
                            wordControl[index_y + k][index_x + k] = true;
                        }
                    }
                    injected = true;
                    selected++;
                    addedWord.add(word);
                }//injected
            }//selected

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startGame();
                }
            });
        }

        private Boolean CheckEmptyCellVertical(int x,int y, int length) {
            if((y + length -1) >= 15)
                return false;

            for(int i = 0; i< length; i++) {
                int temp_y = y+i;
                if(wordControl[temp_y][x]) {
                    return false;
                }
            }
            return true;
        }

        private Boolean CheckEmptyCellHorizontal(int x,int y, int length) {
            if((x + length -1) >= 15)//arr_x
                return false;

            for(int i = 0; i< length; i++) {
                int temp_x = x+i;
                if(wordControl[y][temp_x]) {
                    return false;
                }
            }
            return true;
        }

        private Boolean CheckEmptyCellCrossLeftUp(int x,int y, int length) {
            if((x - length - 1) <= 0 || (y - length -1) <= 0)
                return false;

            for(int i = 0; i<length; i++) {
                int temp_x = x - i;
                int temp_y = y - i;
                if(wordControl[temp_y][temp_x])
                    return false;
            }

            return true;
        }

        private Boolean CheckEmptyCellCrossLeftDown(int x,int y, int length) {
            if((x - length - 1) <= 0 || (y + length -1) >= 15)//arr_x
                return false;

            for(int i = 0; i<length; i++) {
                int temp_x = x - i;
                int temp_y = y + i;
                if(wordControl[temp_y][temp_x])
                    return false;
            }
            return true;
        }

        private Boolean CheckEmptyCellCrossRightUp(int x,int y, int length) {
            if((x + length - 1) >= 15 || (y - length -1) <= 0)//arr_x
                return false;

            for(int i = 0; i<length; i++) {
                int temp_x = x + i;
                int temp_y = y - i;
                if(wordControl[temp_y][temp_x])
                    return false;
            }
            return true;
        }

        private Boolean CheckEmptyCellCrossRightDown(int x,int y, int length) {
            if((x + length - 1) >= 15 || (y + length -1) >= 15)//arr_x
                return false;

            for(int i = 0; i<length; i++) {
                int temp_x = x + i;
                int temp_y = y + i;
                if(wordControl[temp_y][temp_x])
                    return false;
            }
            return true;
        }

        private int getRandom(int low,int high){
            Random r = new Random();
            int result = r.nextInt(high-low) + low;

            return result;
        }
    }
}
