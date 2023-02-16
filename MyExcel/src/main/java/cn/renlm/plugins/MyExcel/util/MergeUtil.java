package cn.renlm.plugins.MyExcel.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.experimental.UtilityClass;

/**
 * 合并工具
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class MergeUtil {

	/**
	 * 多行复杂表头单元格合并处理
	 * 
	 * @param sh
	 * @param points
	 */
	public static final void mergeComplexTitle(final Sheet sh, final MergeCellPoints points) {
		for (CellRangeAddress merge : points.getRanges()) {
			sh.addMergedRegion(merge);
		}
	}

	/**
	 * 获取合并区域列表
	 * 
	 * @param titleRowPos
	 * @param rowNbr
	 * @param titleRowColMap
	 * @return
	 */
	public static final MergeCellPoints findCellRangeAddress(final int titleRowPos, final int rowNbr,
			final Map<String, List<Integer[]>> titleRowColMap) {
		MergeCellPoints points = MergeCellPoints.builder().build();
		for (Map.Entry<String, List<Integer[]>> titleEntry : titleRowColMap.entrySet()) {
			List<Integer[]> rowColList = titleEntry.getValue();
			if (rowColList.size() > 1) {
				MergeArea ma = MergeArea.builder().title(titleEntry.getKey()).titleRowPos(titleRowPos)
						.titleRowNbr(rowNbr).build();
				for (Integer[] point : rowColList) {
					int rowIndex = point[0];
					int colIndex = point[1];
					ma.pushRowColPoint(rowIndex, colIndex);
					ma.pushColRowPoint(rowIndex, colIndex);
				}
				for (Integer[] point : rowColList) {
					int rowIndex = point[0];
					int colIndex = point[1];
					Map<Integer, Map<Integer, String>> exMap = points.getExcludeRowColMap();
					if (exMap.containsKey(rowIndex) && exMap.get(rowIndex).containsKey(colIndex)) {
						continue;
					}
					changeMergeArea(ma, rowIndex, colIndex, points);
				}
			}
		}
		return points;
	}

	/**
	 * 获取单元格所在区域的第一个单元格
	 * 
	 * @param sh
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	public static final Cell findFirstCellOfUnitRegion(final Sheet sh, final int rowIndex, final int columnIndex) {
		CellRangeAddress area = findUnitRegion(sh, rowIndex, columnIndex);
		return sh.getRow(area.getFirstRow()).getCell(area.getFirstColumn());
	}

	/**
	 * 标题坐标映射
	 * 
	 * @param map
	 * @param title
	 * @param point
	 */
	public static final void pushTitleRowColMap(Map<String, List<Integer[]>> map, String title, Integer[] point) {
		List<Integer[]> list = map.get(title);
		if (list == null) {
			list = new ArrayList<>();
			map.put(title, list);
		}
		list.add(point);
	}

	/**
	 * 单元格合并区域点集
	 */
	@Data
	@Builder
	private static final class MergeCellPoints {
		@Default
		private Map<String, CellRangeAddress> pools = new LinkedHashMap<>();

		/**
		 * 合并区集合
		 */
		@Default
		private List<CellRangeAddress> ranges = new ArrayList<>();

		/**
		 * 排除处理点集（已被钻取处理过）
		 */
		@Default
		private Map<Integer, Map<Integer, String>> excludeRowColMap = new LinkedHashMap<>();

		/**
		 * 排除指定点（已被钻取处理过）
		 * 
		 * @param rowIndex
		 * @param colIndex
		 * @param title
		 */
		public void exclude(int rowIndex, int colIndex, String title) {
			pushPointTitleMap(excludeRowColMap, rowIndex, colIndex, title);
		}
	}

	/**
	 * 单元格合并区域
	 */
	@Data
	@Builder
	private static final class MergeArea {

		/**
		 * 单元格内容
		 */
		private String title;

		/**
		 * 标题起始行
		 */
		private int titleRowPos;

		/**
		 * 标题行数
		 */
		private int titleRowNbr;

		/**
		 * 单元格合并起始行
		 */
		@Default
		private Integer firstRow = -100;

		/**
		 * 单元格合并结束行
		 */
		@Default
		private Integer lastRow = -100;

		/**
		 * 单元格合并起始列
		 */
		@Default
		private Integer firstCol = -100;

		/**
		 * 单元格合并结束列
		 */
		@Default
		private Integer lastCol = -100;

		/**
		 * 上一单元格行号
		 */
		@Default
		private Integer prevRowIndex = -100;

		/**
		 * 上一单元格列号
		 */
		@Default
		private Integer prevColIndex = -100;

		/**
		 * 行列映射
		 */
		@Default
		private Map<Integer, Map<Integer, String>> _RowColPointMap = new LinkedHashMap<>();

		/**
		 * 列行映射
		 */
		@Default
		private Map<Integer, Map<Integer, String>> _ColRowPointMap = new LinkedHashMap<>();

		/**
		 * 重置区域
		 */
		public void reset() {
			this.firstRow = -100;
			this.lastRow = -100;
			this.firstCol = -100;
			this.lastCol = -100;
			this.prevRowIndex = -100;
			this.prevColIndex = -100;
		}

		/**
		 * 添加行列映射
		 * 
		 * @param rowIndex
		 * @param colIndex
		 */
		public void pushRowColPoint(int rowIndex, int colIndex) {
			pushPointTitleMap(_RowColPointMap, rowIndex, colIndex, title);
		}

		/**
		 * 添加列行映射
		 * 
		 * @param rowIndex
		 * @param colIndex
		 */
		public void pushColRowPoint(int rowIndex, int colIndex) {
			pushPointTitleMap(_ColRowPointMap, colIndex, rowIndex, title);
		}
	}

	/**
	 * 获取单元格所在区域
	 * 
	 * @param sh
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	private static final CellRangeAddress findUnitRegion(final Sheet sh, final int rowIndex, final int columnIndex) {
		int sheetMergeCount = sh.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sh.getMergedRegion(i);
			int firstCol = range.getFirstColumn();
			int lastCol = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (rowIndex >= firstRow && rowIndex <= lastRow && columnIndex >= firstCol && columnIndex <= lastCol) {
				return new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
			}
		}
		return new CellRangeAddress(rowIndex, rowIndex, columnIndex, columnIndex);
	}

	/**
	 * 坐标标题映射
	 * 
	 * @param map
	 * @param key1
	 * @param key2
	 * @param title
	 */
	private static final void pushPointTitleMap(Map<Integer, Map<Integer, String>> map, int key1, int key2,
			String title) {
		Map<Integer, String> map2 = map.get(key1);
		if (map2 == null) {
			map2 = new LinkedHashMap<>();
			map.put(key1, map2);
		}
		map2.put(key2, title);
	}

	/**
	 * 合并区域变化处理（连续的单元格才能合并）
	 * 
	 * @param ma
	 * @param rowIndex
	 * @param colIndex
	 * @param mp
	 */
	private static final void changeMergeArea(final MergeArea ma, final int rowIndex, final int colIndex,
			MergeCellPoints mp) {
		boolean isMerge = false;
		boolean isAddToCellRangeAddress = true;
		final Map<Integer, Map<Integer, String>> _RowColPointMap = ma.get_RowColPointMap();
		final Map<Integer, Map<Integer, String>> _ColRowPointMap = ma.get_ColRowPointMap();
		final int rowCount = _RowColPointMap.size();
		final int colCount = _ColRowPointMap.size();

		if (rowCount == 1 && colCount == 1) {
			ma.reset();
			return;
		} else if (rowCount == 1 || colCount == 1) {
			isMerge = isPointOfMergeSingleRowOrCol(ma, rowIndex, colIndex);
		} else {
			Boolean[] multiBools = isPointOfMergeMultiRowCol(ma, rowIndex, colIndex, mp);
			isMerge = multiBools[0];
			isAddToCellRangeAddress = multiBools[1];
		}

		_actionMergeAreaChange(ma, rowIndex, colIndex);
		if (isMerge) {
			if (isAddToCellRangeAddress) {
				addToCellRangeAddressList(mp, ma);
			}
			ma.reset();
		}
	}

	/**
	 * 合并区域变化动作
	 * 
	 * @param ma
	 * @param rowIndex
	 * @param colIndex
	 */
	private static final void _actionMergeAreaChange(final MergeArea ma, final int rowIndex, final int colIndex) {
		ma.setFirstRow(ma.getFirstRow() < 0 ? rowIndex : Math.min(ma.getFirstRow(), rowIndex));
		ma.setLastRow(ma.getLastRow() < 0 ? rowIndex : Math.max(ma.getLastRow(), rowIndex));
		ma.setFirstCol(ma.getFirstCol() < 0 ? colIndex : Math.min(ma.getFirstCol(), colIndex));
		ma.setLastCol(ma.getLastCol() < 0 ? colIndex : Math.max(ma.getLastCol(), colIndex));
		ma.setPrevRowIndex(rowIndex);
		ma.setPrevColIndex(colIndex);
	}

	/**
	 * 是否合并点,单行或者单列（从左向右，从上向下）
	 * 
	 * @param ma
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 */
	private static final boolean isPointOfMergeSingleRowOrCol(final MergeArea ma, final int rowIndex,
			final int colIndex) {
		boolean isMerge = false;
		final Map<Integer, Map<Integer, String>> _RowColPointMap = ma.get_RowColPointMap();
		final Map<Integer, Map<Integer, String>> _ColRowPointMap = ma.get_ColRowPointMap();

		if (_RowColPointMap.size() == 1) { // 单行
			isMerge = !_RowColPointMap.get(rowIndex).containsKey(colIndex + 1);
		} else if (_ColRowPointMap.size() == 1) { // 单列
			isMerge = !_ColRowPointMap.get(colIndex).containsKey(rowIndex + 1);
		}
		return isMerge;
	}

	/**
	 * 是否合并点,多行多列（从左向右，从上向下，列合并优先）
	 * 
	 * @param ma
	 * @param rowIndex
	 * @param colIndex
	 * @param mp
	 * @return
	 */
	private static final Boolean[] isPointOfMergeMultiRowCol(final MergeArea ma, final int rowIndex, final int colIndex,
			MergeCellPoints mp) {
		boolean isMerge = false;
		boolean isAddToCellRangeAddress = true;
		final Map<Integer, Map<Integer, String>> _RowColPointMap = ma.get_RowColPointMap();

		boolean isOverRow = !_RowColPointMap.get(rowIndex).containsKey(colIndex + 1);
		boolean isLastRow = !_RowColPointMap.containsKey(rowIndex + 1);
		if (isOverRow) {
			final Map<Integer, String> _ColPointMap = _RowColPointMap.get(rowIndex);
			if (isLastRow) {
				isMerge = true;
			} else {
				final Map<Integer, String> _ColPointMapOfNextRow = _RowColPointMap.get(rowIndex + 1);
				for (int i = colIndex; _ColPointMap.containsKey(i); i--) {
					if (!_ColPointMapOfNextRow.containsKey(i)) {
						isMerge = true;
						break;
					}
				}
				if (!isMerge) { // 列合并结束但未完成,向下钻取合并
					_actionMergeAreaChange(ma, rowIndex, colIndex);
					drillingDown(ma, rowIndex + 1, colIndex, mp);
					isAddToCellRangeAddress = false;
					isMerge = true;
				}
			}
		}
		return new Boolean[] { isMerge, isAddToCellRangeAddress };
	}

	/**
	 * 列合并结束但未完成,向下钻取合并
	 * 
	 * @param ma
	 * @param rowIndex
	 * @param colIndex
	 * @param mp
	 */
	private static final void drillingDown(final MergeArea ma, final int rowIndex, final int colIndex,
			MergeCellPoints mp) {
		boolean isMerge = false;
		int nextRowIndex = rowIndex + 1;
		_actionMergeAreaChange(ma, rowIndex, colIndex);
		final Map<Integer, Map<Integer, String>> _RowColPointMap = ma.get_RowColPointMap();
		boolean isLastRow = !_RowColPointMap.containsKey(nextRowIndex);

		final Map<Integer, String> _ColPointMap = _RowColPointMap.get(rowIndex);
		final Map<Integer, String> _ColPointMapOfNextRow = _RowColPointMap.get(nextRowIndex);
		for (int i = colIndex; _ColPointMap.containsKey(i); i--) {
			if (isLastRow) { // 末级标题只能行合并
				MergeArea temp = MergeArea.builder().title(ma.getTitle()).titleRowPos(ma.getTitleRowPos())
						.titleRowNbr(ma.getTitleRowNbr()).firstRow(ma.getFirstRow()).lastRow(ma.getLastRow())
						.firstCol(i).lastCol(i)._RowColPointMap(ma.get_RowColPointMap())
						._ColRowPointMap(ma.get_ColRowPointMap()).build();
				addToCellRangeAddressList(mp, temp);
				isMerge = true;
			} else {
				if (!_ColPointMapOfNextRow.containsKey(i)) {
					addToCellRangeAddressList(mp, ma);
					isMerge = true;
					break;
				}
			}
		}
		if (!isMerge) {
			drillingDown(ma, nextRowIndex, colIndex, mp);
		}
		if (isLastRow || !isMerge) {
			for (int i = colIndex; _ColPointMap.containsKey(i); i--) {
				mp.exclude(rowIndex, i, ma.getTitle());
			}
		}
	}

	/**
	 * 添加合并区域
	 * 
	 * @param mp
	 * @param ma
	 */
	private static final void addToCellRangeAddressList(final MergeCellPoints mp, final MergeArea ma) {
		int firstRow = ma.getFirstRow();
		int lastRow = ma.getLastRow();
		int firstCol = ma.getFirstCol();
		int lastCol = ma.getLastCol();
		if (firstRow >= 0 && lastRow >= 0 && firstCol >= 0 && lastCol >= 0) {
			if (!(firstRow == lastRow && firstCol == lastCol)) {
				String key = firstRow + File.separator + lastRow + File.separator + firstCol + File.separator + lastCol;
				if (!mp.pools.containsKey(key)) {
					CellRangeAddress area = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
					mp.getRanges().add(area);
					mp.pools.put(key, area);
				}
			}
		}
	}
}