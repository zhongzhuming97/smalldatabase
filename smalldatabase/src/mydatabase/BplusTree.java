package mydatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class BplusTree implements B, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 根节点
	private Node root;
	// 阶数
	private int order;
	// 叶子结点链表头
	private Node head;

	private File file;

	private List<Object> properties;

	private Integer keyPositon;

	private Object keyProperty;
	
	public Object getKeyProperty() {
		return keyProperty;
	}

	public void setKeyProperty(Object keyProperty) {
		this.keyProperty = keyProperty;
	}

	public Integer getKeyPositon() {
		return keyPositon;
	}

	public void setKeyPositon(Integer keyPositon) {
		this.keyPositon = keyPositon;
	}

	public List<Object> getProperties() {
		return properties;
	}

	public void setProperties(List<Object> properties) {
		this.properties = properties;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Node getHead() {
		return head;
	}

	public void setHead(Node head) {
		this.head = head;
	}

	@Override
	public Object get(Comparable key) {
		// TODO Auto-generated method stub
		return root.get(key);
	}

	@Override
	public void remove(Comparable key) {
		root.remove(key, this);
	}

	@Override
	public void insertOrUpdate(Comparable key, Object obj) {
		root.insertOrUpdate(key, obj, this);

	}

	public BplusTree(int order) {
		if (order < 3) {
			System.out.println("order must be greater than 2");
			System.exit(0);
		}
		keyPositon=-1;
		this.order = order;
		root = new Node(true, true);
		head = root;
		properties = new ArrayList<>();
	}

//	public static void main(String[] args) throws IOException, ClassNotFoundException {
//		BplusTree tree = new BplusTree(6);
//		Random random = new Random();
//		long current = System.currentTimeMillis();
//		tree.insertOrUpdate('a', 5);// 0
//		tree.insertOrUpdate('b', 8);// 1
//		tree.insertOrUpdate('c', 10);// 2
//		tree.insertOrUpdate('d', 16);// 3
//		tree.insertOrUpdate('e', 16);// 4
//		tree.insertOrUpdate('f', 20);// 5
//		tree.insertOrUpdate('g', 30);// 6
//		System.out.println(tree.get('a'));
//		Node next = tree.getHead();
//		int count = 0;
//		while (true) {
//			if (next == null)
//				break;
//			++count;
//			List<Entry<Comparable, Object>> entries = next.getEntries();
//			File file = new File(String.valueOf(count) + ".txt");
//			next.setFile(file);
//			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
//			objectOutputStream.writeObject(next);
//			objectOutputStream.close();
//			next = next.getNext();
//		}
//		File treeFile = new File("BplusTree.txt");
//		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(treeFile));
//		objectOutputStream.writeObject(tree);
//		objectOutputStream.close();
//		
//		
//		ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(treeFile));
//		BplusTree tree2=(BplusTree) objectInputStream.readObject();
//		objectInputStream.close();
//		System.out.println(tree2.get('e'));
//	}
//		for (int j = 0; j < 100000; j++) {
//			for (int i = 0; i < 100; i++) {
//				int randomNumber = random.nextInt(1000);
//				tree.insertOrUpdate(randomNumber, randomNumber);
//			}
//
//			for (int i = 0; i < 100; i++) {
//				int randomNumber = random.nextInt(1000);
//				tree.remove(randomNumber);
//			}
//		}
//
//		long duration = System.currentTimeMillis() - current;
//		System.out.println("time elpsed for duration: " + duration);
//		int search = 90;
//		System.out.print(tree.get(search));
//		
//		
}
