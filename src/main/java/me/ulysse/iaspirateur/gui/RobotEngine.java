package me.ulysse.iaspirateur.gui;

import me.ulysse.iaspirateur.ia.Robot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
/*import java.awt.event.*;
  import javax.swing.event.*;
  import java.beans.*;*/
import java.awt.geom.*;

import static java.util.Objects.requireNonNull;

public class RobotEngine extends ObjectEngine implements ClassOpener {
    private JMenu	       	_robotMenu;
	private JMenu          	_buildSubMenu;
	private JMenuItem          	_newItem;      
	private JMenuItem          	_obstaclesItem;
	private JMenuItem          	_robotPositionItem;
	private JMenu          	_controlSubMenu;
	private JMenuItem          	_startItem;      
	private JMenuItem          	_stopItem;
	private JMenuItem          	_manualItem;
	private JMenuItem          	_automaticItem;
	//private ToolBar            	_toolBar;    

	// sould be thread safe
	private List                _polygonList;
	private Polygon             _polygon;
	private double              _robotX;
	private double              _robotY;
	private double              _robotAngle;
	private double              _robotSpeed;
	private double              _robotInertia;
	private double              _robotHeight;
	private double              _robotWidth;
	private double              _angle;
	private double              _speed;
	private double              _magnitude;
	private int                 _accuracy;
	private double              _range;
	private int _firstX;
	private int _firstY;

	private int					_width;
	private int					_height;

	private FuzzyLogicBasic _learner;

	private ClassLoadMenu _classLoadMenu;

	// GA
    private final Robot robot;
    private boolean[][] coverage;
	private Random rand;

//	public RobotEngine() {
//		_polygonList = new LinkedList();
//		_polygon = new Polygon();
//		//_robotX = -1;
//		//_robotY = -1;
//		// TODO: Randomize position and angle, actually no (at least for now)
//		rand = new Random();
//		int r = rand.nextInt(3);
//		switch (r) {
//		case 0:
//			_robotX = 1300;
//			_robotY = 400;
//			_robotAngle = 0.2*(double)(Math.PI / 2);
//			break;
//		case 1:
//			_robotX = 700;
//			_robotY = 900;
//			_robotAngle = 2*(double)(Math.PI / 2);
//			break;
//		case 2:
//			_robotX = 100;
//			_robotY = 350;
//			_robotAngle = 0.30*(double)(Math.PI / 2);
//			break;
//
//		default:
//			_robotX = 100;
//			_robotY = 350;
//			_robotAngle = 0.30*(double)(Math.PI / 2);
//			break;
//		}
//		//_robotX = 100;
//		//_robotY = 350;
//		//_robotAngle = 0.30*(double)(Math.PI / 2);
//		_robotSpeed = 0;
//		_robotInertia = 0.5;
//		_robotHeight = 20;
//		_robotWidth = 10;
//		_angle = 0;
//		_speed = 10;
//		_magnitude = (double)Math.PI / 4;
//		_accuracy = 10;
//		_range = 100;
//		_firstX = -1;
//		_firstY = -1;
//		StrongTriangularFuzzyPartition[] sftps = new StrongTriangularFuzzyPartition[3];
//		sftps[0] = new StrongTriangularFuzzyPartition("front", new double[]{0, _range / 2, _range});
//		sftps[1] = new StrongTriangularFuzzyPartition("left", new double[]{0, _range / 2, _range});
//		sftps[2] = new StrongTriangularFuzzyPartition("right", new double[]{0, _range / 2, _range});
//		_learner = new FuzzyLogicBasic(sftps, 1);
////		robot = new VacuumCleaner();
//		_width = -1;
//		_height = -1;
//		coverage = new boolean[100];
//	}
	
	public RobotEngine(Robot robot, Interface i) {
	    this.robot = requireNonNull(robot);
	    _interface = requireNonNull(i);
		_polygonList = Collections.synchronizedList(new LinkedList());
		_polygon = new Polygon();
		//_robotX = -1;
		//_robotY = -1;
		// TODO: Randomize position and angle, actually no (at least for now)
		_robotX = 500;
		_robotY = 500;
		_robotAngle = (double)(Math.PI / 2);
		_robotSpeed = 0;
		_robotInertia = 0.5;
		_robotHeight = 20;
		_robotWidth = 10;
		_angle = 0;
		_speed = 10;
		_magnitude = (double)Math.PI / 4;
		_accuracy = 10;
		_range = 100;
		_firstX = -1;
		_firstY = -1;
		StrongTriangularFuzzyPartition[] sftps = new StrongTriangularFuzzyPartition[3];
		sftps[0] = new StrongTriangularFuzzyPartition("front", new double[]{0, _range / 2, _range});
		sftps[1] = new StrongTriangularFuzzyPartition("left", new double[]{0, _range / 2, _range});
		sftps[2] = new StrongTriangularFuzzyPartition("right", new double[]{0, _range / 2, _range});
		_learner = new FuzzyLogicBasic(sftps, 1);
		_width = -1;
		_height = -1;
		coverage = new boolean[10][10];
	}

	@Override
    public void initInterface(Interface i) {
	    _interface = requireNonNull(i);
		// ajout du menu propre �� RobotEngine
		// En classe charg��e dynamiquement, il est n��cessaire de cr��er les menus �� la vol��e et non en t��te de classe !
		_robotMenu = new JMenu("Robot");

		_buildSubMenu = new JMenu("Build");
		_buildSubMenu.addActionListener(this);
		_robotMenu.add(_buildSubMenu);

		_newItem = new JMenuItem("New");
		_newItem.addActionListener(this);
		_buildSubMenu.add(_newItem);

		_buildSubMenu.addSeparator();

		ButtonGroup group = new ButtonGroup();			

		_obstaclesItem = new JRadioButtonMenuItem("Obstacles");
		_obstaclesItem.addActionListener(this);
		_buildSubMenu.add(_obstaclesItem);
		group.add(_obstaclesItem);

		_robotPositionItem = new JRadioButtonMenuItem("Robot position");
		_robotPositionItem.addActionListener(this);
		_buildSubMenu.add(_robotPositionItem);
		group.add(_robotPositionItem);

		_obstaclesItem.setSelected(true);

		_controlSubMenu = new JMenu("Control");
		_controlSubMenu.addActionListener(this);
		_robotMenu.add(_controlSubMenu);

		_startItem = new JMenuItem("Start");
		_startItem.addActionListener(this);
		_controlSubMenu.add(_startItem);

		_stopItem = new JMenuItem("Stop");
		_stopItem.addActionListener(this);
		_controlSubMenu.add(_stopItem);

		_controlSubMenu.addSeparator();

		group = new ButtonGroup();			

		_manualItem = new JRadioButtonMenuItem("Manual");
		_manualItem.addActionListener(this);
		_controlSubMenu.add(_manualItem);
		group.add(_manualItem);

		_automaticItem = new JRadioButtonMenuItem("Automatic");
		_automaticItem.addActionListener(this);
		_controlSubMenu.add(_automaticItem);
		group.add(_automaticItem);

		_manualItem.setSelected(true);

		_interface.getMenu().add(_robotMenu);
		_interface.getMenu().validate();
		//	_interface.getFrame().getContentPane().add(_toolBar, BorderLayout.NORTH);
		//_interface.getFrame().getContentPane().validate();
		
		mouseClicked(1, 55, 44);
		mouseClicked(1, 38, 99);
		mouseClicked(1, 116, 111);
		mouseClicked(2, 111, 88);
		
		mouseClicked(1, 291, 54);
		mouseClicked(1, 262, 98);
		mouseClicked(1, 332, 140);
		mouseClicked(1, 355, 106);
		mouseClicked(2, 389, 89);
		
		mouseClicked(1, 582,67);
		mouseClicked(1, 559,110);
		mouseClicked(1, 629,153);
		mouseClicked(1, 652,128);
		mouseClicked(2, 654,126);
		
		mouseClicked(1, 549,287);
		mouseClicked(1, 528,327);
		mouseClicked(1, 616,351);
		mouseClicked(1, 623,293);
		mouseClicked(1, 578,282);
		mouseClicked(2, 574,285);
		
		mouseClicked(1, 163,247);
		mouseClicked(1, 153,276);
		mouseClicked(1, 138,315);
		mouseClicked(1, 224,354);
		mouseClicked(1, 254,302);
		mouseClicked(2, 212,285);
		
		mouseClicked(1, 352,210);
		mouseClicked(1, 333,354);
		mouseClicked(1, 383,350);
		mouseClicked(1, 388,207);
		mouseClicked(2, 370,205);
		
		// Ligne droite
//		mouseClicked(1, 1, 100);
//		mouseClicked(1, 700, 100);
//		mouseClicked(1, 700, 110);
//		mouseClicked(1, 1, 110);
//		mouseClicked(2, 111, 88);
//		
//		mouseClicked(1, 1, 150);
//		mouseClicked(1, 700, 150);
//		mouseClicked(1, 700, 160);
//		mouseClicked(1, 1, 160);
//		mouseClicked(2, 111, 88);
		
		// "Virage"
//		mouseClicked(1, 1, 100);
//		mouseClicked(1, 400, 100);
//		mouseClicked(1, 400, 150);
//		mouseClicked(1, 700, 150);
//		mouseClicked(1, 700, 160);
//		mouseClicked(1, 390, 160);
//		mouseClicked(1, 390, 110);
//		mouseClicked(1, 1, 110);
//		mouseClicked(2, 1, 110);
//
//		mouseClicked(1, 1, 150);
//		mouseClicked(1, 350, 150);
//		mouseClicked(1, 350, 200);
//		mouseClicked(1, 700, 200);
//		mouseClicked(1, 700, 210);
//		mouseClicked(1, 340, 210);
//		mouseClicked(1, 340, 160);
//		mouseClicked(1, 1, 160);
//		mouseClicked(2, 1, 160);
		
		// Robot is now automatic by default
		_automaticItem.setSelected(true);
		this.move();
	}

	public void actionPerformed(ActionEvent event) {
		System.out.println("Menu Robot item [" + event.getActionCommand() + "] was pressed.");

		if (event.getSource() == _newItem) {	    
			newMap();
		}
		if (event.getSource() == _obstaclesItem) {
		}	    
		if (event.getSource() == _robotPositionItem) {
		}	    
		if (event.getSource() == _startItem) {
			move();
		}	    

	}

	public void newMap() {
		_polygonList = new LinkedList();
		_robotX = _robotY = -1;
		_interface.redraw();	
	}


	public void classOpened(Object object, Object caller) {
	}

	private Polygon createRobotPolygon() {
		Polygon res = new Polygon();
		double x = _robotX - Math.cos(_robotAngle) * _robotHeight;
		double y = _robotY + Math.sin(_robotAngle) * _robotHeight;
		double dx = Math.sin(_robotAngle) * _robotWidth;
		double dy = Math.cos(_robotAngle) * _robotWidth;

		res.addPoint((int)Math.round(_robotX), (int)Math.round(_robotY));
		res.addPoint((int)Math.round(x + dx), 
				(int)Math.round(y + dy));
		res.addPoint((int)Math.round(x), 
				(int)Math.round(y));
		res.addPoint((int)Math.round(x - dx), 
				(int)Math.round(y - dy));
		return res;
	}

	private LinkedList createRobotBoundary() {
		LinkedList res = new LinkedList();
		double x = _robotX - Math.cos(_robotAngle) * _robotHeight;
		double y = _robotY + Math.sin(_robotAngle) * _robotHeight;
		double dx = Math.sin(_robotAngle) * _robotWidth;
		double dy = Math.cos(_robotAngle) * _robotWidth;

		res.add(new Line2D.Double(_robotX, _robotY, x + dx, y + dy));
		res.add(new Line2D.Double(x + dx, y + dy, x - dx, y - dy));
		res.add(new Line2D.Double(x - dx, y - dy, _robotX, _robotY));
		return res;
	}

	private LinkedList createFrontRadarBeams() {
		LinkedList res = new LinkedList();
		double angleStep = _magnitude / _accuracy;
		for (int i = 0; i < _accuracy; i++) {
			double dx = _range * Math.cos(_robotAngle - _magnitude / 2 + i * angleStep);
			double dy = _range * Math.sin(_robotAngle - _magnitude / 2 + i * angleStep);
			res.add(new Line2D.Double(_robotX, _robotY, _robotX + dx, _robotY - dy));
		}
		return res; 
	}

	private LinkedList createLeftRadarBeams() {
		LinkedList res = new LinkedList();
		double angleStep = _magnitude / _accuracy;
		for (int i = 0; i < _accuracy; i++) {
			double dx = _range * Math.cos(_robotAngle - Math.PI / 3 - _magnitude / 2 + i * angleStep);
			double dy = _range * Math.sin(_robotAngle - Math.PI / 3 - _magnitude / 2 + i * angleStep);
			res.add(new Line2D.Double(_robotX, _robotY, _robotX + dx, _robotY - dy));
		}
		return res; 
	}

	private LinkedList createRightRadarBeams() {
		LinkedList res = new LinkedList();
		double angleStep = _magnitude / _accuracy;
		for (int i = 0; i < _accuracy; i++) {
			double dx = _range * Math.cos(_robotAngle + Math.PI / 3 - _magnitude / 2 + i * angleStep);
			double dy = _range * Math.sin(_robotAngle + Math.PI / 3 - _magnitude / 2 + i * angleStep);
			res.add(new Line2D.Double(_robotX, _robotY, _robotX + dx, _robotY - dy));
		}
		return res; 
	}

	private boolean isInSegment(Line2D line, double x, double y) {
		return 
				Math.min(line.getX1(), line.getX2()) <= x &&
				Math.max(line.getX1(), line.getX2()) >= x &&
				Math.min(line.getY1(), line.getY2()) <= y &&
				Math.max(line.getY1(), line.getY2()) >= y;
	}

	private Point2D intersection(Line2D line1, Line2D line2) {
		double a1 = (line1.getX1() == line1.getX2() ? Double.POSITIVE_INFINITY 
				: (line1.getY2() - line1.getY1()) / (line1.getX2() - line1.getX1()));
		double a2 = (line2.getX1() == line2.getX2() ? Double.POSITIVE_INFINITY 
				: (line2.getY2() - line2.getY1()) / (line2.getX2() - line2.getX1()));

		if (a1 == Double.POSITIVE_INFINITY) {
			if (a2 == Double.POSITIVE_INFINITY) {
				if (line1.getX1() != line2.getX1()) return new Point2D.Double(-1,-1);
				else {
					if (distanceToRobot(line2.getP1()) <= distanceToRobot(line2.getP2())) {
						if (Math.min(line1.getY1(), line1.getY2()) <= line2.getP1().getY() &&
								Math.max(line1.getY1(), line1.getY2()) >= line2.getP1().getY()) {
							return line2.getP1();
						}
						else {
							return new Point2D.Double(-1,-1);
						}
					}
					else {
						if (Math.min(line1.getY1(), line1.getY2()) <= line2.getP2().getY() &&
								Math.max(line1.getY1(), line1.getY2()) >= line2.getP2().getY()) {
							return line2.getP2();
						}
						else {
							return new Point2D.Double(-1,-1);
						}
					}
				}
			}
			else {
				double b2 = line2.getY1() - a2 * line2.getX1();
				double y = a2 * line1.getX1() + b2;
				if (isInSegment(line1, line1.getX1(), y) && isInSegment(line2, line1.getX1(), y)) {
					return new Point2D.Double(line1.getX1(), y);
				}
				else {
					return new Point2D.Double(-1,-1);
				}
			}
		}
		else {
			if (a2 == Double.POSITIVE_INFINITY) {
				double b1 = line1.getY1() - a1 * line1.getX1();
				double y = a1 * line2.getX1() + b1;
				if (isInSegment(line1, line2.getX1(), y) && isInSegment(line2, line2.getX1(), y)) {
					return new Point2D.Double(line2.getX1(), y);
				}
				else {
					return new Point2D.Double(-1,-1);
				}
			}
			else {
				double b1 = line1.getY1() - a1 * line1.getX1();
				double b2 = line2.getY1() - a2 * line2.getX1();

				if (a1 == a2) {
					if (b1 != b2) return new Point2D.Double(-1,-1);
					else {
						if (distanceToRobot(line2.getP1()) <= distanceToRobot(line2.getP2())) {
							if (Math.min(line1.getY1(), line1.getY2()) <= line2.getP1().getY() &&
									Math.max(line1.getY1(), line1.getY2()) >= line2.getP1().getY()) {
								return line2.getP1();
							}
							else {
								return new Point2D.Double(-1,-1);
							}
						}
						else {
							if (Math.min(line1.getY1(), line1.getY2()) <= line2.getP2().getY() &&
									Math.max(line1.getY1(), line1.getY2()) >= line2.getP2().getY()) {
								return line2.getP2();
							}
							else {
								return new Point2D.Double(-1,-1);
							}
						}
					}
				}
				else {
					double x = (b2 - b1) / (a1 - a2);
					double y = x * a1 + b1;
					if (isInSegment(line1, x, y) && isInSegment(line2, x, y)) {
						return new Point2D.Double(x,y);
					}
					else {
						return new Point2D.Double(-1f,-1f);
					}
				}
			}
		}
	}

	private double distanceToRobot(Point2D point) {
		return point.distance(_robotX, _robotY);
	}

	private Point2D intersection(Line2D line, Polygon polygon) {
		Point2D res = new Point2D.Double(-1f,-1f);
		PathIterator pathIterator = 
				polygon.getPathIterator(new AffineTransform());

		double[] coordinates = new double[6];	

		if (!pathIterator.isDone()) {
			pathIterator.currentSegment(coordinates);
			pathIterator.next();
			coordinates[2] = coordinates[0];
			coordinates[3] = coordinates[1];
		}
		while(!pathIterator.isDone()) {
			pathIterator.currentSegment(coordinates);
			pathIterator.next();
			Point2D point = intersection(line, new Line2D.Double(coordinates[0], coordinates[1], coordinates[2], coordinates[3]));
			if (point.getX() != -1) {
				if (res.getX() == -1 || distanceToRobot(point) < distanceToRobot(res)) {
					res = point;
				} 
			}
			coordinates[2] = coordinates[0];
			coordinates[3] = coordinates[1];
		}
		return res;
	}

	private Point2D intersection(LinkedList beams, Polygon polygon) {
		Point2D min = new Point2D.Double(-1, -1);
		ListIterator listIterator = beams.listIterator();
		while(listIterator.hasNext()) {
			Point2D res = intersection((Line2D)listIterator.next(), polygon);
			if (min.getX() == -1) {
				min = res;
			}
			else {
				if (res.getX() != -1) {
					if (distanceToRobot(res) < distanceToRobot(min))
						min = res;
				}
			}
		}
		return min;
	}

	private Point2D intersection(LinkedList beams) {
		Point2D min = new Point2D.Double(-1, -1);
		ListIterator listIterator = _polygonList.listIterator();
		while(listIterator.hasNext()) {
			Point2D res = intersection(beams, (Polygon)listIterator.next());
			if (min.getX() == -1) {
				min = res;
			}
			else {
				if (res.getX() != -1) {
					if (distanceToRobot(res) < distanceToRobot(min))
						min = res;
				}
			}
		}
		return min;
	}

	public BufferedImage draw(JViewport vp) {
		Dimension d = vp.getSize();
		if (d.width <= 0 || d.height <= 0) return null;

		BufferedImage image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		int side = Math.min(d.width, d.height);
		double tr = side / 1000.0;

		g.setTransform(new AffineTransform(tr, 0, 0, tr, 0, 0));

		g.setColor(new Color(0,125,0));	    
		g.fillRect(0, 0, (int)Math.round(1000.0 * d.width / side), (int)Math.round(1000 * d.height / side));

		// width and height initialization
		if(_width == -1) {
			_width = (int)Math.round(1000.0 * d.width / side);
			_height = (int)Math.round(1000 * d.height / side);
		}

		int w = d.width;
		int h = d.height;

		int margin  = 15;
		if (_polygonList.size() == 0) {
			_polygonList.add(new Polygon(new int[]{0, (int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side), 0, 0},
					new int[]{0, 0, margin, margin, 0}, 5));
			_polygonList.add(new Polygon(new int[]{0, margin, margin, 0, 0},
					new int[]{0, 0, (int)Math.round(1000.0 * h / side), 
					(int)Math.round(1000.0 * h / side), 0}, 5));
			_polygonList.add(new Polygon(new int[]{0, (int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side), 0, 0},
					new int[]{(int)Math.round(1000.0 * h / side - margin), 
					(int)Math.round(1000.0 * h / side - margin), 
					(int)Math.round(1000.0 * h / side), 
					(int)Math.round(1000.0 * h / side),
					(int)Math.round(1000.0 * h / side - margin)}, 5));
			_polygonList.add(new Polygon(new int[]{(int)Math.round(1000.0 * w / side - margin), 
					(int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side - margin),
					(int)Math.round(1000.0 * w / side - margin)},
					new int[]{0, 0, (int)Math.round(1000.0 * h / side), 
					(int)Math.round(1000.0 * h / side), 0}, 5));
		} 
		else {
			_polygonList.set(0, new Polygon(new int[]{0, (int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side), 0, 0},
					new int[]{0, 0, margin, margin, 0}, 5));
			_polygonList.set(1, new Polygon(new int[]{0, margin, margin, 0, 0},
					new int[]{0, 0, (int)Math.round(1000.0 * h / side), 
					(int)Math.round(1000.0 * h / side), 0}, 5));
			_polygonList.set(2, new Polygon(new int[]{0, (int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side), 0, 0},
					new int[]{(int)Math.round(1000.0 * h / side - margin), 
					(int)Math.round(1000.0 * h / side - margin), 
					(int)Math.round(1000.0 * h / side), 
					(int)Math.round(1000.0 * h / side),
					(int)Math.round(1000.0 * h / side - margin)}, 5));
			_polygonList.set(3, new Polygon(new int[]{(int)Math.round(1000.0 * w / side - margin), 
					(int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side), 
					(int)Math.round(1000.0 * w / side - margin),
					(int)Math.round(1000.0 * w / side - margin)},
					new int[]{0, 0, (int)Math.round(1000.0 * h / side), 
					(int)Math.round(1000.0 * h / side), 0}, 5));
		}
		g.setColor(new Color(0,0,0));

		PathIterator pathIterator = 
				_polygon.getPathIterator(new AffineTransform());

		double[] coordinates = new double[6];	
		if (!pathIterator.isDone()) {
			pathIterator.currentSegment(coordinates);
			pathIterator.next();
			coordinates[2] = coordinates[0];
			coordinates[3] = coordinates[1];
		}
		while(!pathIterator.isDone()) {
			pathIterator.currentSegment(coordinates);
			pathIterator.next();
			g.draw(new Line2D.Double(coordinates[0], coordinates[1], coordinates[2], coordinates[3]));
			coordinates[2] = coordinates[0];
			coordinates[3] = coordinates[1];
		}

		ListIterator listIterator = _polygonList.listIterator();
		while(listIterator.hasNext()) {
			try {
				g.fill((Shape)listIterator.next());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (_robotX != -1f && _robotY != -1f) {
			Point2D collision = intersection(createRobotBoundary());
			if (collision.getX() != -1) {
				System.out.println("I am the king of the world, glou glou glou ...");
				System.out.println("Terrain covered="+coverage()*100+"%");
				_robotX = _robotY = -1;
			}
			else {
				g.setColor(new Color(255,0,0));
				Polygon robot = createRobotPolygon();
				g.fill((Shape)robot);
				LinkedList frontRadarBeams = createFrontRadarBeams();
				listIterator = frontRadarBeams.listIterator();
				while (listIterator.hasNext()) {
					g.draw((Shape)listIterator.next());
				}
				LinkedList leftRadarBeams = createLeftRadarBeams();
				listIterator = leftRadarBeams.listIterator();
				while (listIterator.hasNext()) {
					g.draw((Shape)listIterator.next());
				}
				LinkedList rightRadarBeams = createRightRadarBeams();
				listIterator = rightRadarBeams.listIterator();
				while (listIterator.hasNext()) {
					g.draw((Shape)listIterator.next());
				}
				Point2D front = intersection(frontRadarBeams);
				Point2D left = intersection(leftRadarBeams);
				Point2D right = intersection(rightRadarBeams);
				g.setColor(new Color(0,0,255));
				if (front.getX() != -1) g.fill(new Ellipse2D.Double(front.getX() - 5, front.getY() - 5, 10, 10));
				if (left.getX() != -1) g.fill(new Ellipse2D.Double(left.getX() - 5, left.getY() - 5, 10, 10));
				if (right.getX() != -1) g.fill(new Ellipse2D.Double(right.getX() - 5, right.getY() - 5, 10, 10));	    
			}
		}

		return image;
	}


	public void mouseClicked(int whichButton, int x, int y) {
		Dimension d = _interface.getViewport().getSize();
		if (_obstaclesItem.isSelected()) {
			switch (whichButton) {
			case 1:
				// Trace le "contour"
				double tr = 1000.0 / Math.min(d.width, d.height);
				int newX = (int)Math.round(tr * x);
				int newY = (int)Math.round(tr * y);
				_firstX = (_firstX == -1 ? newX : _firstX);
				_firstY = (_firstY == -1 ? newY : _firstY);
				_polygon.addPoint(newX,newY);
				_interface.redraw();
				break;
			case 2:
				// Ferme le polygone
				_polygon.addPoint(_firstX, _firstY);
				_firstX = _firstY = -1;
				_polygonList.add(_polygon);
				_polygon = new Polygon();
				_interface.redraw();
				break;
			case 3:
				_firstX = _firstY = -1;
				_polygon = new Polygon();
				_interface.redraw();
				break;
			}
		}
		else {
			double tr = 1000.0 / Math.min(d.width, d.height);	    
			_robotX = tr * x;
			_robotY = tr * y;
			_interface.redraw();
		}
	}


	public void move() {
		final CountDownLatch latch = new CountDownLatch(1);
		
		Thread worker = new Thread() {
			public synchronized void run() {
				try {
					_interface.redraw(); // To avoid bug in updateCoverage when _width and _height incorrect
					
					long startTime = System.currentTimeMillis(); //fetch starting time
					while((_robotX != 1 && _robotY != -1) && (System.currentTimeMillis()-startTime)<10000) {
						double front = distanceToRobot(intersection(createFrontRadarBeams()));
						double left = distanceToRobot(intersection(createLeftRadarBeams()));
						double right = distanceToRobot(intersection(createRightRadarBeams()));
						if (_automaticItem.isSelected()) {
							_angle = robot.calculateDirection(left, front, right);
							// Previous code
							//_angle = _learner.getValue(new double[]{front, left, right})[0];
						}
						else {
							_learner.setConclusions(new double[]{front, left, right}, new double[]{_angle});
						}
						_robotAngle = _robotAngle + (1 - _robotInertia) * _angle;
						//System.out.println("angle = " + _angle);
						//System.out.println("_robotX="+_robotX+"; _robotY="+_robotY);

						if (_robotAngle >= 2 * Math.PI)
							_robotAngle -= 2 * Math.PI;
						if (_robotAngle <= - 2 * Math.PI)
							_robotAngle += 2 * Math.PI;
						_robotSpeed = 10;
						_robotX += _robotSpeed * Math.cos(_robotAngle);
						_robotY -= _robotSpeed * Math.sin(_robotAngle);
                        updateCoverage();
                        _interface.redraw();

                        // TODO: Speed up frame generation, make it a parameter
						Thread.sleep(0);
					}

                    latch.countDown();
					//System.exit(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		};
		
		worker.start();
//		try {
//			Thread.sleep(3100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_interface.over = true;
		_interface.frame.dispose();
		
	}
	
	public double moveNoGUI() {
		Thread worker = new Thread() {
			public synchronized void run() {
				try {
					while(_robotX != 1 && _robotY != -1) {
						double front = distanceToRobot(intersection(createFrontRadarBeams()));
						double left = distanceToRobot(intersection(createLeftRadarBeams()));
						double right = distanceToRobot(intersection(createRightRadarBeams()));
							_angle = robot.calculateDirection(left, front, right);
							// Previou code
							_angle = _learner.getValue(new double[]{front, left, right})[0];

						_robotAngle = _robotAngle + (1 - _robotInertia) * _angle;
						//System.out.println("angle = " + _angle);
						//System.out.println("_robotX="+_robotX+"; _robotY="+_robotY);

						if (_robotAngle >= 2 * Math.PI)
							_robotAngle -= 2 * Math.PI;
						if (_robotAngle <= - 2 * Math.PI)
							_robotAngle += 2 * Math.PI;
						_robotSpeed = 10;
						_robotX += _robotSpeed * Math.cos(_robotAngle);
						_robotY -= _robotSpeed * Math.sin(_robotAngle); 
						//_interface.redraw();
						// TODO: Speed up frame generation
						Thread.sleep(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		worker.start();
		// Not sure this method works, not called anywhere for now
        throw new UnsupportedOperationException();
	}


	public void keyTyped(char c) {
		if (c == 'a') _automaticItem.setSelected(true);
		if (c == 'm') _manualItem.setSelected(true);
		if (_robotX != -1 && _robotY != -1 && _manualItem.isSelected()) {
			switch (c) {
			case '4':
				_angle = Math.min(_angle + Math.PI / 12, Math.PI / 6);
				break;
			case '6':
				_angle = Math.max(_angle - Math.PI / 12, -Math.PI / 6);
				break;
			}
		}
	}


	public void write() {
		System.out.println("Vous ��tes dans RobotEngine");
	}

	public void terminate() {
		// supprimer les menus
		_interface.getMenu().remove(_robotMenu);
		_interface.getMenu().validate();
		_interface.getMenu().repaint();		
	}

	/**
	 * Fonction qui mesure l'aire parcourue par le robot (Fitness)
	 */
	private void updateCoverage() {
		int x = (int) (_robotX/(_width/10));
		int y = (int) (_robotY/(_height/10));

		coverage[x][y] = true;
	}

	public double coverage() {
		int nbZoneCovered = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if(coverage[i][j]) {
					nbZoneCovered++;
				}
			}
		}

		return (double) nbZoneCovered / 100;
	}
}
