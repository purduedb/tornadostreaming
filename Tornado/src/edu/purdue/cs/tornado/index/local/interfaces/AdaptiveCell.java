package edu.purdue.cs.tornado.index.local.interfaces;

import edu.purdue.cs.tornado.helper.IndexCellCoordinates;

public interface AdaptiveCell {
	public IndexCellCoordinates getGlobalCoordinates();

	public void setGlobalCoordinates(IndexCellCoordinates globalCoordinates);

	public Integer getIndexCellCost();

	public void setIndexCellCost(Integer indexCellCost);

	public boolean isTransmitted();

	public void setTransmitted(boolean transmitted);

}
