package edu.purdue.cs.tornado.serializer;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.purdue.cs.tornado.helper.IndexCell;
import edu.purdue.cs.tornado.helper.IndexCell.IndexCellType;
import edu.purdue.cs.tornado.helper.IndexCellCoordinates;
import edu.purdue.cs.tornado.helper.Rectangle;

public class IndexCellSerializer extends com.esotericsoftware.kryo.Serializer<IndexCell> {
	@Override
	public IndexCell read(Kryo kryo, Input input, Class<IndexCell> type) {

		IndexCell indexCell = new IndexCell();
		indexCell.storedObjects = kryo.readObjectOrNull(input, HashMap.class);
		indexCell.allDataTextInCell = kryo.readObjectOrNull(input, HashMap.class);
		indexCell.allQueryTextInCell = kryo.readObjectOrNull(input, HashMap.class);
		indexCell.storedQueries = (kryo.readObjectOrNull(input, ArrayList.class));
		indexCell.outerEvaluatorQueries = kryo.readObjectOrNull(input, ArrayList.class);
		indexCell.queriesInvertedList = kryo.readObjectOrNull(input, HashMap.class);
		indexCell.bounds = kryo.readObjectOrNull(input, Rectangle.class);
		indexCell.dataObjectCount = kryo.readObjectOrNull(input, Integer.class);
		indexCell.queryCount = kryo.readObjectOrNull(input, Integer.class);
		indexCell.spatialOnlyFlag = kryo.readObjectOrNull(input, Boolean.class);
		indexCell.indexCellType = kryo.readObjectOrNull(input, IndexCellType.class);
		indexCell.level = kryo.readObjectOrNull(input, Integer.class);
		indexCell.globalCoordinates = kryo.readObjectOrNull(input, IndexCellCoordinates.class);
		indexCell.indexCellCost = kryo.readObjectOrNull(input, Integer.class);
		indexCell.minExpireTime = kryo.readObjectOrNull(input, Long.class);
		return indexCell;
	}

	@Override
	public void write(Kryo kryo, Output output, IndexCell indexCell) {
		kryo.writeObjectOrNull(output, indexCell.storedObjects, HashMap.class);
		kryo.writeObjectOrNull(output, indexCell.allDataTextInCell, HashMap.class);
		kryo.writeObjectOrNull(output, indexCell.allQueryTextInCell, HashMap.class);
		kryo.writeObjectOrNull(output, indexCell.storedQueries, ArrayList.class);
		kryo.writeObjectOrNull(output, indexCell.outerEvaluatorQueries, ArrayList.class);
		kryo.writeObjectOrNull(output, indexCell.queriesInvertedList, HashMap.class);
		kryo.writeObjectOrNull(output, indexCell.bounds, Rectangle.class);
		kryo.writeObjectOrNull(output, indexCell.dataObjectCount, Integer.class);
		kryo.writeObjectOrNull(output, indexCell.queryCount, Integer.class);
		kryo.writeObjectOrNull(output, indexCell.spatialOnlyFlag, Boolean.class);
		kryo.writeObjectOrNull(output, indexCell.indexCellType, IndexCellType.class);
		kryo.writeObjectOrNull(output, indexCell.level, Integer.class);
		kryo.writeObjectOrNull(output, indexCell.globalCoordinates, IndexCellCoordinates.class);
		kryo.writeObjectOrNull(output, indexCell.indexCellCost, Integer.class);
		kryo.writeObjectOrNull(output, indexCell.minExpireTime, Long.class);
	}

}
