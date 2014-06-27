package engine.index.vo;
/**
 * crawling된 후 Indexing 할 데이터에 대한 Bean
 * @see doc/indexField표
 * @author KIMSEONHO
 *
 */
public class IndexDataBean {

	/**
	 * 문서 판별을 위한 고유 index
	 */
	private int idxNum;
	/**
	 * 웹 주소
	 */
	private String url;
	/**
	 * 웹 문서 제목
	 */
	private String title;
	/**
	 * 수정된 날짜
	 */
	private String modifiedDate;
	/**
	 * 최초 등록 날짜
	 */
	private String registeredDate;
	/**
	 * 공유한 횟수
	 */
	private int hit;
	
	/**
	 * @return the idxNum
	 */
	public final int getIdxNum() {
		return idxNum;
	}
	/**
	 * @param idxNum the idxNum to set
	 */
	public final void setIdxNum(int idxNum) {
		this.idxNum = idxNum;
	}
	/**
	 * @return the url
	 */
	public final String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public final void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public final void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the hit
	 */
	public final int getHit() {
		return hit;
	}
	/**
	 * @param hit the hit to set
	 */
	public final void setHit(int hit) {
		this.hit = hit;
	}
	/**
	 * @return the modifiedDate
	 */
	public final String getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public final void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	/**
	 * @return the registeredDate
	 */
	public final String getRegisteredDate() {
		return registeredDate;
	}
	/**
	 * @param registeredDate the registeredDate to set
	 */
	public final void setRegisteredDate(String registeredDate) {
		this.registeredDate = registeredDate;
	}
}
