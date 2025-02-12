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
		if (state == 0) {//рисуем бота
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 10, 10);
			if (draw_type == 0) {
				canvas.setColor(new Color(255, 233, 128));
			}else if (draw_type == 1) {
				int r = (int)(reg_map[xpos][ypos][0] * 255);
				if (r > 255) {
					r = 255;
				}else if (r < 0) {
					r = 0;
				}
				canvas.setColor(new Color(255, 255 - r, 255 - r));
			}
			canvas.fillRect(x + 1, y + 1, 8, 8);
		}else {//
			
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//бот
				update_commands(iterator);
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		multiply(rand.nextInt(8), iterator);
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
				map[pos[0]][pos[1]] = new_bot;
				iterator.add(new_bot);
			}
		}
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
