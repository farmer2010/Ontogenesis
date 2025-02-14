package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	private int x;
	private int y;
	public int xpos;
	public int ypos;
	public int killed = 0;
	public Bot[][] map;
	public int[][][] commands = new int[8][32][36];
	public int index = 0;
	public int state = 0;
	private int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	private int[] world_scale = {162, 108};
	private double[][][] reg_map;
	private boolean block_multiply = false;
	private boolean is_new = true;
	public Bot(int new_xpos, int new_ypos, Bot[][] new_map, double[][][] new_reg_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * 10;
		y = new_ypos * 10;
		objects = new_objects;
		map = new_map;
		reg_map = new_reg_map;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 32; j++) {
				for (int k = 0; k < 36; k++) {
					commands[i][j][k] = rand.nextInt(256);
				}
			}
		}
	}
	public void Draw(Graphics canvas, int draw_type) {
		canvas.setColor(new Color(0, 0, 0));
		canvas.fillRect(x, y, 10, 10);
		if (draw_type == 0) {
			if (state == 0) {
				canvas.setColor(new Color(230, 230, 230));
			}else if (state == 1) {
				canvas.setColor(new Color(255, 233, 128));
			}else if (state == 2) {
				canvas.setColor(new Color(128, 70, 0));
			}
		}else if (draw_type <= 8) {
			int r = (int)(reg_map[xpos][ypos][draw_type - 1] * 255);
			if (r > 255) {
				r = 255;
			}else if (r < 0) {
				r = 0;
			}
			if (draw_type == 1) {
				canvas.setColor(new Color(255, 255 - r, 255 - r));
			}else if (draw_type == 2) {
				canvas.setColor(new Color(255 - r, 255, 255 - r));
			}else if (draw_type == 3) {
				canvas.setColor(new Color(255 - r, 255 - r, 255));
			}else if (draw_type == 4) {
				canvas.setColor(new Color(255, 255, 255 - r));
			}else if (draw_type == 5) {
				canvas.setColor(new Color(255, 255 - r, 255));
			}else if (draw_type == 6) {
				canvas.setColor(new Color(255 - r, 255, 255));
			}
		}else if (draw_type == 9) {
			if (block_multiply) {
				canvas.setColor(new Color(255, 0, 0));
			}else {
				canvas.setColor(new Color(0, 255, 0));
			}
		}else if (draw_type == 10) {
			if (index == 0) {
				canvas.setColor(new Color(0, 0, 255));
			}else if (index == 1) {
				canvas.setColor(new Color(0, 255, 0));
			}else if (index == 2) {
				canvas.setColor(new Color(255, 0, 0));
			}
		}
		canvas.fillRect(x + 1, y + 1, 8, 8);
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0 && !is_new) {
			update_commands(iterator);
		}
		if (killed == 0 && !is_new) {
			if (!block_multiply && rand.nextInt(3) == 0) {
				multiply(rand.nextInt(8), iterator);
			}
		}
		is_new = false;
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		if (state == 0) {
			if (index == 0) {
				if (bot_count() == 0) {
					index = 1;
				}else {
					index = 2;
				}
				is_new = true;
			}else if (index == 1) {
				reg_map[xpos][ypos][0] = 1;
				reg_map[xpos][ypos][3] = 0.90;
			}else if (index == 2) {
				if (reg_map[xpos][ypos][0] < 0.001) {
					if (reg_map[xpos][ypos][2] >= 0.001) {
						reg_map[xpos][ypos][1] = 0.2;
						state = 2;
						block_multiply = true;
					}else {
						index = 1;
						reg_map[xpos][ypos][2] = 1.5;
					}
				}else {
					block_multiply = false;
					if (reg_map[xpos][ypos][1] >= 0.001 && bot_count() == 8) {
						state = 1;
						index = 0;
					}
				}
			}
		}else if (state == 1) {
			reg_map[xpos][ypos][5] = 0.125;
			if (index == 0) {
				reg_map[xpos][ypos][1] = 0.2;
				index = 1;
				is_new = true;
			}else if (index == 1) {
				if (reg_map[xpos][ypos][3] < 0.001) {
					reg_map[xpos][ypos][1] = 0.2;
					block_multiply = true;
				}else {
					block_multiply = false;
				}
				if (reg_map[xpos][ypos][0] < 0.001) {
					killed = 1;
					map[xpos][ypos] = null;
				}
				if (bot_count() < 8) {
					reg_map[xpos][ypos][4] = 0.1;
				}
			}
		}else if (state == 2) {
			if (reg_map[xpos][ypos][4] < 0.001) {
				block_multiply = true;
			}else {
				block_multiply = false;
			}
			if (reg_map[xpos][ypos][5] < 0.001) {
				killed = 1;
				map[xpos][ypos] = null;
			}
		}
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				Bot self = map[xpos][ypos];
				map[xpos][ypos] = null;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * 10;
				y = ypos * 10;
				map[xpos][ypos] = self;
				return(1);
			}
		}
		return(0);
	}
	public void multiply(int rot, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] >= 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				Bot new_bot = new Bot(pos[0], pos[1], map, reg_map, objects);
				new_bot.state = state;
				map[pos[0]][pos[1]] = new_bot;
				iterator.add(new_bot);
			}
		}
	}
	public int bot_count() {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] >= 0 & pos[1] < world_scale[1]) {
				if (map[pos[0]][pos[1]] != null) {
					count++;
				}
			}
		}
		return(count);
	}
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + movelist[rot][0]) % world_scale[0];
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = 161;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
	public int max(int number1, int number2) {//максимальное из двух чисел
		if (number1 > number2) {
			return(number1);
		}else if (number2 > number1) {
			return(number2);
		}else {
			return(number1);
		}
	}
}
