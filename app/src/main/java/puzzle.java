/**
 * Created by MAHIN on 18-09-2018.
 */

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.*;
import java.io.*;
import java.lang.*;
public class puzzle
{   public static ArrayList<String> ans  = new ArrayList<>();
    public static class coordinate
    {
        int x;
        int y;
        public coordinate(int x,int y)
        {
            this.x = x;
            this.y = y;
        }
    }
    public static class next_state
    {
        int[][] state;
        int cost;
        coordinate pivot;
        String direction;
        int level;
        next_state parent;
        public next_state(int cost,int n,int l,int[][] temp,String move,coordinate b,next_state parent)
        {
            this.cost = cost;
            state = new int[n+1][n+1];
            this.pivot = b;
            direction = move;
            this.parent = parent;
            level = l;
            for(int i=1;i<=n;i++)
            {
                for(int j=1;j<=n;j++)
                    state[i][j] = temp[i][j];
            }
        }
    }
    public static class solve_n_puzzle
    {
        int size;
        int[][] initial_state;
        int[][] final_state;
        coordinate[] position;
        coordinate initial_pivot;
        next_state last_state;
        BufferedReader br;
        int[] dx = {-1,1,0,0};
        int[] dy = {0,0,-1,1};
        String[] move = {"UP","DOWN","LEFT","RIGHT"};
        public solve_n_puzzle(int n)
        {
            this.size = n;
            initial_pivot = new coordinate(0,0);
            br = new BufferedReader(new InputStreamReader(System.in));
            initial_state = new int[n+1][n+1];
            final_state = new int[n+1][n+1];
            position = new coordinate[n*n+1];
        }
        public void take_input(int [][]result, int [][]result1)
        {
            //System.out.println("Input : ");
            for(int i=1;i<=size;i++)
            {
               // String[] str = (br.readLine()).trim().split(" ");
                for(int j=1;j<=size;j++)
                {
                    initial_state[i][j] = result[i-1][j-1];
                    if(initial_state[i][j] == 0)
                        initial_pivot = new coordinate(i,j);
                }
            }
            //System.out.println("Output : ");
            for(int i=1;i<=size;i++)
            {
                //String[] str = (br.readLine()).trim().split(" ");
                for(int j=1;j<=size;j++)
                {
                    final_state[i][j] = result[i-1][j-1];
                    position[final_state[i][j]] = new coordinate(i,j);
                }
            }
        }
        public void print(int[][] arr)
        {
            for(int i=1;i<=size;i++)
            {
                for(int j=1;j<=size;j++)
                    System.out.print(arr[i][j] + " ");
                System.out.println();
            }
        }
        public int heuristic(int[][] current_state)
        {
            int hamming = 0,manhatton = 0;
            for(int i=1;i<=size;i++)
            {
                for(int j=1;j<=size;j++)
                {
                    if(current_state[i][j] != 0)
                    {
                        if(current_state[i][j] != final_state[i][j])
                            hamming = hamming + 1;
                        int x = Math.abs(position[current_state[i][j]].x - i);
                        int y = Math.abs(position[current_state[i][j]].y - j);
                        manhatton = manhatton + x + y;
                    }
                }
            }
            return Math.max(hamming,manhatton);
        }
        public boolean isVisited(int i,String str)
        {
            if(str.equals("UP") && !move[i].equals("DOWN"))
                return true;
            else if(str.equals("DOWN") && !move[i].equals("UP"))
                return true;
            else if(str.equals("LEFT") && !move[i].equals("RIGHT"))
                return true;
            else if(str.equals("RIGHT") && !move[i].equals("LEFT"))
                return true;
            return false;
        }
        public boolean check_for_valid_move(int i,String move,int x,int y)
        {
            if(x >= 1 && x <= size && y >= 1 && y <= size)
            {
                if(move.equals(" "))
                    return true;
                if(isVisited(i,move))
                    return true;
            }
            return false;
        }
        public int[][] make_move(int[][] current_state,coordinate pivot,String str)
        {
            int[][] state = new int[size+1][size+1];
            for(int i=1;i<=size;i++)
            {
                for(int j=1;j<=size;j++)
                    state[i][j] = current_state[i][j];
            }
            for(int i=0;i<4;i++)
            {
                if(str.equals(move[i]))
                {
                    state[pivot.x][pivot.y] = state[pivot.x + dx[i]][pivot.y + dy[i]];
                    state[pivot.x + dx[i]][pivot.y + dy[i]] = 0;
                    break;
                }
            }
            return state;
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void informed_bfs()
        {
            PriorityQueue<next_state> pq = new PriorityQueue<>(new Comparator<next_state>(){
                public int compare(next_state a,next_state b)
                {
                    return a.cost - b.cost;
                }
            });
            int level = 1;
            int cost = heuristic(initial_state) + level;
            pq.add(new next_state(cost,size,level,initial_state," ",new coordinate(initial_pivot.x,initial_pivot.y),null));
            while(!pq.isEmpty())
            {
                next_state min_move = pq.poll();
                if(heuristic(min_move.state) == 0)
                {
                    last_state = min_move;
                    return;
                }
                for(int i=0;i<4;i++)
                {
                    int x = min_move.pivot.x + dx[i];
                    int y = min_move.pivot.y + dy[i];
                    cost = 0;
                    if(check_for_valid_move(i,min_move.direction,x,y))
                    {
                        //System.out.println("hi");
                        coordinate new_pivot = new coordinate(x,y);
                        int[][] new_state = make_move(min_move.state,min_move.pivot,move[i]);
                        cost = heuristic(new_state);
                        cost = cost + min_move.level + 1;
                        pq.add(new next_state(cost,size,min_move.level+1,new_state,move[i],new_pivot,min_move));
                    }
                }
            }
        }


        public void print_path(next_state node)
        {
            if(node.parent == null)
                return;
            print_path(node.parent);
            //System.out.println("next state");
            //print(node.state);
            ans.add(node.direction);


        }
    }
    public static ArrayList<String> abcd(int [][]result, int[][] result1)
    {

        solve_n_puzzle puzzle = new solve_n_puzzle(3);
        puzzle.take_input(result,result1);

        puzzle.informed_bfs();
        puzzle.print_path(puzzle.last_state);
        return ans;
    }
}
