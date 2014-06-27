package engine.index.vo;
/**
 *  Bookmark를 저장한 후 indexing 한 결과를 저장하는 Bean
 * @author KIMSEONHO
 *
 */
public class IndexResultDataBean {

	/**
	 * 북마크의 총 url 갯수
	 */
	private int totalBookmarkSize;
	/**
	 * 총 indexing된 URL수
	 */
	private int totalURLListSize;
	/**
	 * 북마크 url중 실제 indexing된 url수
	 */
	private int assignedIdxNum;
	/**
	 * 해당 contributor가 총 등록한 url수
	 */
	private int totalAssignedIdxNum;
	
	/**
	 * @return the totalBookmarkSize
	 */
	public final int getTotalBookmarkSize() {
		return totalBookmarkSize;
	}
	/**
	 * @param totalBookmarkSize the totalBookmarkSize to set
	 */
	public final void setTotalBookmarkSize(int totalBookmarkSize) {
		this.totalBookmarkSize = totalBookmarkSize;
	}
	/**
	 * @return the totalURLListSize
	 */
	public final int getTotalURLListSize() {
		return totalURLListSize;
	}
	/**
	 * @param totalURLListSize the totalURLListSize to set
	 */
	public final void setTotalURLListSize(int totalURLListSize) {
		this.totalURLListSize = totalURLListSize;
	}
	/**
	 * @return the assignedIdxNum
	 */
	public final int getAssignedIdxNum() {
		return assignedIdxNum;
	}
	/**
	 * @param assignedIdxNum the assignedIdxNum to set
	 */
	public final void setAssignedIdxNum(int assignedIdxNum) {
		this.assignedIdxNum = assignedIdxNum;
	}
	public int getTotalAssignedIdxNum() {
		return totalAssignedIdxNum;
	}
	public void setTotalAssignedIdxNum(int totalAssignedIdxNum) {
		this.totalAssignedIdxNum = totalAssignedIdxNum;
	}

}
