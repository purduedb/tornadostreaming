package edu.purdue.cs.tornado.serializer;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Rectangle;
import edu.purdue.cs.tornado.index.local.hybridgrid.GridIndexCell;

public class GridIndexCellSerializer extends com.esotericsoftware.kryo.Serializer<GridIndexCell> {
	@Override
	public GridIndexCell read(Kryo kryo, Input input, Class<GridIndexCell> type) {

		GridIndexCell indexCell = new GridIndexCell();
		indexCell.setStoredQueries  (kryo.readObjectOrNull(input, ArrayList.class));
		indexCell.setQueriesInvertedList ( kryo.readObjectOrNull(input, HashMap.class));
		indexCell.setBounds ( kryo.readObjectOrNull(input, Rectangle.class));
		indexCell.setGlobalCoordinates ( kryo.readObjectOrNull(input, IndexCellCoordinates.class));
		indexCell.setIndexCellCost(kryo.readObjectOrNull(input, Integer.class));
		indexCell.setMinExpireTime ( kryo.readObjectOrNull(input, Long.class));
		return indexCell;
	}

	@Override
	public void write(Kryo kryo, Output output, GridIndexCell indexCell) {
		kryo.writeObjectOrNull(output, indexCell.getStoredQueries(), ArrayList.class);
		kryo.writeObjectOrNull(output, indexCell.getQueriesInvertedList(), HashMap.class);
		kryo.writeObjectOrNull(output, indexCell.getBounds(), Rectangle.class);
		kryo.writeObjectOrNull(output, indexCell.getGlobalCoordinates(), IndexCellCoordinates.class);
		kryo.writeObjectOrNull(output, indexCell.getIndexCellCost(), Integer.class);
		kryo.writeObjectOrNull(output, indexCell.getMinExpireTime(), Long.class);
	}

}
