package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Cell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.MyTreeNode;

public class Utils {

	public static void main(String[] args ){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File("tree/sys/templateTree.xml"));
			Element book = doc.getDocumentElement();
			
			
			//Element e = getChildElementByName(book, "SampleNo");
			Element e = getChildElementByName(book, "MicroElement");
			Element s = getElementByPath(e, new String[]{"SampleNo"});
			System.out.println(s);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public static String getCellStringValue( Cell cell ){
		if( null == cell )return null;
		switch( cell.getCellType() ){
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf( cell.getBooleanCellValue() );
		case Cell.CELL_TYPE_ERROR:
			return null;
		case Cell.CELL_TYPE_FORMULA:
			return null;
		case Cell.CELL_TYPE_NUMERIC:
			return String.valueOf( cell.getNumericCellValue() );
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		}
		return null;
	}
	public static String getAttribute( Element root, Object[] path, String attri ){
		Element element = getElementByPath(root, path);
		if( null != element ){
			return element.getAttribute(attri);
		}
		return null;
	}
	public static Element getElementByPath( Element parent, Object[] path ){
		Element e = null;
		for( int i = 0; i < path.length; i++ ){
			String name = path[i].toString();
			e = getChildElementByName(parent, name);
			parent = e;
		}
		return e;
	}
	public static Element getChildElementByName( Element parent, String name){
		NodeList childs = parent.getChildNodes();
		for( int i = 0; i < childs.getLength(); i++ ){
			Node node = childs.item(i);
			if( node.getNodeType() == Node.ELEMENT_NODE ){
				Element element = (Element) node;
				String value = element.getAttribute("name").trim();
				if( value.equals(name) )return element;
			}
		}
		return null;
	}
	public static File saveXML(Document doc, String defname ) {
		File file = null;
		int value = 0;
		boolean cancel = false;
		String name = null;
		StringBuffer fileName = new StringBuffer();
		String message = "please input the file name";
		while( !cancel ){
			fileName.delete(0, fileName.length());
			name = JOptionPane.showInputDialog( message );
			if( null == name ){
				cancel = true;
				break;
			}
			if( name.startsWith("#") ){
				message = name + " is unvalidate please input again";
				continue;
			}
			fileName.append("tree");
			fileName.append( File.separator );
			fileName.append( name.trim() );
			if( !fileName.toString().endsWith(".xml") ){
				fileName.append(".xml");
			}
			file = new File( fileName.toString() );
			if( file.exists() ){
				value = JOptionPane.showConfirmDialog(null, 
					name + " has exists do you want to replace it?",
					"Confirm", JOptionPane.YES_NO_OPTION);
				if( JOptionPane.YES_OPTION == value )break;
			}else{
				try {
					file.createNewFile();
					break;
				} catch (IOException e) {
					message = name + " is unvalidate please input again";
					continue;
				}
			}
		}
		if( !cancel ){
			return saveXML(doc, file );
		}
		return null;
	}
	public static File saveXML(Document doc, File file ) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch ( TransformerException e) {
			JOptionPane.showMessageDialog(null, Lang.get("err1"));
			e.printStackTrace();
		}
		return file;
	}
	public static void printXML(Document doc ) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch ( TransformerException e) {
			JOptionPane.showMessageDialog(null, Lang.get("err1"));
			e.printStackTrace();
		}
		
	}

	public static MyTreeNode createTree(String path) throws Exception {
		return createTree(new File(path));
	}

	public static MyTreeNode createTree(File source) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(source);
		Element book = doc.getDocumentElement();
		MyTreeNode root = new MyTreeNode(book);
		iteratorCreateNode(root, book);
		return root;
	}
	private static void iteratorCreateNode(MyTreeNode root, Node info) {
		MyTreeNode treeNode = null;
		NodeList subInfos = info.getChildNodes();
		for (int i = 0; i < subInfos.getLength(); i++) {
			Node node = subInfos.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				treeNode = createTreeNode(root, node);
				iteratorCreateNode(treeNode, node);
			}
		}
	}

	private static MyTreeNode createTreeNode(MyTreeNode parent, Object info) {
		MyTreeNode child = new MyTreeNode(info);
		parent.add(child);
		return child;
	}
	
	public static String elementPathToString( Element element ){
		StringBuffer buf = new StringBuffer();
		Node temp = null;
		Element parent = element;
		List<String> branch = new ArrayList<String>();
		while( parent != null ){
			branch.add( parent.getAttribute("name").trim() );
			temp = parent.getParentNode();
			if( temp.getNodeType() == Node.ELEMENT_NODE ){
				parent = (Element) temp;
			}else{
				parent = null;
			}
		}
		Collections.reverse(branch);
		Iterator<String> itr = branch.iterator();
		buf.append(" ");
		while( itr.hasNext()){
			buf.append(itr.next());
			buf.append('>');
		}
		buf.deleteCharAt( buf.length() - 1 );
		return buf.toString();
	}
	public static List<MyTreeNode> getLeafs(MyTreeNode root) {
		
		MyTreeNode node = null;
		List<MyTreeNode> leaves = new ArrayList<MyTreeNode>();
		Queue<MyTreeNode> branches = new ArrayDeque<MyTreeNode>();
		
		branches.add(root);
		
		while( !branches.isEmpty() ){
			node = branches.poll();
			for( int i = 0; i < node.getChildCount(); i++ ){
				if( node.getChildAt(i).isLeaf() ){
					leaves.add((MyTreeNode) node.getChildAt(i));
				}else{
					branches.offer((MyTreeNode) node.getChildAt(i));
				}
			}
		}
		return leaves;
	}
	public static String getFileName(String name) {
		int index = name.lastIndexOf('.');
		index = index < 0 ? 0 : index;
		return name.substring(0, index);
	}
}
