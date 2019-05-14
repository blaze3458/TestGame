package com.xgames.testgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    Button firstButton,lastButton;
    private RelativeLayout touchview;
    TextView tvTime;
    ListView wordList;

    private static final int CHOOSECOLOR = 0xFFB6FCD5;
    private static final int FIRSTCOLOR = 0xFFDFD730;

    private static final String START = "START";
    private static final String STOP = "STOP";

    private Boolean firstClick = false,lastClick = false;

    int arr_x = 15;//4
    int arr_y = 15;//4

    Button[][] arr_btn = new Button[arr_y][arr_x];//4,4
    Boolean[][] check_arr = new Boolean[arr_y][arr_x];
    String[][] letter_arr = new String[arr_y][arr_x];

    Boolean[] checkWord;

    String gameStatus;

    int first_x,first_y,last_x,last_y;
    int wordCounter;
    int wordTotal;

    MyThread thread;
    TableAdapter adapter;
    Settings settings;
    HighScores easy,med,hard;

    private ArrayList<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchview = findViewById(R.id.relativeLayout);
        initializeIntent();
        initializeSettings();
        initializeElements();
        init();
    }

    @Override
    public void onBackPressed(){
        onExitGame();
    }

    private void initializeIntent(){

        words = (ArrayList<String>) getIntent().getSerializableExtra("KeyWords");

        Object[] obj = (Object[])getIntent().getSerializableExtra("CrossWord");
        String[][] arr = new String[15][15];
        if(obj != null){
            for (int i = 0; i < obj.length; i++) {
                String[] object = (String[])obj[i];
                for(int k = 0; k < object.length; k++)
                    arr[i][k] = object[k];
            }
        }
        letter_arr = arr;
        gameStatus = STOP;
    }

    private void initializeSettings(){
        settings = new Settings(this);
        easy = new HighScores("HighScoreEasy",this);
        med = new HighScores("HighScoreMedium",this);
        hard = new HighScores("HighScoreHard",this);

        if(settings.getEasy())
            wordTotal = 3;
        else if(settings.getMedium())
            wordTotal = 6;
        else if(settings.getHard())
            wordTotal = 9;
    }

    private void init(){
        int k = -1; // y
        for(int i = 0; i< touchview.getChildCount(); i++){
            int j = i % arr_x; // x
            if(i % arr_x == 0) {
                k++;
            }
            View v = touchview.getChildAt(i);
            if(v instanceof Button){
                String text = letter_arr[k][j];
                ClickListener cl = new ClickListener(this,(Button)v,j,k);
                v.setOnClickListener(cl);
                ((Button) v).setText(text);
                arr_btn[k][j] = (Button)v;

                check_arr[k][j] = false;
            }
        }

        checkWord = new Boolean[wordTotal];

        for(int i = 0; i<wordTotal; i++)
            checkWord[i] = false;

        adapter = new TableAdapter(words,checkWord);
        wordList.setAdapter(adapter);
        startGame();
    }

    private void initializeElements(){
        tvTime = findViewById(R.id.time);
        wordList = findViewById(R.id.word_list);
    }

    private void startGame(){
        thread = new MyThread();
        wordCounter = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                thread.start();
            }
        }, 1500);
    }

    private void stopGame(){
        gameStatus = STOP;
        thread.interrupt();
        finishGameMessage();
        //Skor için isim
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
    }


    public class ClickListener implements View.OnClickListener {
        Button btn;
        int x,y;

        public ClickListener(Context cont,Button btn,int x,int y){
            this.btn = btn;
            this.x = x;
            this.y = y;
        }

        @Override
        public void onClick(View v) {
            if(!firstClick){
                firstClick = true;
                firstButton = this.btn;
                firstButton.setBackgroundColor(FIRSTCOLOR);
                first_x = this.x;
                first_y = this.y;
            }

            else if(!lastClick) {
                lastClick = true;
                lastButton = this.btn;
                last_x = this.x;
                last_y = this.y;
            }
            //Toast.makeText(MainActivity.this,"Click=> "+x+" : "+y+" First : "+firstClick+" Last : "+lastClick,Toast.LENGTH_SHORT).show();

            if(firstClick && lastClick){
                //kontrol
                checkButtons();
            }
        }
    }

    private void checkButtons() {
        firstClick = false;
        lastClick = false;

        if (firstButton == lastButton){
            if(check_arr[first_y][first_x])
                firstButton.setBackgroundColor(CHOOSECOLOR);
            else
                firstButton.setBackgroundColor(Color.WHITE);

            return;
        }

        firstButton.setBackgroundColor(Color.WHITE);

        //Dikey
        if (Vertical()) {
            //Toast.makeText(this, "Dikey", Toast.LENGTH_SHORT).show();
            markButtonsVertical();
        }
        //Yatay
        else if (Horizontal()) {
            //Toast.makeText(this, "Yatay", Toast.LENGTH_SHORT).show();
            markButtonsHorizontal();
        }
        //ÇaprazSolÜst
        else if (CrossLeftUp()) {
            //Toast.makeText(this, "Sol-Çapraz-Üst", Toast.LENGTH_SHORT).show();
            markButtonsCrossLeftUp();
        }
        //ÇaprazSolAlt
        else if (CrossLeftDown()) {
            //Toast.makeText(this, "Sol-Çapraz-Alt", Toast.LENGTH_SHORT).show();
            markButtonsCrossLeftDown();
        }
        //ÇaprazSağÜst
        else if (CrossRightUp()) {
            //Toast.makeText(this, "Sağ-Çapraz-Üst", Toast.LENGTH_SHORT).show();
            markButtonsCrossRightUp();
        }
        //ÇaprazSağAlt
        else if (CrossRightDown()) {
            //Toast.makeText(this, "Sağ-Çapraz-Alt", Toast.LENGTH_SHORT).show();
            markButtonsCrossRightDown();
        }
        else{
            firstButton.setBackgroundColor(Color.WHITE);
        }
    }

    private boolean Vertical(){
        if(first_x == last_x){
            return true;
        }
        return false;
    }

    private boolean Horizontal(){
        if(first_y == last_y){
            return true;
        }
        return false;
    }

    private boolean CrossLeftUp(){
        //Yukarı sol
        for(int i = 0; i<arr_y; i++){
            int temp_y = first_y - i;
            int temp_x = first_x - i;
            if((last_y == temp_y) && (last_x == temp_x) )
                return true;
        }
        return false;
    }

    private boolean CrossLeftDown(){
        //Aşağı sol
        for(int i = 0; i<arr_y; i++){
                int temp_y = first_y + i;
                int temp_x = first_x - i;//
                if((last_y == temp_y) && (last_x == temp_x) )
                    return true;
        }
        return false;
    }

    private boolean CrossRightUp(){
        //Yukarı sağ
        for(int i = 0; i<arr_y; i++){
            int temp_y = first_y - i;
            int temp_x = first_x + i;
            if((last_y == temp_y) && (last_x == temp_x) )
                return true;
        }
        return false;
    }

    private boolean CrossRightDown(){
        //Aşağı sağ
        for(int i = 0; i<arr_y; i++){
            int temp_y = first_y + i;
            int temp_x = first_x + i;//
            if((last_y == temp_y) && (last_x == temp_x) )
                return true;
        }
        return false;
    }

    public void markButtonsVertical(){
        String selectedWord = "";
        for(int i = 0; i < arr_y; i++){
            if((i >= first_y && i <=last_y) || (i>= last_y && i <= first_y )){
                Button btn = arr_btn[i][first_x];
                selectedWord += btn.getText().toString();
            }
        }
        String RselectedWord =  new StringBuilder(selectedWord).reverse().toString();

        if(checkWord(selectedWord,RselectedWord)) {
            for (int i = 0; i < arr_y; i++) {
                if ((i >= first_y && i <= last_y) || (i >= last_y && i <= first_y)) {
                    Button btn = arr_btn[i][first_x];
                    btn.setBackgroundColor(CHOOSECOLOR);
                    selectedWord += btn.getText().toString();
                    check_arr[i][first_x] = true;
                }
            }
        }

    }

    public void markButtonsHorizontal(){
        String selectedWord = "";
        for(int i = 0; i < arr_x; i++){
            if((i >= first_x && i <=last_x) || (i>= last_x && i <= first_x )){
                Button btn = arr_btn[first_y][i];
                selectedWord += btn.getText().toString();
            }
        }
        String RselectedWord =  new StringBuilder(selectedWord).reverse().toString();
        if(checkWord(selectedWord,RselectedWord)){
            for(int i = 0; i < arr_x; i++){
                if((i >= first_x && i <=last_x) || (i>= last_x && i <= first_x )){
                    Button btn = arr_btn[first_y][i];
                    btn.setBackgroundColor(CHOOSECOLOR);
                    check_arr[first_y][i] = true;
                }
            }
        }
    }

    public void markButtonsCrossLeftUp(){
        String selectedWord = "";
        for(int i = 0; i<arr_y; i++){
            int temp_y = first_y - i;
            int temp_x = first_x - i;

            Button btn = arr_btn[temp_y][temp_x];
            selectedWord += btn.getText().toString();
            if((last_y == temp_y) && (last_x == temp_x))
                break;
        }
        String RselectedWord =  new StringBuilder(selectedWord).reverse().toString();
        if(checkWord(selectedWord,RselectedWord)){
            for(int i = 0; i<arr_y; i++){
                int temp_y = first_y - i;
                int temp_x = first_x - i;

                Button btn = arr_btn[temp_y][temp_x];
                btn.setBackgroundColor(CHOOSECOLOR);
                check_arr[first_y][i] = true;
                if((last_y == temp_y) && (last_x == temp_x))
                    break;
            }
        }
    }

    public void markButtonsCrossLeftDown(){
        String selectedWord = "";
        for(int i = 0; i<arr_y; i++){
            int temp_y = first_y + i;
            int temp_x = first_x - i;//

            Button btn = arr_btn[temp_y][temp_x];
            selectedWord += btn.getText().toString();
            if((last_y == temp_y) && (last_x == temp_x))
                break;
        }
        String RselectedWord =  new StringBuilder(selectedWord).reverse().toString();
        if(checkWord(selectedWord,RselectedWord)){
            for(int i = 0; i<arr_y; i++){
                int temp_y = first_y + i;
                int temp_x = first_x - i;//

                Button btn = arr_btn[temp_y][temp_x];
                btn.setBackgroundColor(CHOOSECOLOR);
                check_arr[first_y][i] = true;
                if((last_y == temp_y) && (last_x == temp_x))
                    break;
            }
        }
    }

    public void markButtonsCrossRightUp(){
        String selectedWord = "";
        for(int i = 0; i<arr_y; i++){
            int temp_y = first_y - i;
            int temp_x = first_x + i;

            Button btn = arr_btn[temp_y][temp_x];
            selectedWord += btn.getText().toString();
            if((last_y == temp_y) && (last_x == temp_x))
                break;
        }
        String RselectedWord =  new StringBuilder(selectedWord).reverse().toString();
        if(checkWord(selectedWord,RselectedWord)){
            for(int i = 0; i<arr_y; i++){
                int temp_y = first_y - i;
                int temp_x = first_x + i;

                Button btn = arr_btn[temp_y][temp_x];
                btn.setBackgroundColor(CHOOSECOLOR);
                check_arr[first_y][i] = true;
                if((last_y == temp_y) && (last_x == temp_x))
                    break;
            }
        }
    }

    public void markButtonsCrossRightDown(){
        String selectedWord = "";
        for(int i = 0; i<arr_y; i++){
            int temp_y = first_y + i;
            int temp_x = first_x + i;//

            Button btn = arr_btn[temp_y][temp_x];
            selectedWord += btn.getText().toString();
            if((last_y == temp_y) && (last_x == temp_x))
                break;
        }
        String RselectedWord =  new StringBuilder(selectedWord).reverse().toString();
        if(checkWord(selectedWord,RselectedWord)){
            for(int i = 0; i<arr_y; i++){
                int temp_y = first_y + i;
                int temp_x = first_x + i;//

                Button btn = arr_btn[temp_y][temp_x];
                btn.setBackgroundColor(CHOOSECOLOR);
                check_arr[first_y][i] = true;
                if((last_y == temp_y) && (last_x == temp_x))
                    break;
            }
        }
    }

    private boolean checkWord(String selected,String Rselected){
        if(words.contains(selected)) {
            strikeWord(selected);
            return true;
        }
        else if(words.contains(Rselected)){
            strikeWord(Rselected);
            return true;
        }

        return false;
    }

    private void strikeWord(String word){
        wordCounter++;
        int i = words.indexOf(word);
        checkWord[i] = true;
        adapter = new TableAdapter(words,checkWord);
        wordList.setAdapter(adapter);
        Toast.makeText(this,word,Toast.LENGTH_SHORT).show();

        if(wordCounter == wordTotal){
            stopGame();
        }
    }

    public void finishGameMessage(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Süreniz : "+thread.time);
        alert.setPositiveButton("Ana Menü", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        if((settings.getEasy() && easy.getScore(9) > thread.time) || (settings.getMedium() && med.getScore(9) > thread.time) ||
                (settings.getHard() && hard.getScore(9) > thread.time)){
            alert.setNegativeButton("Skoru Kaydet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    insertScore(thread.time);
                }
            });
        }

        alert.setCancelable(false);
        alert.show();
    }

    private void insertScore(final int score){

        View v = getLayoutInflater().inflate(R.layout.username_input,null);
        final EditText input = v.findViewById(R.id.userName);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Enter a username");
        alertDialog.setView(v);
        alertDialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userName = input.getText().toString();
                if(settings.getEasy())
                    easy.insertNewScore(userName,score);
                else if(settings.getMedium())
                    med.insertNewScore(userName,score);
                else if(settings.getHard())
                    hard.insertNewScore(userName,score);

                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void onExitGame(){
        AlertDialog.Builder alert =  new AlertDialog.Builder(this);
        alert.setMessage("Oyundan çıkmak istiyor musunuz?");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }


    private class MyThread extends Thread{
        public int time;
        public MyThread(){
            time = 0;
        }

        @Override
        public void run(){
            gameStatus = START;
            while(gameStatus.equals(START)){
                time++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(String.valueOf(time));
                    }
                });

                try{
                    Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private class TableAdapter extends BaseAdapter{

        ArrayList<String> list;
        Boolean[] checkWord;
        public TableAdapter(ArrayList<String> list,Boolean[] checkWord){
            this.list = list;
            this.checkWord = checkWord;
        }

        @Override
        public int getCount() {
            return list.size() / 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.word_element,null);

            TextView tv1,tv2,tv3;

            tv1 = v.findViewById(R.id.word1);
            tv2 = v.findViewById(R.id.word2);
            tv3 = v.findViewById(R.id.word3);

            String t = list.get(3*position);
            String t2 = list.get(3*position+1);
            String t3 = list.get(3*position+2);
            Boolean b1,b2,b3;

            b1 = checkWord[3*position];
            b2 = checkWord[3*position+1];
            b3 = checkWord[3*position+2];

            tv1.setText(t);
            tv2.setText(t2);
            tv3.setText(t3);

            if(b1 || b2 || b3){
                if(b1)
                    tv1.setPaintFlags(tv1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                if(b2)
                    tv2.setPaintFlags(tv2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                if(b3)
                    tv3.setPaintFlags(tv3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            return v;
        }
    }
}
