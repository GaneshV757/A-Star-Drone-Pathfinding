import java.util.*;
import java.io.*;
import java.math.*;

public class Player {
	int[][] overlap;
	int bx, by, myX, myY, myID;
	int prevDirection = 3;
	int playerCount = 1;
	boolean[][] grid = new boolean[30][15];
	int[][] players;
	Scanner in = new Scanner(System.in);
	int helperbots;
	int[][] enc = {{-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1},
					{-1, 1}};
	boolean enclosed = false;

	public static void main(String[] args) {
		// long start = System.currentTimeMillis();
		Player s = new Player();
		s.begin();
		while(true) {
		    s.run();
			// System.out.println("Time taken: " + (System.currentTimeMillis() - start));
		}
	}

	public void begin() {
	    playerCount = in.nextInt();
	    players = new int[playerCount][2];
	    myID = in.nextInt();
	}

	public void run() {
	    // update player bots
	    helperbots = in.nextInt();

	    // update other player coordinates
        for (int i = 0; i < playerCount; i++) {
            int x = in.nextInt(); // your bot's coordinates on the grid (0,0) is top-left
            int y = in.nextInt();
            if(x != -1 && y != -1) {
                grid[x][y] = true;
                players[i][0] = x;
                players[i][1] = y;
            }
        }

        int removalCount = in.nextInt(); // the amount walls removed this turn by helper bots
        for (int i = 0; i < removalCount; i++) {
            int x = in.nextInt(); // the coordinates of a wall removed this turn
            int y = in.nextInt();
            grid[x][y] = false;
        }

	    myX = players[myID][0];
	    myY = players[myID][1];

		// System.err.println("before overlap");
		// overlap = buildValue(myX, myY);
		// System.err.println("after one overlap");
		int start = 0;
		while(start == myID || players[start][0] == -1) {
			start++;
		}
		overlap = buildValue(players[start][0], players[start][1]);
		for(int i = start + 1; i < playerCount; i++) {
		    if(i != myID && players[i][0] != -1) {
		        overlap(buildValue(players[i][0], players[i][1]));
		    }
		}
		// System.err.println("after all overlap");
// 		overlap(buildValue(15, 5));
// 		overlap(buildValue(28, 10));
// 		overlap(buildValue(1, 3));
		// print(overlap);
		// print(grid);
		int direction = -1;
		findBestPoint();
		int counter = 0;
		do {
			// enclosed = enclosed();
			// if(!(enclosed)) {
			direction = astar(prevDirection, myX, myY);
			// } else {
			// 	direction = surround();
			// }
			// System.err.println("astar");
			// System.out.println("DIRECTION: " + direction + " " + allZero());
			// System.out.println(direction == -1 || allZero());
		} while(direction == -1 || allZero());
		if(direction == -1) {
		    System.out.println("DEPLOY");
		} else if(direction == 0) {
		    System.out.println("UP");
		} else if(direction == 1) {
		    System.out.println("RIGHT");
		} else if(direction == 2) {
		    System.out.println("DOWN");
		} else {
		    System.out.println("LEFT");
		}
		prevDirection = direction;
		// System.out.println(findBestPoint() + " - (" + bx + ", " + by + ")");
	}

	public int surround() {
		String[] commands = {"DOWN", "LEFT", "UP", "RIGHT"};
		String currComm = commands[0];
		String prevComm = commands[0];
		int commInd = 0;

		if(++commInd == 4) {
            commInd = 0;
        }

        currComm = commands[commInd];

        if(freeNextSquare(currComm, myX, myY)) {
            prevComm = currComm;
			switch(commInd) {
				case 0: case 2: return 2 - commInd;
				case 1: case 3: return 4 - commInd;
			}
			return -1;
        }
        if(--commInd == -1) {
            commInd = 3;
        }
        prevComm = commands[commInd];
        int repeat = 0;
        while(!freeNextSquare(prevComm, myX, myY)) {
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
    	switch(commInd) {
			case 0: case 2: return 2 - commInd;
			case 1: case 3: return 4 - commInd;
		}
		return -1;
	}

	public boolean enclosed() {
		int x = myX;
		int y = myY;
		int index = 0;
		if(prevDirection == 3) {
			index = 0;
		} else if(prevDirection == 2) {
			index = 2;
		} else if(prevDirection == 1) {
			index = 4;
		} else {
			index = 6;
		}
		boolean arrived = false;
		int counter = 0;
		while(counter < 100) {
			counter++;
			boolean loop = true;
			for(int i = 0; i < enc.length - 1 && loop; i++) {
				loop = !(grid[x + enc[(i + index) % 8][0]][y + enc[(i + index) % 8][1]]);
				if(!(loop)) {
					switch(index) {
						case 3:
						case 7: index = 10 - index; break;
						case 1:
						case 5: index = 6 - index; break;
						case 0:
						case 4: index = 4 - index; break;
						case 6:
						case 2: index = 8 - index; break;
					}
				}
			}
			if(x == myX && y == myY) {
				return true;
			} if(loop) {
				return false;
			}
		}
		return false;
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
		// if(prevDirection == 2 && grid[myX][(myY+1)%30]) {
		// 	return true;
		// }
		// else if(prevDirection == 0 && grid[myX][Math.abs(myY-1) %30]) {
		// 	return true;
		// }
		// else if(prevDirection == 3 && grid[Math.abs(myX-1) %30][myY]) {
		// 	return true;
		// }
		// else if(prevDirection == 1 && grid[(myX+1) % 30][myY]) {
		// 	return true;
		// }
		// return false;
	// }

	public boolean allZero() {
	    for(int x = 0; x < 30; x++) {
	        for(int y = 0; y < 15; y++) {
	            if(overlap[x][y] != 0)
	                return false;
	        }
	    }
	    return true;
	}

	public int findBestPoint() {
		int best = 0;
		for(int x = 0; x < 30; x++) {
			for(int y = 0; y < 15; y++) {
				if(overlap[x][y] > best && !(grid[x][y])) {
					best = overlap[x][y];
					bx = x;
					by = y;
				}
			}
		}
		return best;
	}

	public int astar(int direction, int cx, int cy) {
		Node beginN = new Node(cx, cy, null, direction);
        ArrayList<Node> arr = new ArrayList<Node>();
        ArrayList<Node> closed = new ArrayList<Node>();
		// System.out.println(bx + " " + by);
        beginN.setCost(bx, by);
		int currDir = direction;
        arr.add(beginN);
		// int counter = 0;
        while(!(arr.isEmpty())) {
            int min = arr.get(0).cost;
            Node minNode = arr.get(0);

            for(Node j : arr) {
                if(j.cost < min) {
                    minNode = j;
                    min = j.cost;
                }
            }

            arr.remove(minNode);
            int x = minNode.x;
            int y = minNode.y;
			currDir = minNode.direction;
			// if(counter % 25 == 0) {
			// 	System.out.println();
			// }
			// counter++;
			// System.out.printf("%10s", x + " " + y + " " + minNode.cost);

            Node down = notAWall(x, y + 1, minNode, 2);
            Node up = notAWall(x, y - 1, minNode, 0);
            Node left = notAWall(x + 1, y, minNode, 1);
            Node right = notAWall(x - 1, y, minNode, 3);
            switch(currDir) {
                case 0: down = null; break;
                case 1: left = null; break;
                case 2: up = null; break;
                default: right = null;
            }
            if(down != null && down.x == bx && down.y == by) {
                return getParentNode(down);
            } else if(up != null && up.x == bx && up.y == by) {
                return getParentNode(up);
            } else if(left != null && left.x == bx && left.y == by) {
                return getParentNode(left);
            } else if(right != null && right.x == bx && right.y == by) {
                return getParentNode(right);
            } else {
                if(down != null) {
                    addNode(down, arr, closed);
                } if(up != null) {
                    addNode(up, arr, closed);
                } if(left != null) {
                    addNode(left, arr, closed);
                } if(right != null) {
                    addNode(right, arr, closed);
                }
            }
			// System.out.printf("%20s%20s%20s%20s%20s\n", down, up, left, right, minNode);
            closed.add(minNode);
        }
        overlap[bx][by] = 0;
        findBestPoint();
        return -1;
	}

	public void addNode(Node n, ArrayList<Node> open, ArrayList<Node> closed) {
		n.setCost(bx, by);
		if(open.contains(n) && open.get(open.indexOf(n)).cost <= n.cost) {
			return;
		}
		if(closed.contains(n) && closed.get(closed.indexOf(n)).cost <= n.cost) {
			return;
		} else {
			open.add(n);
		}
	    // if(!(arr.contains(n)) || arr.get(arr.indexOf(n)).cost > n.cost) {
	    //     if(!(addArr.contains(n)) || addArr.get(addArr.indexOf(n)).cost > n.cost) {
	    //         n.setCost(bx, by);
        //         arr.add(n);
	    //     }
        // }
	}

	public Node notAWall(int x, int y, Node prev, int dir) {
        if(x < 0) {
	        x = 30+x;
	    }
	    if(y < 0) {
	        y = 15+y;
	    }
	    if(x > 29) {
	        x = x-30;
	    }
	    if(y > 14) {
	        y = y-15;
	    }
	    if(!(grid[x][y])) {
	        return new Node(x, y, prev, dir);
	    }
	    return null;
	}

	public int getParentNode(Node test) {
		// int count = 0;
	    while(test.prev != null) {
			// if(count % 20 == 0)
			// 	System.out.println();
			// count++;
			// System.out.print("curr dir: " + test.direction + "\t");
	        if(test.prev.prev == null) {
	            return test.direction;
	        }
	        test = test.prev;
	    }
	    return -1;
	}

	public int distance(int x1, int y1, int x2, int y2) {
		int xDist = Math.abs(x1 - x2);
		if(xDist > 15) {
			xDist = 30 - xDist;
		}
		int yDist = Math.abs(y1 - y2);
		if(yDist > 7) {
			yDist = 15 - yDist;
		}
		return xDist * xDist + yDist * yDist;
	}

	public void overlap(int[][] value) {
		for(int x = 0; x < 30; x++) {
			for(int y = 0; y < 15; y++) {
				if(overlap[x][y] > value[x][y]) {
					overlap[x][y] = value[x][y];
				}
			}
		}
	}

	public int[][] buildValue(int cx, int cy) {
		int[][] result = new int[30][15];
		for(int x = 0; x < 30; x++) {
			for(int y = 0; y < 15; y++) {
				result[x][y] = distFromCenter(cx, cy, x, y);
			}
		}
		return result;
	}

	public int distFromCenter(int cx, int cy, int x, int y) {
		int xDist = Math.abs(x - cx);
		if(xDist > 15) {
			xDist = 30 - xDist;
		}
		int yDist = Math.abs(y - cy);
		if(yDist > 7) {
			yDist = 15 - yDist;
		}
		return xDist + yDist;
	}

	public void print(int[][] array) {
		for(int i = 0; i < array[0].length; i++) {
			for(int j = 0; j < array.length; j++) {
				System.out.printf("%5d", array[j][i]);
			}
			System.out.println();
		}
	}

	public void print(boolean[][] array) {
		System.out.println();
		for(int i = 0; i < array[0].length; i++) {
			for(int j = 0; j < array.length; j++) {
				System.out.printf("%5s", array[j][i] ? "T" : "F");
			}
			System.out.println();
		}
	}

	class Node {
		public int x = -1;
		public int y = -1;
		public Node prev = null;
		public int cost = 0;
		public int direction = -1;

		public Node(int x, int y, Node p, int direction) {
		    if(x < 0) {
		        x = 30+x;
		    }
		    if(y < 0) {
		        y = 15+y;
		    }
		    if(x > 29) {
		        x = x-30;
		    }
		    if(y > 14) {
		        y = y-15;
		    }
			this.x = x;
			this.y = y;
			this.prev = p;
			this.direction = direction;
		}

		public void setCost(int bx, int by) {
    		int xDist = Math.abs(x - bx);
    		if(xDist > 15) {
    			xDist = 30 - xDist;
    		}
    		int yDist = Math.abs(y - by);
    		if(yDist > 7) {
    			yDist = 15 - yDist;
    		}
    		cost = xDist * xDist + yDist * yDist;
		}

		public boolean equals(Object o) {
		    Node n = (Node)(o);
		    if(n.x == x && n.y == y) {
		        return true;
		    }
		    return false;
		}

		public String toString() {
			return x + " " + y + " d: " + direction + " c: " + cost;
		}
	}
}
