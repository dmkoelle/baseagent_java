package org.baseagent.embodied;

import org.baseagent.behaviors.Behavior;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;

public interface HasBody {
	public Grid getBody();
	public Behavior getBodyLogic();
	public GridLayer getParentGridLayer();
}
