package engine.search.vo;

/**
 * 입력된 query에 대한 searching후 ui에 출력되는 검색결과에 대한 data 
 * @author KIMSEONHO
 *
 */
public class SearchDataBean {

	String title;
	String url;
	String contributors;
	int hits;
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
	 * @return the contributors
	 */
	public final String getContributors() {
		return contributors;
	}
	/**
	 * @param contributors the contributors to set
	 */
	public final void setContributors(String contributors) {
		this.contributors = contributors;
	}
	/**
	 * @return the hits
	 */
	public final int getHits() {
		return hits;
	}
	/**
	 * @param hits the hits to set
	 */
	public final void setHits(int hits) {
		this.hits = hits;
	}

}
