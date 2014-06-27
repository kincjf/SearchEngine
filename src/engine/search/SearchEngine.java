package engine.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import common.PropertiesContainer;

import engine.search.query.CountingQuery;
import engine.search.vo.SearchDataBean;

/**
 * Query에 대한 
 * @author KIMSEONHO
 *
 */
public class SearchEngine {

	/**
	 * @param args
	 */
	int totalHits = 0;;
	int hitsPerPage = 10; // 기본 10, 최대 50
	/** Simple command-line based search demo. */
	long searchTime = 0;
	int multiply = 5;

	public SearchEngine(int hitPerPage, int multiply) {
		this.hitsPerPage = hitPerPage;
		this.multiply = multiply;
		// 페이지 뷰 갯수 지정, 단위 검색 갯수 지정 ( 설정량의 [multiply]배)
	}

	public SearchEngine() {
	}

	/**
	 * query에 대한 검색 수행후 Score별로 검색된 상위문서 반환
	 * @param inputQuery		query for searching
	 * @param page				page navigator - 1, 2, 3, 4, 5 / 10개 단위
	 * @return					doc data list - maximum size : 50
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public ArrayList<SearchDataBean> search(String inputQuery, int page)
			throws Exception { // hitPerPage(단위) 만큼 잘라서 출력한다.

		String index = PropertiesContainer.prop.getProperty("indexPath"); // index
																			// dir
		String[] searchingFields = {"contents", "title"}; // 검색할 필드 지정 (url, title, contents....)
		// the "title" arg specifies the default fieuxld to use
		// when no field is explicitly specified in the query.
		String queryFile = null; // query using file
		
		/* 그냥 System.out.println 으로 보여줄건가? 에 대한 변수 */
		boolean raw = false;		
		String queryString = inputQuery; // queries using string

		Date searchStartTime = new Date(); // 시간 측정을 위해서 시작 시간 측정

		ArrayList<SearchDataBean> searchDataArray = new ArrayList<SearchDataBean>();

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);		// LUCENE_40
		// contents field, title field에 대한 query 수행
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_CURRENT, searchingFields, analyzer);
		
		while (true) {

			String queryLine = queryString != null ? queryString : "hit-it";
			// query가 안들어오면 수행하지 않음 - keyword hitit 검색
			if (queryLine == null || queryLine.length() == -1) {
				break;
			}

			queryLine = queryLine.trim(); // 양쪽 공백 제거 -> 빈 문자열 제거( 클라이언트 상에서도 가능)
			if (queryLine.length() == 0) {
				break;
			}

			Query query = parser.parse(queryLine);
			System.out.println("SearchEngine - Searching for: " + query.toString());

			totalHits = doPagingSearch(searcher, query, hitsPerPage, raw,
					(queryFile == null && queryString == null),
					searchDataArray, page);
			// interactive - 상호작용?? => query를 수정하면서 질의검색이 가능한가?
			// 일단은 불가한걸로 지정
			if (queryString != null) {
				break;
			}
		}
		reader.close();

		Date searchEndTime = new Date();

		System.out.println("SearchEngine - Query : " + queryString);

		searchTime = (searchEndTime.getTime() - searchStartTime.getTime());
		System.out.println("SearchEngine - Time : " + searchTime + "ms");

		return searchDataArray;
	}
	/**
	 * 
	 * @param searcher			indexSearcher
	 * @param query				Searching terms
	 * @param hitsPerPage		기본 View 단위
	 * @param raw				print data in console
	 * @param interactive		available dynamic Querying?
	 * @param searchDataArray	Container of SearchDataBean
	 * @param page				page navigator - 1, 2, 3, 4, 5 / 10개 단위
	 * @return					in queries, how many docs hit? maxinum size : 50
	 * @throws IOException
	 */
	public int doPagingSearch(IndexSearcher searcher,
			Query query, int hitsPerPage, boolean raw, boolean interactive,
			ArrayList<SearchDataBean> searchDataArray, int page) throws IOException {
		// 제한 페이지 - [multiply]까지
		// Collect enough docs to show 5 pages
		
		CountingQuery ct = new CountingQuery(query);
		
		TopDocs results = searcher.search(ct, multiply * hitsPerPage); 
		// [multiply] * 페이지수만큼 검색한다.
		ScoreDoc[] hitDocs = results.scoreDocs;
		// 점수순으로 내림차순 정렬해서 보여준다.

		int numTotalHits = results.totalHits;
		System.out.println("SearchEngine - " + numTotalHits + " total matching documents");

		//int page = 1;		// 기본 1, page를 이용해서 페이지뷰를 조절한다.
		
		/* 10개씩 보기 */
		// int start = Math.max(0, (page - 1) * hitsPerPage);		/* 시작 페이지 인덱스 */
		// int end = Math.min(numTotalHits, start + hitsPerPage);		/* 종료 페이지 인덱스 */
		
		/* 최대 50개씩 보기 */
		int start = 0;		/* 시작 페이지 인덱스 */
		int end = Math.min(numTotalHits, (start + hitsPerPage * multiply));		/* 종료 페이지 인덱스 */
		
		SearchDataBean searchDataBean = null;

		while (true) {

			for (int i = start; i < end; i++) { // start, end를 조정해서 페이지뷰 갯수를 조정할
												// 수 있다.
				if (raw) { /* output raw format - 그냥 print한다.*/ 
					System.out.println("SearchEngine - doc=" + hitDocs[i].doc + " score="
							+ hitDocs[i].score);
					continue;
				}

				Document doc = searcher.doc(hitDocs[i].doc);		// 순위가 매겨진 hit(i)의 문서
				// String path = doc.get("path");

				String title = doc.get("title");
				if (title != null) {
					String url = doc.get("url");
					if (url != null) {
						String contributors = doc.get("contributor");
						String hitNum = doc.get("hit");
						
						System.out.println("SearchEngine - Title: " + title);
						System.out.println("             - URL: " + url);
						System.out.println("             - hitNum: " + hitNum);
						System.out.println("             - contributors: " + contributors);
						
						searchDataBean = new SearchDataBean();
						
						searchDataBean.setTitle(title);
						searchDataBean.setUrl(url);
						searchDataBean.setHits(Integer.parseInt(hitNum));
						searchDataBean.setContributors(contributors);
						
						searchDataArray.add(searchDataBean);
					}
				} else {
					System.out.println("SearchEngine - " + (i + 1) + ". "
							+ "No title for this document");
				}
			}

			if (!interactive || end == 0) {
				break;
			}

		}
		return numTotalHits; // 총 hit 수를 반환한다.
	}

	/**
	 * @return the totalHits
	 */
	public final int getTotalHits() {
		return totalHits;
	}

	/**
	 * @return the hitsPerPage
	 */
	public final int getHitsPerPage() {
		return hitsPerPage;
	}

	public long getSearchTime() {
		// TODO Auto-generated method stub
		return searchTime;
	}
}
