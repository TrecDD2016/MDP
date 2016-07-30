import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ResultPanel extends JPanel{
	public ResultPanel(){
		setLayout(null);
		setVisible(true);
	}
	
	public void showResult(ArrayList<String> docs){
		removeAll();
		ArrayList<String> paths = new DBReader3().getPaths(docs);
		for (int i=0;i<docs.size();i++) {
			JLabel jl = new JLabel(docs.get(i));
			jl.setBounds(300, 30*i, 400, 25);;
			jl.addMouseListener(new MyAdapter(paths.get(i)));
			add(jl);
		}

		this.repaint();
	}
}
class MyAdapter extends MouseAdapter{
	private String path;
	public MyAdapter(String path) {
		this.path = path;
	}
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		new MyFrame(path);
	}
	
}

class MyFrame extends JFrame{
	/**
	 * 
	 */
	public MyFrame(String path) {
		this.setSize(800, 600);
		TextArea ta = new TextArea();
		this.add(ta);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(path)));
			String s;
			while((s = br.readLine()) != null) {
				ta.append(s);
				ta.append("\r\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
}