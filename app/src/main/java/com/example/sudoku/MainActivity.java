package com.example.sudoku;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    View view;
    int i=0, sampleSize=0, solved=0, solvedInOnePass=0, counter=1, numEmptyAtStart=0, cellResults=0, temp=0, tempRow=0, tempCol=0;
    String possibleResults="", displayString="";
    Button setButton,solveButton,easyButton, mediumButton, clearCells;
    ArrayList<Integer> indexOfEmpty = new ArrayList<>();
    ArrayList<String> sudokuBoard = new ArrayList<>();
    ArrayList<String> sudokuBoard2 = new ArrayList<>();
    EditText[] editTexts = new EditText[81];
    TextView displayText, timerDisplay;
    long startTime=0,currentTime=0, beforeguessTime=0,previousTime=0,basicPass=0,basicStart=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setButton = findViewById(R.id.setButton);
        solveButton = findViewById(R.id.solveButton);
        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        clearCells = findViewById(R.id.clearCells);
        displayText = findViewById(R.id.displayText);
        displayText.setMovementMethod(new ScrollingMovementMethod());
        timerDisplay = findViewById(R.id.timerDisplay);

        //initialize EditText variables
        for (int u = 0; u < 81; u++) {
            String editTextID = "EditText" + (u + 1);
            int resID = getResources().getIdentifier(editTextID, "id", getPackageName());
            editTexts[u] = (findViewById(resID));
            editTexts[u].setText("");
        }

        clearCells.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearCells();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                set(v);
            }
        });
        solveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                solve(v);
            }
        });
        easyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numEmptyAtStart = 0;
                for (int j = 0; j < 81; j++) {
                    if (editTexts[j].getText().toString().length() != 0)
                        editTexts[j].setText("");
                    else
                        numEmptyAtStart++;
                }
                editTexts[2].setText("6");
                editTexts[5].setText("8");
                editTexts[7].setText("9");
                editTexts[8].setText("5");
                editTexts[12].setText("2");
                editTexts[17].setText("7");
                editTexts[18].setText("5");
                editTexts[23].setText("1");
                editTexts[25].setText("3");
                editTexts[28].setText("1");
                editTexts[31].setText("3");
                editTexts[35].setText("9");
                editTexts[36].setText("6");
                editTexts[38].setText("5");
                editTexts[39].setText("1");
                editTexts[41].setText("7");
                editTexts[42].setText("4");
                editTexts[44].setText("8");
                editTexts[45].setText("9");
                editTexts[49].setText("4");
                editTexts[52].setText("1");
                editTexts[55].setText("5");
                editTexts[57].setText("7");
                editTexts[62].setText("3");
                editTexts[63].setText("7");
                editTexts[68].setText("9");
                editTexts[72].setText("4");
                editTexts[73].setText("6");
                editTexts[75].setText("3");
                editTexts[78].setText("7");
                for (int k = 0; k < 81; k++) {
                    sudokuBoard.add(editTexts[k].getText().toString());

                    if (editTexts[k].getText().toString().length() == 0)
                        numEmptyAtStart++;
                }
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numEmptyAtStart = 0;
                for (int j = 0; j < 81; j++) {
                    if (editTexts[j].getText().toString().length() != 0)
                        editTexts[j].setText("");
                    else
                        numEmptyAtStart++;
                }
                editTexts[2].setText("5");
                editTexts[5].setText("9");
                editTexts[8].setText("7");
                editTexts[9].setText("1");
                editTexts[11].setText("4");
                editTexts[14].setText("8");
                editTexts[17].setText("5");
                editTexts[18].setText("9");
                editTexts[21].setText("4");
                editTexts[22].setText("6");
                editTexts[28].setText("1");
                editTexts[34].setText("3");
                editTexts[36].setText("4");
                editTexts[38].setText("3");
                editTexts[42].setText("9");
                editTexts[44].setText("1");
                editTexts[52].setText("4");
                editTexts[58].setText("5");
                editTexts[59].setText("4");
                editTexts[62].setText("9");
                editTexts[63].setText("5");
                editTexts[66].setText("2");
                editTexts[69].setText("1");
                editTexts[71].setText("4");
                editTexts[72].setText("7");
                editTexts[75].setText("1");
                editTexts[78].setText("3");
                for (int k = 0; k < 81; k++) {
                    sudokuBoard.add(editTexts[k].getText().toString());
                    if (editTexts[k].getText().toString().length() == 0)
                        numEmptyAtStart++;
                }
            }
        });

    }

    public void solve(View view) {
        int solvedSoFar=0;
        displayString="";
        counter=0;
        displayText.setText("");
        String timerText="";
        startTime = System.currentTimeMillis();
        previousTime = startTime;
        solved = 0;
        do{
            solveAllCells();
            set2(view);
            solvedInOnePass = solved;
            counter++;
            solvedSoFar += solved;
            displayString+="Pass number "+counter+" has solved "+solvedInOnePass+" cells.\t"+solvedSoFar+"/"+numEmptyAtStart+"\t"+(System.currentTimeMillis() - previousTime)+" ms\nBasic "+basicPass+" ms\n";
            displayText.setText(displayString);
            previousTime = System.currentTimeMillis();
        }
        while(solved!=0);

//backup sudokuBoard in sudokuBoard2
        sudokuBoard2.clear();
        sudokuBoard2.addAll(sudokuBoard);

        if(indexOfEmpty.size()!=0) {
            beforeguessTime = System.currentTimeMillis() - startTime;
            timerText = "before guess : "+beforeguessTime+" ms";

            for (int i = 0; i < indexOfEmpty.size(); i++) {
                solveOneCell(indexOfEmpty.get(i));
                temp = indexOfEmpty.get(i);

                for (int u = 0; u < possibleResults.length(); u++) {
                    sudokuBoard.set(temp, possibleResults.substring(u, u + 1));
                    solved = 0;
                    do {
                        solveAllCells();
                        set2(view);
                        solvedInOnePass = solved;
                        counter++;
                        solvedSoFar += solved;
                        displayString += "Pass number " + counter + " has solved " + solvedInOnePass + " cells.\t" + solvedSoFar + "/" + numEmptyAtStart + "\t" + (System.currentTimeMillis() - previousTime)+" ms\nBasic "+basicPass+" ms\n";
                        displayText.setText(displayString);
                        previousTime = System.currentTimeMillis();
                    }
                    while (solved != 0);
                    if (indexOfEmpty.size() == 0 || u == possibleResults.length()-1) {
                        break;
                    } else {
                        sudokuBoard.clear();
                        sudokuBoard.addAll(sudokuBoard2);
                        set2(view);
                    }
                }
            }
        }
//set the grid to sudokuBoard
        for(int u=0;u<81;u++) {
            editTexts[u].setText(sudokuBoard.get(u));
        }

        if(indexOfEmpty.size()==0)
            Toast.makeText(this, "solved", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "time for brute force", Toast.LENGTH_SHORT).show();
        currentTime = System.currentTimeMillis() - startTime;
        timerDisplay.setText(timerText+"\ntotal time: "+currentTime+" ms");
    }

    public void set(View view) {
        sampleSize = 0;
        indexOfEmpty.clear();
        sudokuBoard.clear();
        sudokuBoard2.clear();
        for (i = 0; i < 81; i++) {
            sudokuBoard.add(editTexts[i].getText().toString());
            if (editTexts[i].getText().toString().equals(null) || editTexts[i].getText().toString().equals("")) {
                editTexts[i].getBackground().setColorFilter(null);
                sampleSize++;
                indexOfEmpty.add(i);
            } else {
                editTexts[i].getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP);
            }
        }
        numEmptyAtStart = indexOfEmpty.size();
    }

    public void set2(View view) {
        sampleSize = 0;
        indexOfEmpty.clear();
        for (i = 0; i < 81; i++) {
            if (sudokuBoard.get(i).length()==0) {
                sampleSize++;
                indexOfEmpty.add(i);
            }
        }
    }

    public String solveOneCell(int index) {
        String row="",col="",grid="";
        int gridNumber, tempIndex;
        possibleResults="";
        tempRow = (index/9)*9;

        //get row string
        for(int u=tempRow;u<tempRow+9;u++){
            row += sudokuBoard.get(u);
        }

        //get col string
        tempCol = index%9;//find column

        tempIndex = tempCol;

        for (int y = 0; y < 9; y++) {
            col += sudokuBoard.get(tempIndex);
            tempIndex=tempIndex+9;
        }

        //get 3x3 string
        gridNumber = getGrid(index);
        grid = getGridString(gridNumber);

        for(int u=1;u<=9;u++) {
            if(!row.contains(Integer.toString(u)) && !col.contains(Integer.toString(u)) && !grid.contains(Integer.toString(u))) {
                possibleResults+=u;
            }
        }
        return possibleResults;
    }

    public void solveAllCells() {
        solved=0;
        boolean firstBasic = true;
        for(int u=0;u<81;u++) {
            if(sudokuBoard.get(u).length()==0) {
                solveOneCell(u);
                if (possibleResults.length() == 1) {
                    if(firstBasic) {
                        basicStart = System.currentTimeMillis();
                        firstBasic = false;
                    }
                    sudokuBoard.set(u, possibleResults);
                    set2(view);
                    solved++;
                }
                else if(possibleResults.length() > 1)
                {
                    cellResults = Integer.parseInt(possibleResults);
                    elimRow(u, Integer.toString(cellResults));
                    if(possibleResults.length()>1) {
                        elimCol(u, Integer.toString(cellResults));
                        if (possibleResults.length()>1) {
                            elimGrid(u, Integer.toString(cellResults));
                        }
                    }
                }
            }
        }
        basicPass = System.currentTimeMillis()-basicStart;
        //updates number of cells solved, used for deciding to loop again.
    }

    public int getGrid(int index) {//returns grid number 1-9
        int gridNumber=1;
        //grid 1,4,7
        if(index%9==0 || index%9==1 || index%9==2) {
            if(index<21) {
                gridNumber=1;
            }
            else if(index<48) {
                gridNumber=4;
            }
            else if(index<75) {
                gridNumber=7;
            }
            return gridNumber;
        }

        //grid 2,5,8
        else if(index%9==3 || index%9==4 || index%9==5) {
            if(index<24) {
                gridNumber=2;
            }
            else if(index<51) {
                gridNumber=5;
            }
            else if(index<78) {
                gridNumber=8;
            }
            return gridNumber;
        }

        //grid 3,6,9
        else if(index%9==6 || index%9==7 || index%9==8) {
            if(index<27) {
                gridNumber=3;
            }
            else if(index<54) {
                gridNumber=6;
            }
            else if(index<81) {
                gridNumber=9;
            }
            return gridNumber;
        }
        return gridNumber;
    }

    public String getGridString(int gridNumber) {//returns a string of all the possible results in the grid
        String gridString = "";
        int cell1 = 0;
        if (gridNumber == 1)
            cell1=0;
        else if(gridNumber==2)
            cell1=3;
        else if(gridNumber==3)
            cell1=6;
        else if(gridNumber==4)
            cell1=27;
        else if(gridNumber==5)
            cell1=30;
        else if(gridNumber==6)
            cell1=33;
        else if(gridNumber==7)
            cell1=54;
        else if(gridNumber==8)
            cell1=57;
        else if(gridNumber==9)
            cell1=60;

        gridString += sudokuBoard.get(cell1);
        gridString += sudokuBoard.get(cell1+1);
        gridString += sudokuBoard.get(cell1+2);
        gridString += sudokuBoard.get(cell1+9);
        gridString += sudokuBoard.get(cell1+10);
        gridString += sudokuBoard.get(cell1+11);
        gridString += sudokuBoard.get(cell1+18);
        gridString += sudokuBoard.get(cell1+19);
        gridString += sudokuBoard.get(cell1+20);
        return gridString;
    }

    public void elimRow(int index, String cellResults) {
        String rowSolutions="";
        for(int j=tempRow;j<tempRow+9;j++) {//get string of possible results in row
            if(j!=index) {
                if (sudokuBoard.get(j).length() == 0)
                    rowSolutions += solveOneCell(j);
            }
        }
        for(int u=0;u<cellResults.length();u++) {
            if(!rowSolutions.contains(cellResults.substring(u,u+1))) {
                possibleResults=(Integer.toString(u));
            }
        }
    }

    public void elimCol(int index, String cellResults) {
        String colSolutions="";
        int tempIndex;
        tempIndex = tempCol;

        for (int y = 0; y < 9; y++) {//get string of possible results in column
            if(index!=tempIndex)
                colSolutions += solveOneCell(tempIndex);
            tempIndex=tempIndex+9;
        }

        for(int u=0;u<cellResults.length();u++) {
            if(!colSolutions.contains(cellResults.substring(u,u+1))) {
                possibleResults=(Integer.toString(u));
            }
        }
    }

    public void elimGrid(int index, String cellResults) {
        int gridNumber, cell1=0;
        String gridString="";
        gridNumber = getGrid(index);
        if (gridNumber == 1)//get string of possible results in grid
            cell1=0;
        else if(gridNumber == 2)
            cell1=3;
        else if(gridNumber == 3)
            cell1=6;
        else if(gridNumber == 4)
            cell1=27;
        else if(gridNumber == 5)
            cell1=30;
        else if(gridNumber == 6)
            cell1=33;
        else if(gridNumber == 7)
            cell1=54;
        else if(gridNumber == 8)
            cell1=57;
        else if(gridNumber == 9)
            cell1=60;

        if(cell1!=index)
            gridString += solveOneCell(cell1);
        if(cell1+1!=index)
            gridString += solveOneCell(cell1+1);
        if(cell1+2!=index)
            gridString += solveOneCell(cell1+2);
        if(cell1+9!=index)
            gridString += solveOneCell(cell1+9);
        if(cell1+10!=index)
            gridString += solveOneCell(cell1+10);
        if(cell1+11!=index)
            gridString += solveOneCell(cell1+11);
        if(cell1+18!=index)
            gridString += solveOneCell(cell1+18);
        if(cell1+19!=index)
            gridString += solveOneCell(cell1+19);
        if(cell1+20!=index)
            gridString += solveOneCell(cell1+20);

        for(int u=0;u<cellResults.length();u++) {
            if(!gridString.contains(cellResults.substring(u,u+1))) {
                possibleResults=(Integer.toString(u));
            }
        }
    }

    public void clearCells() {
        for(int k=0;k<81;k++) {
            editTexts[k].setText("");
            sudokuBoard.clear();
            sudokuBoard2.clear();
        }
    }


}
