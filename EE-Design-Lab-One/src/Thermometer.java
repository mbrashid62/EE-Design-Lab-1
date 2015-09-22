import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.SwingConstants;

import com.twilio.sdk.TwilioRestException;

import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;

public class Thermometer {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private String targetNumber;
	private double minTemp;
	private double maxTemp;
	private boolean onGoing;
	private boolean isCelsius;
//	private boolean hasGraphBeenInit;
	private Texter Alert;
	private final String defaultNumber = "+13198559324";
	private SerialComm communication;
	private boolean connection;
	private JTextArea textArea;
	private GoThread collector;

	//private Graph tempGraph;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Thermometer window = new Thermometer();
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
	public Thermometer() {
		initializeGui();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGui() {
		JOptionPane.showMessageDialog(null, "Default tartget number: +13198559324\nDefault Min Temperature: -10.0 C \u00BA\nDefault Max Temperature: 63.0 C \u00BA");
		isCelsius = true;
	//	hasGraphBeenInit = false;
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(102, 204, 204));
		frame.getContentPane().setForeground(new Color(0, 0, 0));
		frame.setBounds(100, 100, 387, 376);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblTextMessageAlert = new JLabel("Text Message Alert");
		lblTextMessageAlert.setBounds(0, 0, 347, 22);
		lblTextMessageAlert.setHorizontalAlignment(SwingConstants.CENTER);
		lblTextMessageAlert.setFont(new Font("Tahoma", Font.BOLD, 15));
		frame.getContentPane().add(lblTextMessageAlert);

		JLabel label = new JLabel("Target Number:");
		label.setBounds(10, 36, 101, 15);
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setFont(new Font("Tahoma", Font.ITALIC, 12));
		label.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(label);

		textField = new JTextField();
		textField.setBounds(107, 33, 133, 22);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		minTemp = -10;
		maxTemp = 63;
		try {
			Alert = new Texter(defaultNumber);
		} catch (TwilioRestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JButton btnSave = new JButton("Save");
		btnSave.setBackground(new Color(153, 255, 255));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (textField.getText().matches("[0-9]+") && textField.getText().length() > 2) {
					targetNumber = textField.getText();
					try {
						Alert = new Texter(targetNumber);
						JOptionPane.showMessageDialog(null, "New Target number: +1"+targetNumber);
					} catch (TwilioRestException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Tartget number must contains integers only !");
				}
			}
		});
		btnSave.setBounds(270, 33, 77, 23);
		frame.getContentPane().add(btnSave);

		JLabel lblNewLabel = new JLabel("Real-Time Temperature");
		lblNewLabel.setBounds(84, 130, 186, 14);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Temperature Settings");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(101, 62, 159, 22);
		frame.getContentPane().add(lblNewLabel_1);

		JLabel lblMax = new JLabel("Max:");
		lblMax.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblMax.setBounds(30, 99, 34, 14);
		frame.getContentPane().add(lblMax);

		textField_1 = new JTextField();
		textField_1.setBounds(62, 95, 60, 24);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Min:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_2.setBounds(143, 99, 34, 15);
		frame.getContentPane().add(lblNewLabel_2);

		textField_2 = new JTextField();
		textField_2.setBounds(177, 95, 60, 24);
		frame.getContentPane().add(textField_2);
		textField_2.setColumns(10);
        
		communication = new SerialComm();
		
		  try { 
			  connection = communication.initialize(); //display graph of old data 
		  
			  if(!connection) { 
				  JOptionPane.showMessageDialog(null,
				  "Serial Communication offline! re-connecting..."); }
		  
			  while(!connection) { 
				  connection = communication.initialize(); 
				  }
			  JOptionPane.showMessageDialog(null, "Serial Communication online!");
		  } 
		  catch (Exception e) { // TODO Auto-generated catch block
		  e.printStackTrace(); }
		 

		JButton btnNewButton = new JButton("Save");
		btnNewButton.setBackground(new Color(153, 255, 255));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double a = Double.parseDouble(textField_1.getText());
				double b = Double.parseDouble(textField_2.getText());
				if (a <= b) {
					JOptionPane.showMessageDialog(null, "Max Temperature must be bigger than Min Temperature!");
				} else {
					if (a > 63 || b < -10) {
						JOptionPane.showMessageDialog(null,
								"Proper temperature limit should be within -10 ~ 63 degree Cesius!");
					} else {
						maxTemp = a;
						minTemp = b;
						JOptionPane.showMessageDialog(null, "Temperature Alert Settings Updated!\nMin Temperature: "+minTemp+"\nMax Temperature: "+maxTemp);
					}
				}

			}
		});
		btnNewButton.setBounds(270, 96, 77, 23);
		frame.getContentPane().add(btnNewButton);

		ButtonGroup group = new ButtonGroup();

		JRadioButton rdbtnC = new JRadioButton("C \u00BA");
		rdbtnC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isCelsius = true;
	//			tempGraph.setAxisCelsius();

			}
		});
		rdbtnC.setBackground(new Color(102, 204, 204));
		rdbtnC.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtnC.setBounds(315, 192, 46, 23);
		rdbtnC.setSelected(true);
		frame.getContentPane().add(rdbtnC);

		JRadioButton rdbtnF = new JRadioButton("F \u00BA");
		rdbtnF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isCelsius = false;
	//			tempGraph.setAxisFarenheit();
			}
		});
		rdbtnF.setBackground(new Color(102, 204, 204));
		rdbtnF.setFont(new Font("Tahoma", Font.PLAIN, 13));
		rdbtnF.setBounds(315, 239, 46, 23);
		frame.getContentPane().add(rdbtnF);

		group.add(rdbtnC);
		group.add(rdbtnF);

		JToggleButton tglbtnNewToggleButton = new JToggleButton("LED");
		tglbtnNewToggleButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange() == ItemEvent.SELECTED) {
					communication.sendData('L');
				} else if (ev.getStateChange() == ItemEvent.DESELECTED) {
					communication.sendData('O');
				}
			}

		});
		tglbtnNewToggleButton.setBackground(new Color(255, 255, 255));
		tglbtnNewToggleButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		tglbtnNewToggleButton.setForeground(new Color(102, 0, 102));
		tglbtnNewToggleButton.setBounds(298, 295, 63, 31);
		frame.getContentPane().add(tglbtnNewToggleButton);

		JPanel panel = new JPanel();
		panel.setBounds(10, 155, 299, 135);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(0, 0, 299, 134);
		panel.add(textArea);
		textArea.setBackground(new Color(204, 255, 153));
		textArea.setForeground(new Color(51, 102, 204));
		textArea.setFont(new Font("Comic Sans MS", Font.ITALIC, 94));

		JButton btnNewButton_1 = new JButton("Go");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onGoing = true;
				collector = new GoThread();
				collector.start();
			/*	if (!hasGraphBeenInit) {
					tempGraph = new Graph();
					hasGraphBeenInit = true;
				//	System.out.println("In first if");
				}
				if (hasGraphBeenInit == true) {

				//	System.out.println("In here");*/

					
				}

			//}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_1.setBackground(new Color(153, 255, 255));
		btnNewButton_1.setForeground(new Color(0, 153, 51));
		btnNewButton_1.setBounds(30, 301, 89, 23);
		frame.getContentPane().add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("Stop");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onGoing = false;
//				tempGraph.stopCollector();
				collector = null;
				textArea.setText("");
			}
		});
		btnNewButton_2.setBackground(new Color(153, 255, 255));
		btnNewButton_2.setForeground(new Color(255, 51, 51));
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_2.setBounds(181, 301, 89, 23);
		frame.getContentPane().add(btnNewButton_2);

	}

	public double CtoF(double C) {
		double F = 0;
		F = 9 * C / 5 + 32;
		return F;
	}
	
	private class GoThread extends Thread {
		
		public GoThread() {
		}
		
		public void run() {
			while (onGoing) {

				if (!connection) {
					JOptionPane.showMessageDialog(null, "Serial Communication offline! re-connecting...");
				}

				while (!connection) {
					connection = communication.initialize();
				}
				
				communication.sendData('T');
				String InputReading = "";
				 
	            InputReading = communication.getTemperature();        
	            
				communication.dataReset();   
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*** get temp from arduino **/
				double currentTemp = Double.parseDouble(InputReading);
				
//						tempGraph.sendData(currentTemp);
				if(!Alert.isAlerted()) {
					if (currentTemp < minTemp) {
						Alert.LowTempAlert();
					} else if (currentTemp > maxTemp) {
						Alert.HighTempAlert();
					}
				} else {
					if(currentTemp >= minTemp&&currentTemp <= maxTemp) {
	                	Alert.Reset();
	                }
				}
				
				if (isCelsius) {
					textArea.setText("" + currentTemp);
				} else {
					textArea.setText("" + CtoF(currentTemp));
				}			        
			}
			textArea.setText("");
		}
		
	}
	

}
