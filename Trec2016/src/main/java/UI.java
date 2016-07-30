import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 */

/**
 *
 * @since 2016年4月25日 下午5:24:43
 * @version 1.0
 */
public class UI extends JPanel{
	
	SearchSession searchSession = new SearchSession();
	
	JTextField textField = new JTextField();
	JButton searchBtn = new JButton("Search");
	ArrayList<JLabel> results = new ArrayList<JLabel>();
	
	public static void main(String[]args) {
		new UI();
	}
	
	public UI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initComponents();
		JFrame frame = new JFrame("TREC");
		frame.setSize(800, 600);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void initComponents() {
		this.add(textField);
		this.add(searchBtn);
		searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = textField.getText();
				if (query == null || query.length() == 0) {
					return;
				}
				
				removeAll();
				revalidate();
				repaint();
				
				ArrayList<String> docs = searchSession.getSearchResults(query);
				
				if(docs==null||docs.size()==0){  //没有结果的情况
					JLabel jl = new JLabel("no result");
					add(jl);
					return;
				}
				
				ArrayList<String> paths = new DBReader3().getPaths(docs);
				
				for (int i=0;i<docs.size();i++) {
					JLabel jl = new JLabel(docs.get(i));
					jl.addMouseListener(new MyAdapter(paths.get(i)));
					add(jl);
				}
				
			}
		});
		
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
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.setVisible(true);
			
		}
	}

}
