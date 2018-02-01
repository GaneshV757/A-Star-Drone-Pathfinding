import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Don't run into a player's light trail! Use your helper bots at strategic moments or as a last resort to be the last drone standing!
 **/
class Player {
    
    Scanner in = null;
    String[] commands = {"DOWN", "LEFT", "UP", "RIGHT"};
    String currComm = commands[0];
    String prevComm = commands[0];
    int commInd = 0;
    Location[] playerCoords = null;
    int playerCount = -1;
    int myID = -1;
    boolean[][] grid = new boolean[30][15];
    int helperBots = 3;
    int repeat2 = 0;
    int repeatCount = 4;
    
    static class Location {
        int x = -1;
        int y = -1;
        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
    }

    public static void main(String args[]) {
        Player temp = new Player();
        temp.begin();
        while(true) {
            temp.run();    
        }
    }
    
    public void begin() {
        in = new Scanner(System.in);
        playerCount = in.nextInt();
        playerCoords = new Location[playerCount]; // the number of at the start of this game
        myID = in.nextInt();
    }
    
    public void run() {
        long start = System.currentTimeMillis();

        String depCommand = null; // or boolean?

        // update helpbots
        helperBots = in.nextInt(); // your number of charges left to deploy helper bots
        
        // update other player coordinates
        for (int i = 0; i < playerCount; i++) {
            int x = in.nextInt(); // your bot's coordinates on the grid (0,0) is top-left
            int y = in.nextInt();
            if(x != -1 && y != -1) {
                grid[x][y] = true;
                playerCoords[i] = new Location(x, y);   
            }
        }
        
        // update walls removed by players
        int removalCount = in.nextInt(); // the amount walls removed this turn by helper bots
        for (int i = 0; i < removalCount; i++) {
            int x = in.nextInt(); // the coordinates of a wall removed this turn
            int y = in.nextInt();
            grid[x][y] = false;
        }
        
        if(repeat2 <repeatCount/2) {
        if(++commInd == 4) {
            commInd = 0;
        }
        
        currComm = commands[commInd];
        
 
        
        if(freeNextSquare(currComm, playerCoords[myID].getX(), playerCoords[myID].getY())) {
            prevComm = currComm;
            System.out.println(currComm);
            return;
        }
        if(--commInd == -1) {
            commInd = 3;
        }
        prevComm = commands[commInd];
        int repeat = 0;
        while(!freeNextSquare(prevComm, playerCoords[myID].getX(), playerCoords[myID].getY())) {
          if(commInd == 0) {
                commInd = 4;
            }
            prevComm = commands[--commInd];
            
            repeat++;
            if(repeat == 3) {
              break;
            }
        }
        
        currComm = prevComm;
        System.out.println(currComm);
        repeat2++;
        }
        else if(repeat2 < repeatCount){
          
          if(--commInd == -1) {
            commInd = 3;
          }
        
        currComm = commands[commInd];
        
 
        
        if(freeNextSquare(currComm, playerCoords[myID].getX(), playerCoords[myID].getY())) {
            prevComm = currComm;
            System.out.println(currComm);
            return;
        }
        if(++commInd == 4) {
            commInd = 0;
        }
        prevComm = commands[commInd];
        int repeat = 0;
        while(!freeNextSquare(prevComm, playerCoords[myID].getX(), playerCoords[myID].getY())) {
          if(commInd == 3) {
                commInd = -1;
            }
            prevComm = commands[++commInd];
            
            repeat++;
            if(repeat == 3) {
              break;
            }
        }
        
        currComm = prevComm;
        System.out.println(currComm);
        repeat2++;
        }
        
        if(repeat2 == repeatCount){
          repeat2 = 0;
          repeatCount+=2;
        }
            

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        //System.err.println(playerCount + " " + myId);
        // DOWN | LEFT | RIGHT | UP or DEPLOY (to clear walls)
        //System.out.println("DOWN");
    	System.err.println("Time taken: " + (System.currentTimeMillis() - start));

    }
    
    public boolean freeNextSquare(String direction, int x, int y) {
        if(direction.equals("DOWN") && y+1 != grid[x].length && !grid[x][y+1]) {
            return true;
        }
        else if(direction.equals("UP") && y!=0 && !grid[x][y-1]) {
            return true;
        }
        else if(direction.equals("LEFT") && x!=0 && !grid[x-1][y]) {
            return true;
        }
        else if(direction.equals("RIGHT") && x+1 != grid.length &&!grid[x+1][y]) {
            return true;
        }
        return false;
    }
        
}