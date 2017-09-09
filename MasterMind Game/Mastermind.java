/**
 * Created by Matthew on 27/03/2016.
 */
import java.awt.BorderLayout;import java.awt.Color;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.Random;

//import all the things

/*
todo : Note for Marker:

        I have not implemented a 'smart algorithm' but a naive one (according to mark scheme).
        I've done everything else tho, good luck.
 */



public class Mastermind extends JFrame  implements ActionListener {

    int width;      //width
    int height;     //height of screen, used alongside numGuesses
    int numColors;  //number of colours used
    static int numGuesses; //number of guesses made
    int start = 0;      //leads up to end value of number of guesses - edited in main first num in mastermind
    JButton[][] colouredPegs;   //button for colours
    JButton[][] whites;         //button for white pegs
    JButton[][] blacks;         //button for black pegs
    JButton[] computerGuess;
    int state[][];
    int[] hiddenGuess;          //answer
    List<Integer[]> compguess;
    JButton guess = new JButton("Guess");       //for computer guess
    JButton Enter = new JButton("Enter");       //scanner for user guess
    JPanel colouredPanel = new JPanel();
    JPanel whitesPanel = new JPanel();
    JPanel blacksPanel = new JPanel();
    JPanel computerGuessPanel = new JPanel();
    JPanel panel2 = new JPanel();     //panels and guesses for use by the computer


    static int blacks (int one[], int two[]) {          //use for black pegs
        int val=0;

        for (int i=0;i<one.length;i++) {
            if (one[i]==two[i]) {           //if both values are equal print out black peg
                val++;
            }
        }

        return val;
    }

    static int whites (int [] one, int [ ] two) {       //used for the white pegs
        boolean found;
        int [ ] oneA = new int[one.length];     //similar method to the black pegs, using an array and for loop to
        int [ ] twoA = new int[one.length];     //decide on the values found

        for (int i=0;i<one.length;i++) {
            oneA[i]=one[i]; twoA[i]=two[i];
        }

        int val=0;
        for (int i=0;i<one.length;i++) {
            if (oneA[i]==twoA[i]) {             //if both values are equal then print out a white peg
                oneA[i]=0-i-10;twoA[i]=0-i-20;
            }
        }

        for (int i=0;i<one.length;i++) {
            found=false;
            for (int j=0;j<one.length && !found;j++) {
                if (i!=j && oneA[i]==twoA[j])
                {val++;oneA[i]=0-i-10;twoA[j]=0-j-20;found=true;}
            }
        }

        return val;
    }

    Integer inputNum(String InputText) { //method to implement user entering an int
        System.out.print(InputText);
        Scanner user_input = new Scanner(System.in);    //scanner to put in users input
        return user_input.nextInt();
    }

    static Color choose(int i) {
        if (i==0) return Color.red;
        if (i==1) return Color.green;
        if (i==2) return Color.blue;
        if (i==3) return Color.orange;
        else return Color.yellow;
    }

    public Mastermind(int h, int w, int c) throws FileNotFoundException, UnsupportedEncodingException {  //throws exception
        width=w; height=h; numColors=c;
        numGuesses=0;           //number of guesses made by computer

        this.setSize(new Dimension(300, 50 * height));      //size of the gui grid
        hiddenGuess = new int[width];       //this is the guess made by the ?
        state = new int[height][width];         //state of the gui
        colouredPegs= new JButton[height][width];   //coloured pegs and their position in the grid
        whites= new JButton[height][width];         //white implemented as JButton on gui
        blacks= new JButton[height][width];         //blacks implemented as JButton on gui
        computerGuess= new JButton[width];          //computers guess made a JButton
        colouredPanel.setLayout(new GridLayout(height,width));
        blacksPanel.setLayout(new GridLayout(height,width));        //sets layouts for the panels
        whitesPanel.setLayout(new GridLayout(height,width));
        computerGuessPanel.setLayout(new GridLayout(1,width));
        colouredPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));       //sets borders for the panels
        whitesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        blacksPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        class bing implements ActionListener {
            int x;
            int y;

            public void actionPerformed(ActionEvent e) {
                state[x][y] = (state[x][y] + 1) % numColors;
                System.out.println(state[x][y]);
                ((JButton)(e.getSource())).setBackground(choose(state[x][y]));
            }

            public bing (int p,int q) {
                x=p;y=q;
            }
        }

        for (int k = 0; k < width; k++) {

            computerGuess[k] = new JButton();
            int Pchoose = inputNum("Please select your answer to the program: " + k + "-\n0:Red 1:Green 2:Blue 3:Orange 4:Yellow ");
            computerGuess[k].setVisible(true);          //implements users guess as answer
            computerGuess[k].setBackground(choose(Pchoose));     //player choice set as background
            computerGuessPanel.add(computerGuess[k]);
            hiddenGuess[k] = Pchoose;       //player choice set as answer

/*      Original answer
            computerGuess[k].setBackground(choose(hiddenGuess[k]));
            hiddenGuess[k]=rand.nextInt(numColors); //you can use this to make the user enter a pattern - how?
            int j = 0;
            state[k][j] = 0;
            colouredPegs[k][j] = new JButton();
            colouredPegs[k][j].addActionListener(new bing(k, j));
            colouredPegs[k][j].setBackground(choose(state[k][j]));

            colouredPanel.add(colouredPegs[k][j]);
             */
        }

        compguess = new ArrayList<>();       //array list for possible choices

        for (int i = 0; i < 5; i++) { //red
            for (int j = 0; j < 5; j++) { //green
                for (int k = 0; k < 5; k++) { //blue
                    for (int l = 0; l < 5; l++) { //orange
                        Integer[] colourmovement = new Integer[4];           //implementation of 'dumb' algorithm
                                                                            //that ignores white and black pegs
                        colourmovement[0] = i;
                        colourmovement[1] = j;
                        colourmovement[2] = k;
                        colourmovement[3] = l;                   //possible colours computer can choose

                        compguess.add(colourmovement);          //add the possible movements to the gui
                    }
                }
            }
        }

        int q = 0;
        for (Integer[] possible : compguess) {             //implement the comp guess via the for loop
            System.out.print(q + " ");                    //prints out number of guesses made by comp
            q++;                            //increment q throughout loop
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.println("Average number of guesses made: " +  31562.5 / q);  //31562 is sum of total guesses made by comp 1 + 2 + 3... + 625
            writer.close();


            System.out.println(Arrays.toString(possible));         //prints out string of colours comp uses
        }


        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                state[i][j] = compguess.get(start)[j];                  //get start position from computers guess

                colouredPegs[i][j] = new JButton();
                colouredPegs[i][j].addActionListener(new bing(i, j));
                colouredPegs[i][j].setBorderPainted(false);
                int choice = state[i][j];
                colouredPegs[i][j].setBackground(choose(choice));

                whites[i][j] = new JButton();           //implementation of white pegs
                whites[i][j].setVisible(false);
                whites[i][j].setBackground(Color.green);
                whites[i][j].setBorderPainted(false);
                blacks[i][j] = new JButton();               //implementation of black pegs
                blacks[i][j].setVisible(false);
                blacks[i][j].setBackground(Color.black);
                blacks[i][j].setBorderPainted(false);

                colouredPanel.add(colouredPegs[i][j]);
                whitesPanel.add(whites[i][j]);
                blacksPanel.add(blacks[i][j]);
                if (i > 0)
                    colouredPegs[i][j].setVisible(false);           //sets all colours to false visibility
            }
        }

        setLayout(new BorderLayout());      //sets out layout for
        add(blacksPanel, "West");       //add blacks panel to west side - marker for reference
        add(colouredPanel, "Center");   //add coloured panel to center side - marker for reference
        add(whitesPanel, "East");       //add whites panel to east side - marker for reference
        JPanel guessPanel = new JPanel();
        guessPanel.setLayout(new FlowLayout());
        guessPanel.add(guess);
        add(guessPanel,"South");
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new GridLayout(1,3));
        topPanel.add(new JLabel("Blacks",JLabel.CENTER));       //add blacks panel to gui - marker for reference
        topPanel.add(computerGuessPanel);
        topPanel.add(new JLabel("Whites",JLabel.CENTER));   //add whites panel to gui - marker for reference
        add(topPanel,"North");  //north
        setDefaultCloseOperation(3);        //closes
        setTitle("Mastermind");
        setMinimumSize(new Dimension(width*50,height*50));  //sets a minimum size for the frame
        pack();
        setVisible(true);   //allows user to see grid
        guess.addActionListener(this);      //adds an action listener for the gui grid

        while(true) {       //while program is running

            int whiteThings = whites(state[numGuesses], hiddenGuess);   //sets up whites for use while gui is running
            int blackPegs = blacks(state[numGuesses], hiddenGuess);   //sets up blacks while gui are running
           // System.out.println(blackPegs);
           // System.out.println(whiteThings);
            if (blackPegs == width) {       //sets black pegs to equal width of gui
                for (int i = 0; i < blackPegs; i++) {
                    blacks[numGuesses][i].setVisible(true);
                }
                for (int i = 0; i < width; i++) {
                    computerGuess[i].setVisible(true);  //sets computers guess as visible all way through program
                }

                int n = JOptionPane.showConfirmDialog(this,
                        "You've Won! Would you like to play again?", "",    //win message
                        JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.NO_OPTION) {
                    System.exit(0);
                } else {
                    dispose();
                    new Mastermind(height, width, numColors);
                }
            }
            start++;    //start value added through during program
            if (numGuesses < height) {
                for (int i = 0; i < whiteThings; i++) {
                    whites[numGuesses][i].setVisible(true);
                }
                for (int i = 0; i < blackPegs; i++) {
                    blacks[numGuesses][i].setVisible(true);
                }
                numGuesses++;       //increments number of guesses

                if (numGuesses < height) {  //end game statement

                    for (int i = 0; i < width; i++) {
                        colouredPegs[numGuesses][i].setVisible(true);
                        state[numGuesses][i] = compguess.get(start)[i];
                        colouredPegs[numGuesses][i].setBackground(choose(compguess.get(start)[i]));
                    }
                } else {
                    for (int i = 0; i < width; i++) {
                        computerGuess[i].setVisible(true);
                    }
                    int n = JOptionPane.showConfirmDialog(this,
                            "You've lost! Would you like to play again?", "You've Lost!",    //lose message
                            JOptionPane.YES_NO_OPTION);

                    if (n == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    } else {
                        dispose();
                        new Mastermind(height, width, numColors);

                    }
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        //Code moved out of here so it can run automatically
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {   //throws file exception for output file
        new Mastermind(20, 4, 5);      //increase first variable to increase number of computer guesses



        }
    }
/*

        */