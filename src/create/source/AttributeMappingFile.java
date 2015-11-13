package create.source;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.corba.se.impl.orb.ParserTable.TestIIOPPrimaryToContactInfo;
import com.sun.org.apache.xalan.internal.xsltc.compiler.NodeTest;

import common.FieldMapping;
import common.MyTreeCellRender;
import common.MyTreeNode;
import descript.FileList;
import sun.net.TelnetInputStream;


public class AttributeMappingFile extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JTree templateTree;
	private JTree sourceTree;
	private JPanel btnPanel;
	private JButton equalBtn;
	
	private ButtonHandler btnHandler;
	private DefaultListModel<String> model;
	private JList<String> mapList;
	private JButton removeBtn;
	private JButton saveBtn;
	private JButton bigBtn;
	private JButton smallBtn;
	private JFrame frame;
	
	private TreeSelectHandler treeHandler;
	private File file;
	private Document doc ;
	
	private ArrayList<MyTreeNode> mappedNode = new ArrayList<MyTreeNode>();
	private ArrayList<TreePath> matchNode = new ArrayList<TreePath>();
	
	private FileList fileList;
	
 	public AttributeMappingFile( File file, FileList fileList ){
		super("metadata mapping");
		this.frame = this;
		this.fileList = fileList;
		
		btnHandler = new ButtonHandler();
		treeHandler = new TreeSelectHandler();
		
		this.setLayout( new GridBagLayout() );
		this.file = file;
		try {
			templateTree = new JTree( utils.Utils.createTree("tree/sys/templateTree.xml") );
		} catch (Exception e) {
			System.out.println("Can't open templateTree.xml");
			e.printStackTrace();
			return ;
		}
		MyTreeCellRender render = new MyTreeCellRender();
		templateTree.setCellRenderer(render);
		templateTree.addTreeSelectionListener(treeHandler);
		templateTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane templatePane = new JScrollPane( templateTree );
		
		try {
			sourceTree = new JTree( utils.Utils.createTree(file));
			MyTreeNode root = (MyTreeNode) sourceTree.getModel().getRoot();
			this.doc = ((Node)root.getUserObject()).getOwnerDocument();
		} catch (Exception e) {
			System.out.println("Can't open blankSourceTree.xml");
			e.printStackTrace();
			return ;
		}
		sourceTree.setCellRenderer(render);
		sourceTree.addTreeSelectionListener(treeHandler);
		sourceTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane sourcePane = new JScrollPane(sourceTree);
		
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridBagLayout() );
		equalBtn = new JButton("=");
		equalBtn.setToolTipText("equals");
		equalBtn.addActionListener(btnHandler);
		bigBtn = new JButton(">");
		bigBtn.setToolTipText("superclass");
		bigBtn.addActionListener(btnHandler);
		smallBtn = new JButton("<");
		smallBtn.setToolTipText("subclass");
		smallBtn.addActionListener(btnHandler);
		btnPanel.add( new JPanel(), new GridBagConstraints( 0, 0, 100, 40, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0 , 0 ));
		btnPanel.add( equalBtn,     new GridBagConstraints( 0, 40, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),
				0 , 0 ));
		btnPanel.add( smallBtn,     new GridBagConstraints( 0, 50, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),
				0 , 0 ));
		btnPanel.add( bigBtn,     new GridBagConstraints( 0, 60, 100, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),
				0 , 0 ));
		btnPanel.add( new JPanel(), new GridBagConstraints( 0, 70, 100, 30, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
				0 , 0 ));
		
		JPanel topPane = new JPanel();
		topPane.setLayout(new GridLayout(1, 2));
		//--------change topPane layout
		topPane.setLayout(new GridBagLayout() );
		topPane.add( sourcePane, new GridBagConstraints( 0,0,40,1,0.5,0.5,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0),
				0,0));
		topPane.add( btnPanel, new GridBagConstraints( 40,0,20,1,0,0,
				GridBagConstraints.CENTER,
				GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0),
				0,0));
		topPane.add( templatePane, new GridBagConstraints( 60,0,40,1,0.5,0.5,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0),
				0,0));
		
		add(topPane, new GridBagConstraints( 0, 0, 100, 70, 1, 0.7,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0),
				0, 0 ) );
		
		model = new DefaultListModel<String>();
		mapList = new JList<String>(model);
		initMapList();
		JScrollPane mapPane = new JScrollPane(mapList);
		removeBtn = new JButton("Remove");
		removeBtn.addActionListener(btnHandler);
		saveBtn = new JButton("Save");
		saveBtn.addActionListener(btnHandler);
		JMenuBar btnsBar = new JMenuBar();
		FlowLayout btnsBarlayout = new FlowLayout();
		btnsBarlayout.setAlignment(FlowLayout.CENTER);
		btnsBar.setLayout(btnsBarlayout);
		btnsBar.add(saveBtn);
		btnsBar.add(removeBtn);
		JPanel bottom = new JPanel();
		bottom.setLayout( new BorderLayout() );
		bottom.add(mapPane, BorderLayout.CENTER);
		bottom.add( btnsBar, BorderLayout.SOUTH );
		add(bottom, new GridBagConstraints(0, 70, 100, 30, 1, 0.3, 
				GridBagConstraints.CENTER, 
				GridBagConstraints.BOTH, 
				new Insets(0, 0, 0, 0),
				0, 0 ) );
		setSize( 600, 600 );
		setLocationRelativeTo(null);
		setVisible(true);
		
		MyTreeNode r = (MyTreeNode) templateTree.getModel().getRoot();
		MyTreeNode major = (MyTreeNode) r.getChildAt(1);
		
		System.out.println( major );
		((MyTreeNode) major.getChildAt(8)).setMatched(true);
		((MyTreeNode) major.getChildAt(9)).setMatched(true);
		((MyTreeNode) major.getChildAt(10)).setMatched(true);
		
	}
	private void initMapList() {
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(this.file);
			Element book = doc.getDocumentElement();
			NodeList list = book.getElementsByTagName("field");
			for( int i = 0; i < list.getLength(); i++ ){
				Element ele = (Element) list.item(i);
				String mapping = ele.getAttribute("mapping").trim();
				if( !mapping.equals("") ){
					String path = utils.Utils.elementPathToString(ele);
					model.addElement(path + mapping); 
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	class ButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			AbstractButton btn = (AbstractButton) e.getSource();
			System.out.println( btn.getText() );
			switch( btn.getText() ){
			case "=":
				mapping("=");
				break;
			case "<":
				mapping("<");
				break;
			case ">":
				mapping(">");
				break;
			case "Remove":
				int index = mapList.getSelectedIndex();
				if( index > 0 ){
					model.remove(index);
				}
				break;
			case "Save":
				utils.Utils.saveXML( doc , file);
				if( null != fileList ){
					fileList.updateList();
				}else{
					System.out.println( "fileList is null");
				}
				frame.dispose();
				break;
			default:
			}
		}
	}
	
	class TreeSelectHandler implements TreeSelectionListener{

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			
			JTree select = (JTree) e.getSource();
			if( select == templateTree )return ;
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) 
					select.getLastSelectedPathComponent();
			if( null == treeNode )return ;
			for( int i = 0; i < mappedNode.size(); i++ ){
				mappedNode.get(i).setMappedNode(false);
			}
			mappedNode.clear();
			Element docNode = (Element) treeNode.getUserObject();
			String mapping = docNode.getAttribute("mapping").trim();
			if( !mapping.equals("") ){
				ArrayList<MyTreeNode> nodes = getMappingTreeNodes(mapping);
				templateTree.scrollPathToVisible(new TreePath( nodes.toArray()));
				MyTreeNode last = nodes.get(nodes.size()-1);
				last.setMappedNode(true);
				mappedNode.add(last);
			}
			templateTree.updateUI();
		}
		
	} 
	private void mapping(String relation){
		MyTreeNode s= (MyTreeNode) sourceTree.getLastSelectedPathComponent();
		MyTreeNode t= (MyTreeNode) templateTree.getLastSelectedPathComponent();
		System.out.println("mapping");
		if( s.isLeaf() && t.isLeaf() ){
			try {
				wirteToFile( s, relation, t );
				showOnPanel( s, relation, t );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			JOptionPane.showMessageDialog(null, "just field node can mapping");
		}
	}
	private void showOnPanel(MyTreeNode source, String relation, MyTreeNode template) {
		model.addElement(new FieldMapping( source, relation, template ).toString() );
	}
	private void wirteToFile( MyTreeNode source, String relation, MyTreeNode template ) throws Exception{
		Element sourceElement = (Element) source.getUserObject();
		Element templateElement = (Element) template.getUserObject();
		String mappingTarget = getMappingTarget( relation, templateElement);
		System.out.println( mappingTarget + "write");
		if( isMappingTargetExist( doc,  mappingTarget) ){
			JOptionPane.showMessageDialog(null, "this template field has have mapping field");
			throw new Exception("this template field has have mapping field");
		}else{
			sourceElement.setAttribute("mapping", mappingTarget );
		}
	}
	
	private String getMappingTarget( String relation, Element targetElement ){
		return relation + " " + utils.Utils.elementPathToString(targetElement);
	}
	private boolean isMappingTargetExist( Document doc, String value ){
		NodeList fields = doc.getElementsByTagName("field");
		for( int i = 0; i < fields.getLength(); i++ ){
			NamedNodeMap fieldAttr = fields.item(i).getAttributes();
			Node map = fieldAttr.getNamedItem("mapping");
			if(  null != map ){
				System.out.println( map.getNodeValue() );
				if(map.getNodeValue().trim().equals( value ) ){
					return true;
				}	
			}
		}
		return false;
	}
	private boolean passable(){
		Element e = null;
		String[] ids = new String[]
				{"requestNo","voyage","stationNo","sampleType","sampleNo"};
		for( int i = 0; i < ids.length; i++ ){
			e = doc.getElementById(ids[i]);
			if( e.getAttribute("mapping") == null ){
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<MyTreeNode> getMappingTreeNodes( String path ){
		String[] nodes = path.split(">");
		MyTreeNode parent = (MyTreeNode) 
				templateTree.getModel().getRoot();
		MyTreeNode node = null;
		ArrayList<MyTreeNode> nodeList = new ArrayList<MyTreeNode>();
		nodeList.add( parent );
		for( int i = 1; i < nodes.length; i++ ){
			node = getTreeNodeByDocNodeName(parent, nodes[i] );
			if( null == node )break;
			parent = node;
			nodeList.add( parent );
		}
		return nodeList;
	}
	public MyTreeNode getTreeNodeByDocNodeName( DefaultMutableTreeNode parent, String name ){
		@SuppressWarnings("unchecked")
		Enumeration<MyTreeNode> enumrate = parent.children();
		while( enumrate.hasMoreElements() ){
			MyTreeNode child = enumrate.nextElement();
			Element e = (Element) child.getUserObject();
			String value = e.getAttribute("name");
			if( value.trim().equals(name))return child;
		}
		return null;
	}
	public static void main( String[] args ){
		//new AttributeMappingFile(new File("tree/blankSourceTree.xml"), null);
	}
}
