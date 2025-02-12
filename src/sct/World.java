package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.*;

import java.awt.Font;
//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.Graphics2D;

public class World extends JPanel{
	ArrayList<Bot> objects;
	int size = 25;
	Timer timer;
	int delay = 1;
	Random rand = new Random();
	Bot[][] Map = new Bot[162][108];//0 - none, 1 - bot, 2 - organics
	double[][][] reg_map = new double[162][108][8];
	Color gray = new Color(100, 100, 100);
	Color green = new Color(0, 255, 0);
	Color red = new Color(255, 0, 0);
	Color black = new Color(0, 0, 0);
	int steps = 0;
	int obj_count = 0;
	String txt2;
	int mouse = 0;
	int W = 1920;
	int H = 1080;
	JButton stop_button = new JButton("Stop");
	boolean pause = false;
	boolean render = true;
	Bot selection = null;
	int[] botpos = new int [2];
	int[] for_set;
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JTextField for_save = new JTextField();
	JTextField for_load = new JTextField();
	boolean sh_brain = false;
	boolean rec = false;
	public int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	int draw_type = 0;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		stop_button.addActionListener(new start_stop());
		stop_button.setBounds(W - 300, 125, 250, 35);
        add(stop_button);
        //
        JButton predators_button = new JButton("Standart");
        predators_button.addActionListener(e -> change_draw_type(0));
		predators_button.setBounds(W - 300, 190, 95, 20);
        add(predators_button);
        //
        JButton energy_button = new JButton("Morph 1");
        energy_button.addActionListener(e -> change_draw_type(1));
		energy_button.setBounds(W - 200, 190, 95, 20);
        add(energy_button);
        //
        JButton select_button = new JButton("Select");
        select_button.addActionListener(new select());
		select_button.setBounds(W - 300, 455, 95, 20);
        add(select_button);
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(new remove());
        remove_button.setBounds(W - 100, 455, 95, 20);
        add(remove_button);
        //
        for_load.setBounds(W - 300, 515, 250, 20);
        add(for_load);
        //
        JButton load_bot_button = new JButton("Load bot");
        load_bot_button.addActionListener(new load_bot());
        load_bot_button.setBounds(W - 300, 540, 90, 20);
        add(load_bot_button);
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(new nwp());
        new_population_button.setBounds(W - 300, 590, 125, 20);
        add(new_population_button);
        //
        render_button.addActionListener(new rndr());
        render_button.setBounds(W - 300, 615, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(new rcrd());
        record_button.setBounds(W - 170, 615, 125, 20);
        add(record_button);
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(new kill_all());
        kill_button.setBounds(W - 170, 590, 125, 20);
        add(kill_button);
        //
        //for (int x = 0; x < 162; x++) {
		//	for (int y = 0; y < 108; y++) {
		//		reg_map[x][y][0] = 0;
		//	}
        //}
        //
		newPopulation();
		timer.start();
	}
	public boolean find_map_pos(int[] pos, int state) {
		if (Map[pos[0]][pos[1]] != null) {
			if (Map[pos[0]][pos[1]].state == state) {
				return(true);
			}
		}
		return(false);
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		if (render) {
			if (draw_type == 1) {
				for (int x = 0; x < 162; x++) {
					for (int y = 0; y < 108; y++) {
						int r = (int)(reg_map[x][y][0] * 255);
						if (r > 255) {
							r = 255;
						}else if (r < 0) {
							r = 0;
						}
						canvas.setColor(new Color(255, 255 - r, 255 - r));
						canvas.fillRect(x * 10, y * 10, 10, 10);
					}
				}
			}
			for(Bot b: objects) {
				b.Draw(canvas, draw_type);
			}
		}
		canvas.setColor(gray);
		canvas.fillRect(W - 300, 0, 300, 1080);
		canvas.setColor(black);
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("steps: " + String.valueOf(steps), W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(obj_count), W - 300, 80);
		if (draw_type == 0) {
			canvas.drawString("render type: " + "bot types view", W - 300, 100);
		}else if (draw_type == 1) {
			canvas.drawString("render type: " + "morphogene 1 view", W - 300, 100);
		}
		canvas.drawString("Render types:", W - 300, 180);
		if (mouse == 0) {
			txt2 = "select";
		}else if (mouse == 1) {
			txt2 = "set";
		}else {
			txt2 = "remove";
		}
		canvas.drawString("mouse function: " + txt2, W - 300, 120);
		canvas.drawString("Selection:", W - 300, 275);
		canvas.drawString("Load:", W - 300, 490);
		canvas.drawString("enter name:", W - 300, 510);
		canvas.drawString("Controls:", W - 300, 580);
		if (selection != null) {
			canvas.drawString("position: " + "[" + String.valueOf(selection.xpos) + ", " + String.valueOf(selection.ypos) + "]", W - 300, 335);
			canvas.setColor(new Color(90, 90, 90, 90));
			canvas.fillRect(0, 0, W - 300, 1080);
			canvas.setColor(new Color(255, 0, 0));
			canvas.fillRect(selection.xpos * 10, selection.ypos * 10, 10, 10);
		}else {
			canvas.drawString("none", W - 300, 295);
		}
		if (rec && steps % 25 == 0) {
			try {
				BufferedImage buff = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = buff.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, 0);
				}
				g2d.dispose();
				ImageIO.write(buff, "png", new File("record/screen" + String.valueOf(steps / 25)+ ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void newPopulation() {
		steps = 0;
		objects = new ArrayList<Bot>();
		Map = new Bot[162][108];
		reg_map = new double[162][108][8];
		int x = 162 / 2;
		int y = 108 / 2;
		if (Map[x][y] == null) {
			Bot new_bot = new Bot(
				x,
				y,
				Map,
				reg_map,
				objects
			);
			//objects.add(new_bot);
			//Map[x][y] = new_bot;
		}
		repaint();
	}
	public int[] get_rotate_position(int rot, int[] sp){
		int[] pos = new int[2];
		pos[0] = (sp[0] + movelist[rot][0]) % 162;
		pos[1] = sp[1] + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = 161;
		}else if(pos[0] >= 162) {
			pos[0] = 0;
		}
		return(pos);
	}
	public void update_regulator(int ind) {//распространение
		double[][] new_map = new double[162][108];//копируем карту
		for (int x = 0; x < 162; x++) {
			for (int y = 0; y < 108; y++) {
				new_map[x][y] = reg_map[x][y][ind];
			}
		}
		//
		for (int x = 0; x < 162; x++) {//проходим по всем клеткам
			for (int y = 0; y < 108; y++) {
				double max = 0;
				for (int i = 0; i < 8; i++) {//каждая клетка смотрит на соседей и находит соседа с максимальным количеством морфогена
					int[] f = {x, y};
					int[] pos = get_rotate_position(i, f);
					if (pos[1] >= 0 && pos[1] < 108) {//границы
						double r;//сколько морфогена будем учитывать
						if (i % 2 == 0) {//по ортогонали не меняем значение
							r = reg_map[pos[0]][pos[1]][ind];
						}else {//по диагонали делим на sqrt(2)
							r = reg_map[pos[0]][pos[1]][ind] / 1.41;
						}
						if (r > max) {//находим максимальное из текущего и предыдущего
							max = r;
						}
					}
				}
				if (max > 0.05 && max > reg_map[x][y][ind]) {//записываем в клетку морфоген - шаг
					new_map[x][y] = max - 0.05;
				}
				new_map[x][y] *= 0.999;//испарение
			}
		}
		//
		for (int x = 0; x < 162; x++) {//копируем карту еще раз
			for (int y = 0; y < 108; y++) {
				reg_map[x][y][ind] = new_map[x][y];
			}
		}
	}
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			if (e.getX() < W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				reg_map[botpos[0]][botpos[1]][0] = 1;
				if (mouse == 0) {//select
					if (find_map_pos(botpos, 0)) {
						Bot b = Map[botpos[0]][botpos[1]];
						selection = b;
					}else {
						selection = null;
					}
				}else {//remove
					if (Map[botpos[0]][botpos[1]] != null) {
						Bot b = Map[botpos[0]][botpos[1]];
						b.killed = 1;
						Map[botpos[0]][botpos[1]] = null;
					}
				}
			}else {
				selection = null;
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (e.getX() < W - 300) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				reg_map[botpos[0]][botpos[1]][0] = 1;
				if (mouse == 2) {//remove
					if (Map[botpos[0]][botpos[1]] != null) {
						Bot b = Map[botpos[0]][botpos[1]];
						b.killed = 1;
						Map[botpos[0]][botpos[1]] = null;
					}
				}
			}else {
				selection = null;
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				steps++;
				obj_count = 0;
				ListIterator<Bot> bot_iterator = objects.listIterator();
				while (bot_iterator.hasNext()) {
					Bot next_bot = bot_iterator.next();
					next_bot.Update(bot_iterator);
					if (selection != null) {
						if (next_bot.xpos == selection.xpos && next_bot.ypos == selection.ypos) {
							if (next_bot != selection) {
								selection = null;
								sh_brain = false;
							}
						}
					}
					obj_count++;
				}
				if (selection != null) {
					int[] pos = {selection.xpos, selection.ypos};
					if (selection.killed == 1 || Map[pos[0]][pos[1]] == null){
						selection = null;
						sh_brain = false;
					}
				}
				update_regulator(0);
			}
			ListIterator<Bot> iterator = objects.listIterator();
			while (iterator.hasNext()) {
				Bot next_bot = iterator.next();
				if (next_bot.killed == 1) {
					iterator.remove();
				}
			}
			repaint();
		}
	}
	public void change_draw_type(int num) {
		draw_type = num;
	}
	private class start_stop implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			pause = !pause;
			if (pause) {
				stop_button.setText("Start");
			}else {
				stop_button.setText("Stop");
			}
		}
	}
	private class select implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 0;
		}
	}
	private class remove implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 2;
		}
	}
	private class nwp implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			newPopulation();
		}
	}
	private class rndr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			render = !render;
			if (render) {
				render_button.setText("Render: on");
			}else {
				render_button.setText("Render: off");
			}
		}
	}
	private class rcrd implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			rec = !rec;
			if (rec) {
				record_button.setText("Record: on");
			}else {
				record_button.setText("Record: off");
			}
		}
	}
	private class kill_all implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			steps = 0;
			objects = new ArrayList<Bot>();
			Map = new Bot[162][108];//0 - none, 1 - bot, 2 - organics
		}
	}
	private class load_bot implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try {
	            FileReader fileReader = new FileReader("saved objects/" + for_load.getText() + ".dat");
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	 
	            String line = bufferedReader.readLine();
	 
	            bufferedReader.close();
	            
	            String[] l = line.split(" ");
	            for_set = new int[64];
	            for (int i = 0; i < 64; i++) {
	            	for_set[i] = Integer.parseInt(l[i]);
	            }
	        } catch (IOException ex) {
	            System.out.println("Ошибка при чтении файла");
	            ex.printStackTrace();
	        }
		}
	}
}
