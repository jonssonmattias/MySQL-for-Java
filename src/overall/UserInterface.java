package overall;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.JTextField;

public class UserInterface {

	private JFrame frame;
	private final JButton button1 = new JButton("SELECT");
	private final JButton button2 = new JButton("INSERT");
	private final JButton button3 = new JButton("UPDATE");
	private final JButton button4 = new JButton("DELETE");
	private final JPanel mainPanel = new JPanel();
	private final JTable table = new JTable();
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserInterface window = new UserInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel menu = new JPanel();
		frame.getContentPane().add(menu, BorderLayout.WEST);
		menu.setLayout(new GridLayout(4, 1, 0, 0));
		menu.add(button1);
		menu.add(button2);
		menu.add(button3);
		menu.add(button4);
		mainPanel.setBackground(Color.YELLOW);
		mainPanel.setVisible(true);
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		table.setSize(new Dimension(mainPanel.getHeight(), mainPanel.getWidth()));
		mainPanel.add(table);
		
		
	}

}
