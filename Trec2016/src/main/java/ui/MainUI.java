package ui;

import search.SearchSession;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class MainUI extends JFrame{
	public static MainUI mainUI;
	JPanel searchPanel;
	SearchSession searchSession;
	ResultPanel resultPanel;
	public MainUI(){
		super("TREC");
		
		resultPanel = new ResultPanel();
		add(resultPanel,BorderLayout.CENTER);
		
		searchSession = new SearchSession();
		searchPanel = new SearchPanel(searchSession);
		add(searchPanel,BorderLayout.NORTH);
		
		
		
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);      
		setVisible(true);
		mainUI=this;
	}
	
	public void showResult(ArrayList<String> docs){
		resultPanel.showResult(docs);
	  
		this.repaint();
	}
	
	public static void main(String[] args){
		new MainUI();
	}
	
	
}
