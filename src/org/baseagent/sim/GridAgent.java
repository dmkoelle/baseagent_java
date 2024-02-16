package org.baseagent.sim;

import java.util.List;
import java.util.Random;

import org.baseagent.behaviors.Behavior;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridPosition;
import org.baseagent.grid.HasFineGridPosition;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.ui.Drawable;
import org.baseagent.ui.DrawableAgent;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.defaults.VisualizationLibrary;
import org.baseagent.util.BaseAgentMath;
import org.baseagent.util.CellPoint2D;
import org.baseagent.util.Vector2D;

import javafx.scene.paint.Color;

public class GridAgent extends DrawableAgent implements HasFineGridPosition {
	private int cellX;
	private int cellY;
	private double heading;
	private String gridLayerName;
	
	// Moving subsystem
	private double fineX;
	private double fineY;
	private HasGridPosition destinationPoint;
	private double movingSpeed;
	private int xFactor, yFactor;
	
	public GridAgent() {
		super();
		setDrawable(new Drawable() {
			@Override
			public void draw(GridCanvasContext gcc) {
//				VisualizationLibrary.drawTriangleWithHeading(gcc.getGraphicsContext(), getCellX(), getCellY(), gcc.getCellWidth(), gcc.getCellHeight(), getHeading(), getColorOrUse(Color.CADETBLUE), getColorOrUse(Color.CADETBLUE).darker());
				VisualizationLibrary.drawTriangleWithHeading(gcc.getGraphicsContext(), getFineX() * gcc.getXFactor(), getFineY() * gcc.getYFactor(), gcc.getCellWidth(), gcc.getCellHeight(), getHeading(), getColorOrUse(Color.CADETBLUE), getColorOrUse(Color.CADETBLUE).darker());
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
	
	@Override
	public double getFineX() {
		return this.fineX;
	}
	
	@Override
	public double getFineY() {
		return this.fineY;
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
		this.fineX = x;
	}
	
	@Override
	public void setCellY(int y) {
		this.cellY = y;
		this.fineY = y;
	}
	
	@Override
	public void setFineX(double x) {
		this.fineX = x;
		this.cellX = (int)Math.round(x);
	}
	
	@Override
	public void setFineY(double y) {
		this.fineY = y;
		this.cellY = (int)Math.round(y);
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

	/** Get any grid layer that the grid knows about */
	public GridLayer getGridLayer(String gridLayerName) {
		return getGrid().getGridLayer(gridLayerName);
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
		this.fineX = getCellX();
		this.fineY = getCellY();
	}

	public boolean continueMovingToward() {
		if (BaseAgentMath.distance(destinationPoint, this) <= movingSpeed) {
			this.cellX = destinationPoint.getCellX();
			this.cellY = destinationPoint.getCellY();
			return true;
		} else {
			setHeading(BaseAgentMath.direction(this, this.destinationPoint));
			this.fineX += movingSpeed * Math.cos(getHeading());
			this.fineY += movingSpeed * Math.sin(getHeading());
			moveTo((int)fineX, (int)fineY);
			return false;
		}
	}
	
	public void moveToward(int destinationX, int destinationY) {
		moveToward(destinationX, destinationY, 1.0);
	}

	public void moveToward(int destinationX, int destinationY, double speed) {
		double direction = BaseAgentMath.direction(this, destinationX, destinationY);
		moveAlong(speed, direction); 
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
		this.cellX = grid.getBoundsPolicy().boundX(x);
		this.cellY = grid.getBoundsPolicy().boundY(y);
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
	
	/** I'm including placeAt because moveTo might imply actual movement */
	public void placeAt(int cellX, int cellY) {
		moveTo(cellX, cellY);
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
	
	//
	//
	// Turtle Functions
	//
	//
	
	public void turnLeft() { this.rotateDelta(-BaseAgentMath.HALF_PI); }
	public void turnRight() { this.rotateDelta(BaseAgentMath.HALF_PI); }
	public void turnAround() { this.rotateDelta(BaseAgentMath.PI); }
	public void moveLeft() { this.cellX = getGrid().getBoundsPolicy().boundX(this.cellX - 1); setHeading(BaseAgentMath.PI); }
	public void moveRight() { this.cellX = getGrid().getBoundsPolicy().boundX(this.cellX + 1); setHeading(0); }
	public void moveUp() { this.cellY = getGrid().getBoundsPolicy().boundY(this.cellY - 1); setHeading(BaseAgentMath.HALF_PI); }
	public void moveDown() { this.cellY = getGrid().getBoundsPolicy().boundY(this.cellY + 1); setHeading(BaseAgentMath.THREE_HALF_PI); }
	public void moveForward() { this.moveForward(1.0); }
	public void moveForward(double distance) { moveTo(this.cellX + (int)(distance * Math.cos(getHeading())), this.cellY + (int)(distance * Math.sin(getHeading()))); }
	public void moveBackward() { this.moveBackward(1.0); }
	public void moveBackward(double distance) { moveTo(this.cellX - (int)(distance * Math.cos(getHeading())), this.cellY + (int)(distance * Math.sin(getHeading()))); }
	public boolean isOn(Object value) { return isOn(Grid.DEFAULT_GRID_LAYER, value); }
    public boolean isOn(String gridLayerName, Object value) { return getGrid().getGridLayer(gridLayerName).get(getCellX(), getCellY()).equals(value); }
    public void setCell(Object value) { setCell(Grid.DEFAULT_GRID_LAYER, value); }
    public void setCell(String gridLayerName, Object value) { getGrid().getGridLayer(gridLayerName).set(getCellX(), getCellY(), value); }

    

    
    //
    //
	// Inventory subsystem and its interaction with GridLayers
	//
    //
	
	public void take(String layerName, Object thing, Object replacement) {
		getGrid().getGridLayer(layerName).set(getCellX(), getCellY(), replacement);
		getInventory().add(thing);
	}

	public void drop(String layerName, Object thing) {
		getGrid().getGridLayer(layerName).set(getCellX(), getCellY(), thing);
		getInventory().remove(thing);
	}

}
