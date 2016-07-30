import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;



public class SearchPanel extends JPanel{
	JTextField content;
	JButton search;
	public SearchPanel(final SearchSession searchSession){
		content = new JTextField(40);
		content.setPreferredSize(new Dimension(446, 28));
		
		search = new JButton("search");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = content.getText();
				if (query == null || query.length() == 0) {
					return;
				}
							
				ArrayList<String> docs = searchSession.getSearchResults(query);
				
				content.setText("");
				
				if(docs==null||docs.size()==0){  //没有结果的情况
					
					return;
				}
				
				MainUI.mainUI.showResult(docs);
								
			}
		});
		
	
		add(content);
		add(search);
	}
	
	
}


