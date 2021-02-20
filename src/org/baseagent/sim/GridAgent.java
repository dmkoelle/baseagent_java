package org.baseagent.sim;

import java.util.List;
import java.util.Random;

import org.baseagent.behaviors.Behavior;
import org.baseagent.grid.GridPosition;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.ui.Drawable;
import org.baseagent.ui.DrawableAgent;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.defaults.VisualizationLibrary;
import org.baseagent.util.BaseAgentMath;
import org.baseagent.util.CellPoint2D;
import org.baseagent.util.Vector2D;

import javafx.scene.paint.Color;

public class GridAgent extends DrawableAgent implements HasGridPosition {
	private int cellX;
	private int cellY;
	private double heading;
	private String gridLayerName;
	
	// Moving subsystem
	private double movingX;
	private double movingY;
	private HasGridPosition destinationPoint;
	private double movingSpeed;
	
	public GridAgent() {
		super();
		setDrawable(new Drawable() {
			@Override
			public void draw(GridCanvasContext gcc) {
				VisualizationLibrary.drawTriangleWithHeading2(gcc.getGraphicsContext(), getCellX(), getCellY(), gcc.getCellWidth(), gcc.getCellHeight(), getHeading(), getColorOrUse(Color.CADETBLUE), getColorOrUse(Color.CADETBLUE).darker());
			}
		});
	}
	
	public GridAgent(String gridLayerName) {
		this();
		this.gridLayerName = gridLayerName;
	}

	public GridAgent(Behavior... behaviors) {
		this();
		
		for (Behavior behavior : behaviors) {
			addBehavior(behavior);
		}
	}

	public GridAgent(List<Behavior> behaviors) {
		this();
		
		for (Behavior behavior : behaviors) {
			addBehavior(behavior);
		}
	}

	@Override
	public int getCellX() {
		return this.cellX;
	}

	@Override
	public int getCellY() {
		return this.cellY;
	}
	
	public GridPosition getGridPosition() {
		return new GridPosition(getCellX(), getCellY());
	}
	
	@Override
	public double getHeading() {
		return this.heading;
	}
	
	@Override
	public void setCellX(int x) {
		this.cellX = x;
	}
	
	@Override
	public void setCellY(int y) {
		this.cellY = y;
	}
	
	public void setHeading(double heading) {
		this.heading = heading;
	}

	public Grid getGrid() {
		return (Grid)getSimulation().getUniverse();
	}
	
	// Convenience method to get the current layer
	@Override
	public GridLayer getGridLayer() {
		return getGrid().getGridLayer(this.gridLayerName);
	}
	
	public Object getObjectFromLayer(String layerName) {
		return getGrid().getGridLayer(layerName).current().get(getCellX(), getCellY());
	}
	
	public Object getObjectFromLayer(String layerName, int cellX, int cellY) {
		return getGrid().getGridLayer(layerName).current().get(cellX, cellY);
	}
	
	public Object getObjectFromLayer(String layerName, CellPoint2D point) {
		return getGrid().getGridLayer(layerName).current().get(point.getCellX(), point.getCellY());
	}
	
	
    //
	// Inventory subsystem and its interaction with GridLayers
	//
	
	public void take(String layerName, Object thing, Object replacement) {
		getGrid().getGridLayer(layerName).set(getCellX(), getCellY(), replacement);
		getInventory().add(thing);
	}

	public void drop(String layerName, Object thing) {
		getGrid().getGridLayer(layerName).set(getCellX(), getCellY(), thing);
		getInventory().remove(thing);
	}

	//
	// Movement subsystem
	// Movement respects the grid's bounds policy
	//
	
	public void startMovingToward(int destinationX, int destinationY, double speed) {
		this.destinationPoint = new GridPosition(destinationX, destinationY);
		this.movingSpeed = speed;
		startMovingToward0();
	}
	
	public void startMovingToward(HasGridPosition pos, double speed) {
		this.destinationPoint = pos;
		this.movingSpeed = speed;
		startMovingToward0();
	}
	
	private void startMovingToward0() {
		setHeading(BaseAgentMath.direction(this, this.destinationPoint));
		this.movingX = getCellX();
		this.movingY = getCellY();
	}

	public boolean continueMovingToward() {
		if (BaseAgentMath.distance(destinationPoint, this) <= movingSpeed) {
			setCellX(destinationPoint.getCellX());
			setCellY(destinationPoint.getCellY());
			return true;
		} else {
			setHeading(BaseAgentMath.direction(this, this.destinationPoint));
			movingX += movingSpeed * Math.cos(getHeading());
			movingY += movingSpeed * Math.sin(getHeading());
			moveTo((int)movingX, (int)movingY);
			return false;
		}
	}
	
	public void moveToward(int destinationX, int destinationY) {
		double direction = BaseAgentMath.direction(this, destinationX, destinationY);
		moveAlong(1.0, direction); // 1.0 should be 'speed'
	}
	
	public void moveToward(HasGridPosition point, double speed) {
		double direction = BaseAgentMath.direction(this, point);
		moveAlong(speed, direction);
	}

	public void moveAlong(Vector2D vector) {
		moveAlong(vector.getMagnitude(), vector.getDirection());
	}
	
	public void moveAlong(double distance, double direction) {
		setHeading(direction);
//		System.out.println("WARNING: Agent.moveToward is assuming a speed of 3.0 for testing purposes");
//		moveDelta(distance * Math.sin(direction), distance * Math.cos(direction));
		moveDelta(6.0 * Math.cos(direction), 6.0 * Math.sin(direction)); // TODO - Agent.moveAlong using 6.0 instead of 'distance'
	}
	
	public void moveDelta(double deltaX, double deltaY) {
		moveDelta((int)deltaX, (int)deltaY);
	}
	
	public void moveDelta(int deltaX, int deltaY) {
		moveTo(getCellX() + deltaX, getCellY() + deltaY);
	}

	public void moveTo(HasGridPosition p) {
		this.moveTo(p.getCellX(), p.getCellY());
	}
	
	public void moveTo(int x, int y) {
		Grid grid = (Grid)getSimulation().getUniverse();
		setCellX(grid.getBoundsPolicy().boundX(x));
		setCellY(grid.getBoundsPolicy().boundY(y));
	}
	
	public boolean isAt(int cellX, int cellY) {
		return (getCellX() == cellX) && (getCellY() == cellY);
	}
	
	public boolean isAt(HasGridPosition pos) {
		return (getCellX() == pos.getCellX()) && (getCellY() == pos.getCellY());
	}
	
	public void rotateDelta(double deltaR) {
		rotateTo(getHeading() + deltaR);
	}
	
	public void rotateTo(double r) {
		setHeading(r);
	}
	
	public void moveForward() {
		moveForward(1.0);
	}
	
	public void moveForward(double distance) {
		moveTo(getCellX() + (int)(distance * Math.cos(getHeading())), getCellY() + (int)(distance * Math.sin(getHeading())));
	}
	
	public void turnLeft() {
		turnLeft(0.1);
	}
	
	public void turnLeft(double radians) {
		rotateDelta(-radians);
	}
	
	public void turnRight() {
		turnRight(0.1);
	}
	
	public void turnRight(double radians) {
		rotateDelta(+radians);
	}
	
	public void moveBackward() {
		moveBackward(1);
	}
	
	public void moveBackward(double distance) {
		moveTo(getCellX() - (int)(distance * Math.cos(getHeading())), getCellY() + (int)(distance * Math.sin(getHeading())));
	}
	
	public CellPoint2D getLeft() {
		return new CellPoint2D(getCellX() - 1, getCellY());
	}

	public CellPoint2D getRight() {
		return new CellPoint2D(getCellX() + 1, getCellY());
	}

	public CellPoint2D getUp() {
		return new CellPoint2D(getCellX(), getCellY() - 1);
	}

	public CellPoint2D getDown() {
		return new CellPoint2D(getCellX(), getCellY() + 1);
	}
	
	// Note that moveLeft() is not parameterized with the agent's heading
	public void moveLeft() {
		setCellX(getCellX() - 1);
	}

	// Note that moveRight() is not parameterized with the agent's heading
	public void moveRight() {
		setCellX(getCellX() + 1);
	}

	// Note that moveUp() is not parameterized with the agent's heading
	public void moveUp() {
		setCellX(getCellY() - 1);
	}
	
	// Note that moveDown() is not parameterized with the agent's heading
	public void moveDown() {
		setCellX(getCellX() + 1);
	}

	public void moveRandomly() {
		moveRandomly(1);
	}
	
	public void moveRandomly(int maxDist) {
		Random random = new Random();
		moveDelta(maxDist - random.nextInt(2*maxDist+1), maxDist - random.nextInt(2*maxDist+1));
	}

	public void moveRandomly(int maxDistX, int maxDistY) {
		Random random = new Random();
		moveDelta(maxDistX - random.nextInt(2*maxDistX+1), maxDistY - random.nextInt(2*maxDistY+1));
	}
	
	public void placeRandomly() {
		Grid grid = (Grid)getSimulation().getUniverse();
		Random random = new Random();
		int x = random.nextInt(grid.getWidthInCells());
		int y = random.nextInt(grid.getHeightInCells());
		moveTo(x, y);

		double r = random.nextDouble() * Math.PI * 2.0;
		rotateTo(r);
	}
	
	/** 
	 * Place this agent in a random position within the bounding rectangle provided in the parameters
	 */
	public void placeRandomly(int x1, int y1, int x2, int y2) {
		Grid grid = (Grid)getSimulation().getUniverse();
		Random random = new Random();
		int x = x1 + random.nextInt(x2-x1);
		int y = y1 + random.nextInt(y2-y1);
		moveTo(x, y);

		double r = random.nextDouble() * Math.PI * 2.0;
		rotateTo(r);
	}

}
