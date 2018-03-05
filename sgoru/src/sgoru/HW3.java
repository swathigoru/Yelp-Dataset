package sgoru;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;




public class HW3 {

	private JFrame myYelpFrame;
	private JList<String> mainCats, attributesList, subCats;
	private JTable businessTable;
	private DefaultTableModel businessModel, model;
	private JComboBox day, openTime, closeTime, location, searchfor;
	private JButton searchBtn, clearBtn;
	private JLabel dayLabel, openLabel, closeLabel, locLabel, catLabel, subCatLabel, attribLabel, busLabel, searchLabel ;
	private String subCatListStr, attribListStr, catListStr;
	private String qBusinessFromCat, queryForAttributes, queryForBusiness;
	private DefaultListModel attributesModel;
	
	Connection dbConn = null;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HW3 window = new HW3();
					window.myYelpFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public HW3() {
		try {
			this.dbConn = dbOpen();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startApp(this.dbConn);
	}
	
	public class CheckboxListCellRenderer extends JCheckBox implements ListCellRenderer {

	    public Component getListCellRendererComponent(JList list, Object value, int index, 
	            boolean isSelected, boolean cellHasFocus) {

	        setComponentOrientation(list.getComponentOrientation());
	        setFont(list.getFont());
	        setBackground(list.getBackground());
	        setForeground(list.getForeground());
	        setSelected(isSelected);
	        setEnabled(list.isEnabled());

	        setText(value == null ? "" : value.toString());  

	        return this;
	    }
	}
    /** 
     * @return a database connection 
     * @throws java.sql.SQLException when there is an error when trying to connect database 
     * @throws ClassNotFoundException when the database driver is not found. 
     */ 
    private static Connection dbOpen() throws SQLException, ClassNotFoundException { 
        // Load the Oracle database driver 
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 

        String host = "localhost"; 
        String port = "1521"; 
        String dbName = "orcl"; 
        String userName = "sgoru"; 
        String password = "sgoru"; 
 
        // Construct the JDBC URL 
        String dbURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName; 
        return DriverManager.getConnection(dbURL, userName, password); 
    } 
 
    /** 
     * Close the database connection 
     * @param con 
     */ 
    private static void dbClose(Connection con) { 
        try { 
            con.close(); 
        } catch (SQLException e) { 
            System.err.println("Unable to close connection: " + e.getMessage()); 
        } 
    }
    
	/**
	 * Initialize the contents of the frame.
	 */
	private void startApp(Connection dbConn) {
		myYelpFrame = new JFrame();
		myYelpFrame.setBounds(150, 150, 1600, 665);
		myYelpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myYelpFrame.getContentPane().setBackground(new Color(153, 204, 255));
		myYelpFrame.setTitle("MyYelp");
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		myYelpFrame.getContentPane().setLayout(gridBagLayout);

		JLabel txtpnUseCtrl = new JLabel();
		txtpnUseCtrl.setText(
				"Select multiple Categories and Subcategories:Use Ctrl + Click");
		GridBagConstraints c1 = new GridBagConstraints();
		c1.insets = new Insets(0, 0, 5, 0);
		c1.gridwidth = 4;
		c1.fill = GridBagConstraints.BOTH;
		c1.gridx = 2;
		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.gridy = 0;
		myYelpFrame.getContentPane().add(txtpnUseCtrl, c1);

		catLabel = new JLabel("Categories");
		catLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridcatLabel = new GridBagConstraints();
		gridcatLabel.insets = new Insets(0, 0, 5, 5);
		gridcatLabel.gridx = 1;
		gridcatLabel.gridy = 1;
		myYelpFrame.getContentPane().add(catLabel, gridcatLabel);

		subCatLabel = new JLabel("Sub Categories");
		subCatLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridsubCatLabel = new GridBagConstraints();
		gridsubCatLabel.insets = new Insets(0, 0, 5, 5);
		gridsubCatLabel.gridx = 2;
		gridsubCatLabel.gridy = 1;
		myYelpFrame.getContentPane().add(subCatLabel, gridsubCatLabel);

		attribLabel = new JLabel("Attributes");
		attribLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridattribLabel = new GridBagConstraints();
		gridattribLabel.insets = new Insets(0, 0, 5, 5);
		gridattribLabel.gridx = 3;
		gridattribLabel.gridy = 1;
		myYelpFrame.getContentPane().add(attribLabel, gridattribLabel);

		busLabel = new JLabel("Business");
		busLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridbusLabel = new GridBagConstraints();
		gridbusLabel.insets = new Insets(0, 0, 5, 0);
		gridbusLabel.gridx = 4;
		gridbusLabel.gridy = 1;
		myYelpFrame.getContentPane().add(busLabel, gridbusLabel);

		mainCats = new JList<String>();
		mainCats.setCellRenderer(new CheckboxListCellRenderer());
		GridBagConstraints c2 = new GridBagConstraints();
		c2.insets = new Insets(0, 0, 5, 5);
		c2.weightx = 0.2;
		c2.fill = GridBagConstraints.BOTH;
		c2.gridx = 1;
		c2.gridy = 2;
		myYelpFrame.getContentPane().add(new JScrollPane(mainCats), c2);

		subCats = new JList<String>();
		subCats.setCellRenderer(new CheckboxListCellRenderer());
		GridBagConstraints c3 = new GridBagConstraints();
		c3.insets = new Insets(0, 0, 5, 5);
		c3.weightx = 0.2;
		c3.fill = GridBagConstraints.BOTH;
		c3.gridx = 2;
		c3.gridy = 2;
		myYelpFrame.getContentPane().add(new JScrollPane(subCats), c3);

		attributesList = new JList<String>();
		attributesList.setCellRenderer(new CheckboxListCellRenderer());
		GridBagConstraints c4 = new GridBagConstraints();
		c4.insets = new Insets(0, 0, 5, 5);
		c4.weightx = 0.2;
		c4.fill = GridBagConstraints.BOTH;
		c4.gridx = 3;
		c4.gridy = 2;
		myYelpFrame.getContentPane().add(new JScrollPane(attributesList), c4);

		businessModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		businessTable = new JTable();
		
		businessTable.setModel(businessModel);
		businessModel.addColumn("ID");
		businessModel.addColumn("Name");
		businessModel.addColumn("Location");
		businessModel.addColumn("Stars");
		businessModel.addColumn("Review_Count");
		businessModel.addColumn("Total_Checkins");
		
		
		businessTable.getColumnModel().getColumn(0).setWidth(0);
		businessTable.getColumnModel().getColumn(0).setMinWidth(0);
		businessTable.getColumnModel().getColumn(0).setMaxWidth(0);

		GridBagConstraints c5 = new GridBagConstraints();
		c5.insets = new Insets(0, 0, 5, 0);
		c5.weightx = 0.4;
		c5.fill = GridBagConstraints.BOTH;
		c5.gridx = 4;
		c5.gridy = 2;
		myYelpFrame.getContentPane().add(new JScrollPane(businessTable), c5);

    	dayLabel = new JLabel("Day Of The Week");
		dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints griddayLabel = new GridBagConstraints();
		griddayLabel.insets = new Insets(0, 0, 5, 5);
		griddayLabel.gridx = 1;
		griddayLabel.gridy = 3;
		myYelpFrame.getContentPane().add(dayLabel, griddayLabel);

		openLabel = new JLabel("Opens At");
		openLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridopenLabel = new GridBagConstraints();
		gridopenLabel.insets = new Insets(0, 0, 5, 5);
		gridopenLabel.gridx = 2;
		gridopenLabel.gridy = 3;
		myYelpFrame.getContentPane().add(openLabel, gridopenLabel);

		closeLabel = new JLabel("Closes At");
		closeLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridcloseLabel = new GridBagConstraints();
		gridcloseLabel.insets = new Insets(0, 0, 5, 5);
		gridcloseLabel.gridx = 3;
		gridcloseLabel.gridy = 3;
		myYelpFrame.getContentPane().add(closeLabel, gridcloseLabel);

		locLabel = new JLabel("Location");
		locLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridlocLabel = new GridBagConstraints();
		gridlocLabel.insets = new Insets(0, 0, 5, 5);
		gridlocLabel.gridx = 4;
		gridlocLabel.gridy = 3;
		myYelpFrame.getContentPane().add(locLabel, gridlocLabel);
		
		searchLabel = new JLabel("SearchFor:");
		searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
		GridBagConstraints gridsearchLabel = new GridBagConstraints();
		gridsearchLabel.insets = new Insets(0, 0, 5, 5);
		gridsearchLabel.gridx = 5;
		gridsearchLabel.gridy = 3;
		myYelpFrame.getContentPane().add(searchLabel, gridsearchLabel);
		
		day = new JComboBox();
		GridBagConstraints c6 = new GridBagConstraints();
		c6.insets = new Insets(0, 0, 5, 5);
		c6.fill = GridBagConstraints.HORIZONTAL;
		c6.gridx = 1;
		c6.gridy = 4;
		myYelpFrame.getContentPane().add(day, c6);

		openTime = new JComboBox();
		GridBagConstraints c7 = new GridBagConstraints();
		c7.insets = new Insets(0, 0, 5, 5);
		c7.fill = GridBagConstraints.HORIZONTAL;
		c7.gridx = 2;
		c7.gridy = 4;
		myYelpFrame.getContentPane().add(openTime, c7);

		closeTime = new JComboBox();
		GridBagConstraints c8 = new GridBagConstraints();
		c8.insets = new Insets(0, 0, 5, 5);
		c8.fill = GridBagConstraints.HORIZONTAL;
		c8.gridx = 3;
		c8.gridy = 4;
		myYelpFrame.getContentPane().add(closeTime, c8);

		location = new JComboBox();
		GridBagConstraints c9 = new GridBagConstraints();
		c9.insets = new Insets(0, 0, 5, 5);
		c9.fill = GridBagConstraints.HORIZONTAL;
		c9.gridx = 4;
		c9.gridy = 4;
		myYelpFrame.getContentPane().add(location, c9);
		
		searchfor = new JComboBox(new String[] {"ALL", "ANY"});
		GridBagConstraints c10 = new GridBagConstraints();
		c10.insets = new Insets(0, 0, 5, 5);
		c10.fill = GridBagConstraints.HORIZONTAL;
		c10.gridx = 5;
		c10.gridy = 4;
		myYelpFrame.getContentPane().add(searchfor, c10);
		searchfor.setSelectedIndex(0);
		
		searchBtn = new JButton("Search");
		GridBagConstraints gridsearchBtn = new GridBagConstraints();
		gridsearchBtn.gridheight = 3;
		gridsearchBtn.gridx = 2;
		gridsearchBtn.gridy = 5;
		myYelpFrame.getContentPane().add(searchBtn, gridsearchBtn);
		
		clearBtn = new JButton("Clear Business");
		GridBagConstraints gridclearBtn = new GridBagConstraints();
		gridclearBtn.gridheight = 3;
		gridclearBtn.gridx = 4;
		gridclearBtn.gridy = 5;
		myYelpFrame.getContentPane().add(clearBtn, gridclearBtn);
		
		loadCategories(dbConn);
		loadSubCategories(dbConn);
		loadAttributes(dbConn);
		loadReviewTable(dbConn);
		loadSearch(dbConn);
		loadClear(dbConn);
	}

		private void loadClear(Connection dbConn) {
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				businessModel.setRowCount(0);
				attributesList.removeAll();
				mainCats.removeAll();
				subCats.removeAll();
				day.setSelectedIndex(-1);
				openTime.setSelectedIndex(-1);
				closeTime.setSelectedIndex(-1);
				location.setSelectedIndex(-1);
				searchfor.setSelectedIndex(0);
			}
		});
	} 
	
	public ResultSet populateBusiness(String query, String allOrAny, Connection dbConn) {
		Statement st = null;
		
		try {
			st = dbConn.createStatement();
			String queryPrefix = "SELECT B.bid, B.bname, B.loc, B.bstars, B.breview_count, B.checkin_count FROM business B WHERE B.open_status = \'true\' AND B.bid IN((";
			StringBuffer busSB = new StringBuffer();
			busSB.append(queryPrefix);
			StringBuffer busHoursSB = new StringBuffer();
			String queryBusHoursPrefix = "SELECT DISTINCT H.bid FROM business_hours H WHERE ";
			boolean setBH = false;
			busHoursSB.append(queryBusHoursPrefix);
			if (day.getSelectedIndex() != -1) {
				busHoursSB.append("H.dayname = "+ "\'"+ day.getSelectedItem().toString()+"\'");
				setBH = true;
			}
			if (location.getSelectedIndex() != -1) {
				if (setBH){
					busHoursSB.append(" AND ");
				}
				busHoursSB.append("H.loc = "+ "\'"+ location.getSelectedItem().toString()+"\'");
				setBH = true;
			}
			
			if ((openTime.getSelectedIndex() != -1) && (closeTime.getSelectedIndex() != -1)) {
				busHoursSB.append("H.openhr >= "+ openTime.getSelectedItem().toString());
				busHoursSB.append(" AND ");
				busHoursSB.append("H.closehr >= "+ closeTime.getSelectedItem().toString());
				setBH = true;
			} else {
				if (openTime.getSelectedIndex() != -1) {
					if (setBH) {
						busHoursSB.append(" AND ");
					}
					busHoursSB.append("H.openhr <= "+ openTime.getSelectedItem().toString());
					setBH = true;
				}
				if(closeTime.getSelectedIndex() != -1) {
					if (setBH){
						busHoursSB.append(" AND ");
					}
					busHoursSB.append("H.closehr >= "+ closeTime.getSelectedItem().toString());
					setBH = true;
				}
			}
			if (setBH) {
				busSB.append(busHoursSB.toString()+") INTERSECT (");
			} 
				
			String delimit = new String();
			if (allOrAny.equals("ALL")) {
				delimit = " INTERSECT ";
			} else {
				delimit = " UNION ";
			}
			List<String> attrList = attributesList.getSelectedValuesList();

			StringBuffer catQuery = new StringBuffer();
			StringBuffer subCatQuery = new StringBuffer();
			String queryPostFix = " ORDER BY B.bname";
			
			List<String> catList = mainCats.getSelectedValuesList();
			int length = catList.size();
			for (int i = 0 ; i < length; i++) {
				catQuery.append("SELECT DISTINCT C" + i + ".bid FROM categories C"+i+" WHERE C"+i+".cat_name=\'"+catList.get(i)+"\'");
				if (i != (length-1)) {
					catQuery.append(delimit);
				}
			}	
			busSB.append(catQuery.toString() + ")");
			
			List<String> subCatList = subCats.getSelectedValuesList();
			if (subCatList.size() > 0) {
				length = subCatList.size();
				for (int i = 0 ; i < length; i++) {
					subCatQuery.append("SELECT DISTINCT C" + (i+length) + ".bid FROM categories C"+(i+length)+" WHERE C"+(i+length)+".subcat_name= \'"+subCatList.get(i)+"\'");
					if (i != (length-1)) {
						subCatQuery.append(delimit);
					}
				}
				busSB.append(" INTERSECT (" +subCatQuery.toString() + ")");
			}
			
			busSB.append(")" + queryPostFix);
			System.out.println("Populate:"+ busSB.toString());
			return st.executeQuery(busSB.toString());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private void loadSearch(Connection dbConn) {
		searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					
				ResultSet rs = populateBusiness(queryForBusiness, (String)searchfor.getSelectedItem(), dbConn);
				try {
					businessModel.setRowCount(0);
					while (rs.next()) {
						businessModel.addRow(new Object[] { rs.getString(1),
								rs.getString(2), rs.getString(3), rs.getInt(4),
								rs.getInt(5), rs.getInt(6) });
					}
					rs.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		    }
		});
	} 
	public ResultSet getBusinessInfo(String bid, Connection dbConn) {
		Statement st = null;
		try {
			st = dbConn.createStatement();
			String queryPrefix = "SELECT R.publish_date, U.user_name, R.text, R.stars, R.useful_votes  FROM reviews R, users U WHERE R.bid =";
			return st.executeQuery(queryPrefix+ "\'" + bid + "\' AND R.user_id = U.user_id" + " ORDER BY R.stars DESC");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void loadReviewTable(Connection dbConn) {
		businessTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evnt) {
				if (evnt.getClickCount() == 1) {
					String businessId = (String) businessModel.getValueAt(businessTable.getSelectedRow(), 0);

					model = new DefaultTableModel() {

						@Override
						public boolean isCellEditable(int row, int column) {
							return false;
						}
					};

					JTable reviews = new JTable(model);
					JFrame reviewFrame = new JFrame();
					reviewFrame.getContentPane().setLayout(new BorderLayout());
					reviewFrame.getContentPane().add(new JScrollPane(reviews));
					reviewFrame.pack();
					reviewFrame.setLocationRelativeTo(null);
					reviewFrame.setVisible(true);
					reviewFrame.getContentPane().setBackground(new Color(153, 204, 255));
					reviewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

					model.addColumn("Review Date");
					model.addColumn("Username");
					model.addColumn("Comment");
					model.addColumn("Star");
					model.addColumn("Useful Votes");

					model.setRowCount(0);
					ResultSet rs = getBusinessInfo(businessId, dbConn);
					try {
						while (rs.next()) {
							model.addRow(new Object[] { rs.getString(1),
							              rs.getString(2), rs.getString(3),
										  rs.getInt(4), rs.getInt(5) });
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			}
			
		});
	}

	public ResultSet getBusinessHours(String catSubcatQuery, String attributes, String allOrAny, Connection dbConn) {
		Statement st = null;
		try {
			st = dbConn.createStatement();
			String delimit = new String();
			if (allOrAny.equals("ALL")) {
				delimit = " INTERSECT ";
			} else {
				delimit = " UNION ";
			}
			String queryPrefix = "SELECT H.dayname, H.openhr, H.closehr, H.loc, H.bid FROM  business_hours H WHERE H.bid IN(( ";
			String queryPostfix = "))";
			StringBuffer query = new StringBuffer();
			String[] attribTokens = attributes.split(",");
			int length = attribTokens.length;
			for (int i = 0 ; i < length; i++) {
				query.append("SELECT DISTINCT A" + i + ".bid FROM battributes A"+i+" WHERE A"+i+".att_name= "+attribTokens[i]);
				if (i != (length-1)) {
					query.append(delimit);
				}
			}
			
			query.append(") INTERSECT (" + catSubcatQuery + queryPostfix );
			queryForBusiness = query.toString();
			System.out.println("Query:"+ queryPrefix +query.toString());
			return st.executeQuery(queryPrefix + query.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}

	private void loadAttributes(Connection dbConn) {
		attributesList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					List<String> selectedList = attributesList.getSelectedValuesList();
					businessModel.setRowCount(0);
					if (selectedList.size() > 0) {
						String selectedAttribListStr = "";
						for (String string : selectedList) {
							selectedAttribListStr += "'" + string + "',";
						}
						attribListStr = selectedAttribListStr = selectedAttribListStr.substring(0, selectedAttribListStr.length() - 1);

						ResultSet rs = getBusinessHours(queryForAttributes, attribListStr, (String)searchfor.getSelectedItem(), dbConn);
						ArrayList<String> daysList = new ArrayList<String>();
						ArrayList<Float> openHoursList = new ArrayList<Float>();
						ArrayList<Float> closeHoursList = new ArrayList<Float>();
						ArrayList<String> locationList = new ArrayList<String>();
						ArrayList<String> bidList = new ArrayList<String>();
						try {
							while (rs.next()) {
								if (!daysList.contains(rs.getString(1).toString())) {
									daysList.add(rs.getString(1).toString());
								}
								if (!openHoursList.contains(rs.getFloat(2))) {
									openHoursList.add(rs.getFloat(2));
								}
								if (!closeHoursList.contains(rs.getFloat(3))) {
									closeHoursList.add(rs.getFloat(3));
								}
								if (!locationList.contains(rs.getString(4).toString())) {
									locationList.add(rs.getString(4).toString());
								}
								if (!bidList.contains(rs.getString(5).toString())) {
									bidList.add(rs.getString(5).toString());
								}								
							}
	
							Collections.sort(daysList);
							Collections.sort(openHoursList);
							Collections.sort(closeHoursList);
							Collections.sort(locationList);
							DefaultComboBoxModel model10 = new DefaultComboBoxModel();
							Iterator<?> i = daysList.iterator();
					        while (i.hasNext()) {
					        	model10.addElement(i.next());
					        }
					        day.setModel(model10);
					        day.setSelectedIndex(-1);
					        
					        DefaultComboBoxModel model1 = new DefaultComboBoxModel();
					        i = openHoursList.iterator();
					        while (i.hasNext()) {
					        	model1.addElement(i.next());
					        }
					        openTime.setModel(model1);
					        openTime.setSelectedIndex(-1);
					        
					        DefaultComboBoxModel model2 = new DefaultComboBoxModel();
					        i = closeHoursList.iterator();
					        while (i.hasNext()) {
					        	model2.addElement(i.next());
					        }
					        closeTime.setModel(model2);
					        closeTime.setSelectedIndex(-1);
					        
					        DefaultComboBoxModel model4 = new DefaultComboBoxModel();
					        i = locationList.iterator();
					        while (i.hasNext()) {
					        	model4.addElement(i.next());
					        }
					        location.setModel(model4);
					        location.setSelectedIndex(-1);
					        
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							rs.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						attributesList.removeAll();
						businessModel.setRowCount(0);
					}
				}
			}
		});
	}
	
	public ResultSet getAttributes(String cat, String subcat, String allOrAny, Connection dbConn) {
		Statement st = null;
		 
		try {
			st = dbConn.createStatement();
			String queryPrefix = "SELECT DISTINCT A.att_name FROM battributes A WHERE A.bid IN(( ";
			String queryPostfix = ")) ORDER BY A.att_name";
			StringBuffer query = new StringBuffer();
			String delimit = new String();
			if (allOrAny.equals("ALL")) {
				delimit = " INTERSECT ";
			} else {
				delimit = " UNION ";
			}
			String[] catTokens = cat.split(",");
			int length = catTokens.length;
			for (int i = 0 ; i < length; i++) {
				query.append("SELECT DISTINCT C" + i + ".bid FROM categories C"+i+" WHERE C"+i+".cat_name= "+ catTokens[i]);
				if (i != (length-1)) {
					query.append(delimit);
				}
			}
			
			query.append(")");
			
			String[] subCatTokens = subcat.split(",");
			length = subCatTokens.length;
			if (length > 0) {
				query.append(" INTERSECT (");
			}
			for (int i = 0 ; i < length; i++) {
				query.append("SELECT DISTINCT C" + (i+length) + ".bid FROM categories C"+(i+length)+" WHERE C"+(i+length)+".subcat_name= "+subCatTokens[i]);
				if (i != (length-1)) {
					query.append(delimit);
				}
			}
			queryForAttributes = query.toString();
			String finalQuery = queryPrefix + query.toString() + queryPostfix;
			return st.executeQuery(finalQuery);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (st != null && st.isClosed()) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private void loadSubCategories(Connection dbConn) {
		subCats.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					List<String> selectedList = subCats.getSelectedValuesList();
					if (selectedList.size() > 0) {
						String setList = "";
						for (String string : selectedList) {
							setList += "'" + string + "',";
						}
						subCatListStr = setList = setList.substring(0, setList.length() - 1);
						ResultSet rs = getAttributes(catListStr, subCatListStr, (String)searchfor.getSelectedItem(), dbConn);
						attributesModel = new DefaultListModel();
						try {
							while (rs.next()) {
								attributesModel.addElement(rs.getString(1));
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							rs.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						attributesList.setModel(attributesModel);
						}
					}
				}
		});
	} 

	public ResultSet getSubCategories(String inClause, String allOrAny, Connection dbConn) {
		Statement st = null;
		try {
			st = dbConn.createStatement();
			String delimit = new String();
			if (allOrAny.equals("ALL")) {
				delimit = "INTERSECT ";
			} else {
				delimit = "UNION ";
			}
			String queryPrefix = "SELECT DISTINCT subcat_name FROM categories WHERE ";
			StringBuffer query = new StringBuffer();
			String queryPostFix = " ORDER BY subcat_name";
			String[] tokens = inClause.split(",");
			int length = tokens.length;
			if (length > 0) {
				query.append(queryPrefix + "cat_name = " + tokens[0]);
			}
			
			for (int i = 1 ; i < length; i++) {
				query.append(delimit + queryPrefix+ "cat_name = " + tokens[i] + " ");
			}
			query.append(queryPostFix);			
			return st.executeQuery(query.toString());
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (st != null && st.isClosed()) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private void loadCategories(Connection dbConn) {

		try {
	        Statement stmt = dbConn.createStatement();
	        DefaultListModel dlm = new DefaultListModel();
			ResultSet rs = stmt.executeQuery("SELECT DISTINCT cat_name FROM categories ORDER BY cat_name");
			while (rs.next()) {
				dlm.addElement(rs.getString(1));
			}
			rs.close();
			

	        mainCats.setModel(dlm);
			mainCats.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						List<String> selectedList = mainCats.getSelectedValuesList();
						attributesList.setListData(new String[] {});
						businessModel.setRowCount(0);
						if (selectedList.size() > 0) {
							catListStr = "";
							for (String string : selectedList) {
								catListStr += "'" + string + "',";
							}
							catListStr = catListStr.substring(0,
									catListStr.length() - 1);
							
							ResultSet rs = getSubCategories(catListStr, (String)searchfor.getSelectedItem(), dbConn);
							DefaultListModel subcatdlm = new DefaultListModel();
							
							try {
								while (rs.next()) {
									subcatdlm.addElement(rs.getString(1));
								}
								rs.close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							subCats.setModel(subcatdlm);
						} else {
							attributesList.setListData(new String[] {});
							businessModel.setRowCount(0);
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
