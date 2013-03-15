package ubco.ai.games;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class chessBoard extends JFrame
{
	final int START = 2;
	boolean gameInProgress;
	Board board;;
	JPanel leftLabels;
	JPanel topLabels;
	JPanel rightSide;
	JPanel bottom;
	JTextPane text;
	chessBoard()
	{
		super();
		createGUI();
	}

	public void moveOurPieces(int id, int row, int col){
		board.moveOurPieceB(id, row, col);
	}
	public void moveTheirPieces(int id, int row, int col){
		board.moveTheirPieceB(id, row, col);
	}
	public void fireArrow(int row, int col){
		board.fireArrowB(row, col);
	}
	public void writeWhiteAndBlack(boolean whitePlayer){
		board.whiteAndBlack(whitePlayer);
	}
	public void writeMove(String player, String message){
		board.addMove(player, message);
	}
	
	protected void createGUI()
	{
		
		//background color
		Color background = new Color(74, 101, 60);
		//this.setSize(786, 806);
		this.setSize(1450,798);
		this.setResizable(false);
		this.setVisible(true);
		
		//Container contentPane = getContentPane();
		leftLabels = new LabelLeft();
		leftLabels.setBackground(background);
		topLabels = new LabelTop();
		topLabels.setBackground(background);
		rightSide = new rightSide();
		rightSide.setBackground(background);
		//rightSide.setSize(100, 0);
		bottom = new rightSide();
		bottom.setBackground(background);
		board = new Board();
		board.setBackground(background);
		
		this.add(board, "Center");
		this.add(leftLabels, "West");
		this.add(topLabels, "North");
		this.add(rightSide, "East");
		this.add(bottom, "South");
	}

	public class LabelLeft extends JPanel
	{
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			drawLabels(g2d);
		}
		protected void drawLabels(Graphics2D g2d){
			g2d.setColor(Color.white);
			String[] letters = {"j", "i", "h", "g", "f", "e", "d", "c", "b", "a"};
			for(int i = 0; i < 10; i++){
				//g2d.drawString(String.valueOf(i), 0, ((i*75) + 30));
				g2d.drawString(letters[i], 0, ((i*75) + 30));
			}
		}
	}
	public class LabelTop extends JPanel
	{
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			drawLabels(g2d);
		}
		protected void drawLabels(Graphics2D g2d){
			g2d.setColor(Color.white);
			for(int i = 0; i < 10; i++){
				g2d.drawString(String.valueOf(i), ((i*75) + 30), 10);
			}
		}
	}
	
	public class rightSide extends JPanel
	{
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			
		}
		protected void drawLabels(Graphics2D g2d){
			
		}
	}
	
	public class Board extends JPanel
	{

		Piece[] ourAmazons;
		Piece[] theirAmazons;
		Square[] squares;
		ArrayList<Arrow> arrows;
		int numRows = 10;
		int numCols = 10;
		int size = 75;
		JTextPane text;
		String message = "";
		ArrayList<String> whiteMoves;
		ArrayList<String> blackMoves;
		
		Board()
		{
			super();
			//text = new JTextPane();
			whiteMoves = new ArrayList<String>();
			blackMoves = new ArrayList<String>();
			ourAmazons = new Piece[4];
			theirAmazons = new Piece[4];
			squares = new Square[100];
			//create a list of arrows and there might be an arrow in every square. Well not really but whatever
			arrows = new ArrayList<Arrow>();
			setupBoard();
			
			//this.add(text, "right");
		}
		
		public void moveOurPieceB(int id, int row, int col){
			ourAmazons[id].setFrame((col*size), (row*size), size, size);
			repaint();
		}
		public void moveTheirPieceB(int id, int row, int col){
			theirAmazons[id].setFrame((col*size), (row*size), size, size);
			repaint();
		}
		public void fireArrowB(int row, int col){
			Arrow arr = new Arrow();
			arr.setFrame((col*size)+10, (row*size)+10, 50, 50);
			arrows.add(arr);
		}
		public void whiteAndBlack(boolean whitePlayer){
			if(whitePlayer){
				message = "You are the White player!";
			}
			else{
				message = "You are the Black player!";
			}
			repaint();
		}
		public void addMove(String player, String message){
			if(player.equals("white")){
				whiteMoves.add(message);
			}
			else{
				blackMoves.add(message);
			}
			repaint();
		}
		
		//set each piece frame before game starts
		protected void setupBoard()
		{
			//set up all starting positions for our amazons
			ourAmazons[0] = new Piece();
			ourAmazons[0].setFrame((0*size), (6*size), size, size);
			
			ourAmazons[1] = new Piece();
			ourAmazons[1].setFrame((3*size), (9*size), size, size);
			
			ourAmazons[2] = new Piece();
			ourAmazons[2].setFrame((6*size), (9*size), size, size);
			
			ourAmazons[3] = new Piece();
			ourAmazons[3].setFrame((9*size), (6*size), size, size);
			
			//set up all starting positions for their amazons
			theirAmazons[0] = new Piece();
			theirAmazons[0].setFrame((0*size), (3*size), size, size);
			
			theirAmazons[1] = new Piece();
			theirAmazons[1].setFrame((3*size), (0*size), size, size);
			
			theirAmazons[2] = new Piece();
			theirAmazons[2].setFrame((6*size), (0*size), size, size);
			
			theirAmazons[3] = new Piece();
			theirAmazons[3].setFrame((9*size), (3*size), size, size);
			
			//setup squares
			int count = 0;
			//create colors
			Color darkBrown = new Color(196, 114, 32);
			Color lightBrown = new Color(255, 235, 161);
			for(int i = 0; i < numCols; i++)
			{
				for(int j = 0; j < numRows; j++)
				{
					squares[count] = new Square();
					squares[count].setFrame((i*size), (j*size), size, size);
					if(i%2 == 0)
					{
						if(count%2==0)
							squares[count].setColor(darkBrown);
						else 
							squares[count].setColor(lightBrown);
					}
					else
					{
						if(count%2==0)
							squares[count].setColor(lightBrown);
						else 
							squares[count].setColor(darkBrown);
					}
					count++;
				}
			}
			//moveOurPieceB(0, 0, 1);
		}
		
		protected Piece getActivePiece()
		{
			for(int i = 0; i < ourAmazons.length; i++)
			{
				if(ourAmazons[i].isActive())
					return ourAmazons[i];
			}
			return null;
		}
		//you must overide the paint method
		//to draw the new moves and such
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			drawGrid(g2d);
			drawPieces(g2d);
			drawArrows(g2d);
			g2d.setColor(Color.white);
			g2d.drawString(message, 1000, 40);
			drawMoves(g2d);
		}
		
		protected void drawMoves(Graphics2D g2d){
			g2d.setColor(Color.white);
			for(int i = 0; i < whiteMoves.size(); i++){
				g2d.drawString(whiteMoves.get(i), 770, (i*15)+60);
			}
			for(int i = 0; i < blackMoves.size(); i++){
				g2d.drawString(blackMoves.get(i), 1100, (i*15)+60);
			}
			
		}
		
		protected void drawArrows(Graphics2D g2d){
			for(int i = 0; i < arrows.size(); i++){
				g2d.setColor(arrows.get(i).c);
				g2d.fill(arrows.get(i));
			}
		}

		protected void drawPieces(Graphics2D g2d) 
		{	
			for(int i = 0; i < ourAmazons.length; i++)
			{
				BufferedImage image = null;
				try{
					image = ImageIO.read(new File("whitePiece.png"));
				}
				catch(IOException ex){
					//System.out.println("Can't find image");
				}
				g2d.drawImage(image, null, ourAmazons[i].x+13, ourAmazons[i].y+13);
			}
			for(int i = 0; i < theirAmazons.length; i++)
			{
				BufferedImage image = null;
				try{
					image = ImageIO.read(new File("blackPiece.png"));
				}
				catch(IOException ex){
					//System.out.println("Can't find image");
				}
				g2d.drawImage(image, null, theirAmazons[i].x+13, theirAmazons[i].y+13);
			}

		}
		protected void drawGrid(Graphics2D g2d) 
		{
			for(int i = 0; i < squares.length; i++)
			{
				g2d.setColor(squares[i].getColor());
				//g2d.fill((Shape) new ImageIcon("whitePiece.png"));
				g2d.fill(squares[i]);
			}
		}

	}

	protected class Piece extends Ellipse2D
	{
		protected Color c;
		protected int x,y,x2,y2;
		protected boolean isActive;

		public Piece()
		{
			super(); 
			c = Color.pink;
		}

		public double getHeight() {
			
			return y2;
		}
		@Override
		public double getWidth() {
			return x2;
		}
		@Override
		public double getX() {
			return x;
		}
		@Override
		public double getY() {
			return y;
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public void setFrame(double arg0, double arg1, double arg2, double arg3) {
			x = (int) arg0; y = (int)arg1; x2 = (int)arg2; y2 = (int)arg3;
		}
		public Rectangle2D getBounds2D() {
			return null;
		}
		public Color getColor()
		{
			return c;
		}
		public void setColor(Color c)
		{
			this.c = c;
		}
		public boolean isActive()
		{
			return isActive;
		}
		public void setActive()
		{
			isActive = true;
		}
		public void setInActive()
		{
			isActive = false;
		}
	}
	
	protected class Arrow extends Ellipse2D{
		protected Color c;
		protected int x,y,x2,y2;
		
		public Arrow(){
			super(); 
			c = Color.black;
		}
		
		@Override
		public Rectangle2D getBounds2D() {
			return null;
		}

		@Override
		public double getHeight() {
			return y2;
		}

		@Override
		public double getWidth() {
			return x2;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public void setFrame(double arg0, double arg1, double arg2, double arg3) {
			x = (int) arg0; y = (int)arg1; x2 = (int)arg2; y2 = (int)arg3;
		}
		
	}

	protected class Square extends Rectangle2D
	{
		protected Color c;
		protected double x,y,x2,y2;

		Square()
		{
			super(); 
			c = Color.black;
		}

		public double getHeight() {
			return y2;
		}
		@Override
		public double getWidth() {
			return x2;
		}
		@Override
		public double getX() {
			return x;
		}
		@Override
		public double getY() {
			return y;
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public void setFrame(double arg0, double arg1, double arg2, double arg3) {
			x = arg0; y = arg1; x2 = arg2; y2 = arg3;
		}
		public Rectangle2D getBounds2D() {
			return null;
		}
		public Color getColor()
		{
			return c;
		}
		public void setColor(Color c)
		{
			this.c = c;
		}

		@Override
		public Rectangle2D createIntersection(Rectangle2D arg0) {
			return null;
		}

		@Override
		public Rectangle2D createUnion(Rectangle2D arg0) {
			return null;
		}

		@Override
		public int outcode(double arg0, double arg1) {
			return 0;
		}

		@Override
		public void setRect(double arg0, double arg1, double arg2, double arg3) {}
	}
}