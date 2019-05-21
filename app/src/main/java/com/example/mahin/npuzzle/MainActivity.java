package com.example.mahin.npuzzle;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;

class puzzle {
    public static ArrayList<String> ans;
    public static int size;
    public static int intx;
    public static int inty;

    public static class coordinate {
        int x;
        int y;

        public coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static boolean is_solvable(int[][] initial_state,int[][]final_state)
    {
        HashMap<Integer,Integer> map = new HashMap<>();
        int count = 1;
        size = 3;
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                if(final_state[i][j] != 0)
                {
                    map.put((int)final_state[i][j],count);
                    count++;
                }
            }
        }
        int[] arr = new int[size*size + 1];
        int k = 0;
        for(int i=0;i<size;i++)
        {
            for(int j=0;j<size;j++)
            {
                if(initial_state[i][j] != 0)
                    arr[k++] = (int)initial_state[i][j];
            }
        }
        //System.out.println(k);
        for(int i=0;i<size*size-1;i++)
        {
            arr[i] = map.get(arr[i]);
        }
        int invcount = 0;
        for(int i=0;i<size*size-1;i++)
        {
            for(int j=i+1;j<size*size-1;j++)
            {
                if(arr[i] > arr[j])
                    invcount++;
            }
        }
        if(invcount % 2 == 0)
            return true;
        return false;
    }

    public static class next_state {
        int[][] state;
        int cost;
        coordinate pivot;
        String direction;
        int level;
        next_state parent;

        public next_state(int cost, int n, int l, int[][] temp, String move, coordinate b, next_state parent) {
            this.cost = cost;
            state = new int[n + 1][n + 1];
            this.pivot = b;
            direction = move;
            this.parent = parent;
            level = l;
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++)
                    state[i][j] = temp[i][j];
            }
        }
    }

    public static class solve_n_puzzle {

        int[][] initial_state;
        int[][] final_state;
        coordinate[] position;
        public coordinate initial_pivot;
        next_state last_state;
        BufferedReader br;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        String[] move = {"UP", "DOWN", "LEFT", "RIGHT"};


        public solve_n_puzzle(int n) {
            size = n;
            initial_pivot = new coordinate(0, 0);
            br = new BufferedReader(new InputStreamReader(System.in));
            initial_state = new int[n + 1][n + 1];
            final_state = new int[n + 1][n + 1];
            position = new coordinate[n * n + 1];
        }

        public void take_input(int[][] result, int[][] result1) {

            for (int i = 1; i <= size; i++) {

                for (int j = 1; j <= size; j++) {
                    initial_state[i][j] = result[i - 1][j - 1];
                    if (initial_state[i][j] == 0) {
                        initial_pivot = new coordinate(i, j);
                        intx = initial_pivot.x - 1;
                        inty = initial_pivot.y - 1;
                    }
                }
            }
            //System.out.println("Output : ");
            for (int i = 1; i <= size; i++) {
                //String[] str = (br.readLine()).trim().split(" ");
                for (int j = 1; j <= size; j++) {
                    final_state[i][j] = result1[i - 1][j - 1];
                    position[final_state[i][j]] = new coordinate(i, j);
                }
            }
        }

        public void print(int[][] arr) {
            for (int i = 1; i <= size; i++) {
                for (int j = 1; j <= size; j++)
                    System.out.print(arr[i][j] + " ");
                System.out.println();
            }
        }

        public int heuristic(int[][] current_state) {
            int hamming = 0, manhatton = 0;
            for (int i = 1; i <= size; i++) {
                for (int j = 1; j <= size; j++) {
                    if (current_state[i][j] != 0) {
                        if (current_state[i][j] != final_state[i][j])
                            hamming = hamming + 1;
                        int x = Math.abs(position[current_state[i][j]].x - i);
                        int y = Math.abs(position[current_state[i][j]].y - j);
                        manhatton = manhatton + x + y;
                    }
                }
            }
            return Math.max(hamming, manhatton);
        }

        public boolean isVisited(int i, String str) {
            if (str.equals("UP") && !move[i].equals("DOWN"))
                return true;
            else if (str.equals("DOWN") && !move[i].equals("UP"))
                return true;
            else if (str.equals("LEFT") && !move[i].equals("RIGHT"))
                return true;
            else if (str.equals("RIGHT") && !move[i].equals("LEFT"))
                return true;
            return false;
        }

        public boolean check_for_valid_move(int i, String move, int x, int y) {
            if (x >= 1 && x <= size && y >= 1 && y <= size) {
                if (move.equals(" "))
                    return true;
                if (isVisited(i, move))
                    return true;
            }
            return false;
        }

        public int[][] make_move(int[][] current_state, coordinate pivot, String str) {
            int[][] state = new int[size + 1][size + 1];
            for (int i = 1; i <= size; i++) {
                for (int j = 1; j <= size; j++)
                    state[i][j] = current_state[i][j];
            }
            for (int i = 0; i < 4; i++) {
                if (str.equals(move[i])) {
                    state[pivot.x][pivot.y] = state[pivot.x + dx[i]][pivot.y + dy[i]];
                    state[pivot.x + dx[i]][pivot.y + dy[i]] = 0;
                    break;
                }
            }
            return state;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void informed_bfs() {
            PriorityQueue<next_state> pq = new PriorityQueue<>(new Comparator<next_state>() {
                public int compare(next_state a, next_state b) {
                    return a.cost - b.cost;
                }
            });
            int level = 1;
            int cost = heuristic(initial_state) + level;
            pq.add(new next_state(cost, size, level, initial_state, " ", new coordinate(initial_pivot.x, initial_pivot.y), null));
            while (!pq.isEmpty()) {
                next_state min_move = pq.poll();
                if (heuristic(min_move.state) == 0) {
                    last_state = min_move;
                    return;
                }
                for (int i = 0; i < 4; i++) {
                    int x = min_move.pivot.x + dx[i];
                    int y = min_move.pivot.y + dy[i];
                    cost = 0;
                    if (check_for_valid_move(i, min_move.direction, x, y)) {
                        coordinate new_pivot = new coordinate(x, y);
                        int[][] new_state = make_move(min_move.state, min_move.pivot, move[i]);
                        cost = heuristic(new_state);
                        cost = cost + min_move.level + 1;
                        pq.add(new next_state(cost, size, min_move.level + 1, new_state, move[i], new_pivot, min_move));
                    }
                }
            }
        }


        public void print_path(next_state node) {
            if (node.parent == null)
                return;
            print_path(node.parent);
            //System.out.println("next state");
            //print(node.state);
            //Log.d("ADebugTag", "Value: " + ans);
            ans.add(node.direction);


        }
    }

    public static ArrayList<String> abcd(int[][] result, int[][] result1) {

        solve_n_puzzle puzzle = new solve_n_puzzle(3);
        puzzle.take_input(result, result1);

        puzzle.informed_bfs();
        puzzle.print_path(puzzle.last_state);
        return ans;
    }
}


public class MainActivity extends AppCompatActivity {
    /*String 2-D array to move the numbers after the solve function is called*/
    public static String[][] string;

    /*iterator to count the number of steps taken */
    public static int iterator = -1;

    /*object for onclick listner*/
    private Button btn;
    private Button resetbtn;
    /*this method is oncreate, which is called by android when the app is opened*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.submit);
        btn.setOnClickListener(myListner);
        resetbtn = findViewById(R.id.reset);
        resetbtn.setOnClickListener(resetListener);
    }

    public void setActivityBackgroundColor() {

        //Log.d("color" , "its hereeeee");
        View view =this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }
    public void setActivityBackgroundColor1() {

        //Log.d("color" , "its hereeeee" + color);
        View view =this.getWindow().getDecorView();
        view.setBackgroundColor(getResources().getColor(R.color.back));
        TextView edit = (TextView) findViewById(R.id.initial);
        edit.setTextColor(getResources().getColor(R.color.black));
        edit = (TextView) findViewById(R.id.final2);
        edit.setTextColor(getResources().getColor(R.color.black));
    }

    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setActivityBackgroundColor1();
            EditText edit = (EditText) findViewById(R.id.btn1);
            edit.setText("8");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn2);
            edit.setText("3");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn3);
            edit.setText("2");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn4);
            edit.setText("4");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn5);
            edit.setText("0");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn6);
            edit.setText("1");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn7);
            edit.setText("7");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn8);
            edit.setText("6");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            edit = (EditText) findViewById(R.id.btn0);
            edit.setText("5");
            edit.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
            Log.d("reset" , "its here");
            Button button = (Button) findViewById(R.id.count);
            button.setText("COUNT");

        }
    };

    /*this method removes the numpad from the screen when clicked anywhere except the numebers*/
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /*This method is onclick Listner for submit button*/
    private View.OnClickListener myListner = new View.OnClickListener() {
        public void onClick(View v) {
            setActivityBackgroundColor1();
            puzzle.ans = new ArrayList<String>();

            /*the custom input 2-D array*/
            int[][] result = new int[3][3];
            //Log.d("size", "here " + puzzle.size);

     /*----------------------------------------------------------------------------------------------*/
     /*This is to set up the input array*/
            EditText edit = (EditText) findViewById(R.id.btn1);
            result[0][0] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn2);
            result[0][1] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn3);
            result[0][2] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn4);
            result[1][0] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn5);
            result[1][1] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn6);
            result[1][2] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn7);
            result[2][0] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn8);
            result[2][1] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btn0);
            result[2][2] = Integer.parseInt(edit.getText().toString());

    /*-----------------------------------------------------------------------------------------------*/
    /*This is to setup the final state array*/

            int[][] result1 = new int[3][3];
            edit = (EditText) findViewById(R.id.btnf1);
            result1[0][0] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf2);
            result1[0][1] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf3);
            result1[0][2] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf4);
            result1[1][0] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf5);
            result1[1][1] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf6);
            result1[1][2] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf7);
            result1[2][0] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf8);
            result1[2][1] = Integer.parseInt(edit.getText().toString());
            edit = (EditText) findViewById(R.id.btnf0);
            result1[2][2] = Integer.parseInt(edit.getText().toString());
            Log.d("isSol" ,"abc" + puzzle.is_solvable(result,result1));

            /*this if block checks is the puzzle is solvable or not*/
            if (!puzzle.is_solvable(result, result1)) {
                Log.d("abcd", "its here");
                Context context = getApplicationContext();
                CharSequence text = "This puzzle is not solvable because it has odd number of inversions!";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                for(int i = 0;i<2;i++)
                toast.show();
            } /*otherwise*/ else {

                ArrayList<String> ans = puzzle.abcd(result, result1);
                Log.d("ADebugTag", "Value: " + ans);
                string = new String[3][3];

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        StringBuilder st = new StringBuilder("btn");
                        if (i == 2 && j == 2) {
                            st.append("0");
                        } else {
                            st.append((i * 3 + j) + 1);
                        }
                        String string1 = st.toString();
                        string[i][j] = string1;
                    }
                }

                EditText notes1 = (EditText) findViewById(getResources().getIdentifier(string[puzzle.intx][puzzle.inty], "id", getPackageName()));
                notes1.setBackground(getResources().getDrawable(R.drawable.zero, null));

                int size = puzzle.ans.size();

                /*This is to pause after each iteration of loop after the answer is obtained form tne ans arrayList*/
                new CountDownTimer((size+1) * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        if(iterator==-1){

                        }
                        else {
                            if (puzzle.ans.get(iterator).equals("UP")) {
                                int x1 = puzzle.intx;
                                int y1 = puzzle.inty;
                                EditText note = (EditText) findViewById(getResources().getIdentifier(string[puzzle.intx][puzzle.inty], "id", getPackageName()));
                                note.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
                                Log.d("ADebugTag", "x: " + puzzle.intx);
                                Log.d("ADebugTag", "y: " + puzzle.inty);
                                int x2 = x1 - 1;
                                int y2 = y1;
                                EditText notes1 = (EditText) findViewById(getResources().getIdentifier(string[x1][y1], "id", getPackageName()));
                                EditText notes = (EditText) findViewById(getResources().getIdentifier(string[x2][y2], "id", getPackageName()));
                                String temp = notes.getText().toString();
                                notes.setText(notes1.getText().toString());
                                notes1.setText(temp);
                                puzzle.intx = x2;
                                puzzle.inty = y2;
                            } else if (puzzle.ans.get(iterator).equals("DOWN")) {
                                int x1 = puzzle.intx;
                                int y1 = puzzle.inty;
                                EditText note = (EditText) findViewById(getResources().getIdentifier(string[puzzle.intx][puzzle.inty], "id", getPackageName()));
                                note.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
                                Log.d("ADebugTag", "x: " + puzzle.intx);
                                Log.d("ADebugTag", "y: " + puzzle.inty);
                                int x2 = x1 + 1;
                                int y2 = y1;
                                EditText notes1 = (EditText) findViewById(getResources().getIdentifier(string[x1][y1], "id", getPackageName()));
                                EditText notes = (EditText) findViewById(getResources().getIdentifier(string[x2][y2], "id", getPackageName()));
                                String temp = notes.getText().toString();
                                notes.setText(notes1.getText().toString());
                                notes1.setText(temp);
                                puzzle.intx = x2;
                                puzzle.inty = y2;
                            } else if (puzzle.ans.get(iterator).equals("RIGHT")) {
                                int x1 = puzzle.intx;
                                int y1 = puzzle.inty;
                                EditText note = (EditText) findViewById(getResources().getIdentifier(string[puzzle.intx][puzzle.inty], "id", getPackageName()));
                                note.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
                                Log.d("ADebugTag", "x: " + puzzle.intx);
                                Log.d("ADebugTag", "y: " + puzzle.inty);
                                int x2 = x1;
                                int y2 = y1 + 1;
                                EditText notes1 = (EditText) findViewById(getResources().getIdentifier(string[x1][y1], "id", getPackageName()));
                                EditText notes = (EditText) findViewById(getResources().getIdentifier(string[x2][y2], "id", getPackageName()));
                                String temp = notes.getText().toString();
                                notes.setText(notes1.getText().toString());
                                notes1.setText(temp);
                                puzzle.intx = x2;
                                puzzle.inty = y2;
                            } else if (puzzle.ans.get(iterator).equals("LEFT")) {
                                int x1 = puzzle.intx;
                                int y1 = puzzle.inty;
                                EditText note = (EditText) findViewById(getResources().getIdentifier(string[puzzle.intx][puzzle.inty], "id", getPackageName()));
                                note.setBackground(getResources().getDrawable(R.drawable.buttonshape, null));
                                Log.d("ADebugTag", "x: " + puzzle.intx);
                                Log.d("ADebugTag", "y: " + puzzle.inty);
                                int x2 = x1;
                                int y2 = y1 - 1;
                                EditText notes1 = (EditText) findViewById(getResources().getIdentifier(string[x1][y1], "id", getPackageName()));
                                EditText notes = (EditText) findViewById(getResources().getIdentifier(string[x2][y2], "id", getPackageName()));
                                String temp = notes.getText().toString();
                                notes.setText(notes1.getText().toString());
                                notes1.setText(temp);
                                puzzle.intx = x2;
                                puzzle.inty = y2;
                            }
                        }
                        EditText notes1 = (EditText) findViewById(getResources().getIdentifier(string[puzzle.intx][puzzle.inty], "id", getPackageName()));
                        notes1.setBackground(getResources().getDrawable(R.drawable.zero, null));
                        iterator++;
                        //String ab =
                        Button btn = (Button) findViewById(R.id.count);
                        btn.setText(Integer.toString(iterator));

                    }

                    public void onFinish() {
                        // mTextField.setText("done!");
                        iterator = -1;
                        setActivityBackgroundColor();
                        TextView edit = (TextView) findViewById(R.id.initial);
                        edit.setTextColor(getResources().getColor(R.color.back));
                        edit = (TextView) findViewById(R.id.final2);
                        edit.setTextColor(getResources().getColor(R.color.back));
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound);
                        mp.start();
                    }
                }.start();
            }
        }
    };
}
