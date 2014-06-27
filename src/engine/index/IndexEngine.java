package engine.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import common.PropertiesContainer;

import engine.index.dao.Indexer.BasicIndexer;
import engine.index.dao.bookmarkParser.BookmarkParser;
import engine.index.dao.crawler.BasicCrawlController;
import engine.index.vo.IndexDataBean;
import engine.index.vo.IndexResultDataBean;

/**
 * bookmark 파일과 contributor를 읽어서 crawling, indexing 과정을 함
 * @author KIMSEONHO
 *
 */
public class IndexEngine {

	/**
	 * crawling, indexing 과정 수행
	 * 
	 * @param String filename	bookmark 파일 이름
	 * @param String donator	contributor 이름
	 */
	public IndexResultDataBean start(String filename, String donator) {
		// TODO Auto-generated method stub
		//String filename = "/data/bookmark/chrome/Bookmarks_13.12.04";		// 북마크 저장 경로
		/**
		 * Crawler가 가동할 thread 수
		 * crawler thread 수 - 5개(기본)
		 */
		int numberOfCrawlers = 
				Integer.parseInt(PropertiesContainer.prop.getProperty("numberOfCrawler"));
		
		/**
		 * crawling한 문서를 저장할 경로
		 */
		String crawlStorageFolder = PropertiesContainer.prop.getProperty("crawlStorageFolder");
		/*
		 * "/data/root";		// crawl한 문서 저장 경로
		 */
		
		/**
		 * index할 문서 저장 경로
		 */
		String indexPath = PropertiesContainer.prop.getProperty("indexPath");
		/*
		 * "/data/indexs";		// index할 문서 저장 경로
		 */
		
		/**
		 * index된 문서의 저장경로
		 */
		String docsPath = PropertiesContainer.prop.getProperty("docsPath");
		/*
		 * "/data/docs/content";		// index된 문서의 저장 경로
		 */
		
		/**
		 * url의 index와 데이터 쌍을 저장
		 */
		HashMap<Integer, IndexDataBean> newIdxBeanMap;
		/**
		 * update할 idx와 idx에 해당하는 Hit수를 matching
		 */
		HashMap<Integer, Integer> updateIdxNHit;
		/**
		 * 새로 저장된 문서 이름
		 */
		List<Integer> newIdxList;
		/**
		 * 업데이트된 문서 이름
		 */
		ArrayList<Integer> updateIdxList;
		
		String contributor = null;
		
		/**
		 * 북마크에 저장된 url 갯수
		 */
		int totalBookmarkSize = 0;
		/**
		 */
		int totalURLListSize = 0;
		/**
		 * 등록한 북마크중 실제로 검색엔진에 등록된 URL수
		 */
		int assignedIdxNum = 0;
		
		IndexResultDataBean indexResultDataBean = new IndexResultDataBean();
		BookmarkParser bookmarkParser = new BookmarkParser();		
		
		BasicCrawlController basicCrawlController = new BasicCrawlController();
			
		/*
		 * paring bookmark file to url
		 */
		JSONArray parsingURL = bookmarkParser.Parser(filename);
		
		/*
		 *  crawling start
		 */
		basicCrawlController.start(parsingURL, donator, crawlStorageFolder, numberOfCrawlers);
		
		
		newIdxBeanMap = new HashMap<Integer, IndexDataBean>(BasicCrawlController.getNewIdxMap());
		updateIdxNHit = new HashMap<Integer, Integer>(BasicCrawlController.getUpdateIdxNHit());
		newIdxList = new ArrayList<Integer>(BasicCrawlController.getNewIdxList());
		updateIdxList = new ArrayList<Integer>(BasicCrawlController.getUpdateIdxList());
		
		// 값들을 index에 반영하기 위해서 옮김
		totalBookmarkSize = BasicCrawlController.getTotalBookmarkSize();		// 등록한 북마크의 url 갯수
		totalURLListSize = BasicCrawlController.getTotalURLListSize();		// 현재 서버에 등록된 url 갯수
		assignedIdxNum = BasicCrawlController.getNewIdxList().size();		// 실제 인덱스에 반영될 문서의 갯수
		contributor = donator;
		///////////////////////////////////////////////////////////////////////////////////
		BasicIndexer basicIndexer = new BasicIndexer(indexPath, docsPath, contributor, newIdxBeanMap, updateIdxNHit, newIdxList, updateIdxList);	
		int indexCount = basicIndexer.index();		// indexer -> index된 갯수
		//////////////////////////////////////////////////////////////////////////////////
		System.out.println("number of Indexing doc : " + indexCount);
		
		indexResultDataBean.setTotalBookmarkSize(totalBookmarkSize);
		indexResultDataBean.setTotalURLListSize(totalURLListSize);
		indexResultDataBean.setAssignedIdxNum(assignedIdxNum);
		
		return indexResultDataBean;
	}

}
