package engine.search.query;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;
/**
 * CustomQuery를 수행 - HIT 수를 판별하여 Scoring에 반영함
 * @author KIMSEONHO
 *
 */
public class CountingQuery extends CustomScoreQuery {
	
	public CountingQuery(Query subQuery) {
		super(subQuery);
	}

	public class CountingQueryScoreProvider extends CustomScoreProvider {

		String _field;

		public CountingQueryScoreProvider(String field, AtomicReaderContext context) {
			super(context);
			_field = field;
		}

		// Rescores by counting the number of terms in the field
		/**
		 * customScoring의 핵심부분이다. url을 참조하기 바람. 궁금하면
		 * @see		http://lucene.apache.org/core/3_0_3/api/all/org/apache/lucene/search/function/CustomScoreProvider.html#customScore(int, float, float)
		 * @see 	http://stackoverflow.com/questions/5924937/lucene-custom-scoring-for-numeric-fields
		 * 
		 * @param doc	index of each document
		 * @param subQueryScore		SubQuery로 계산된 Score
		 * @param valSrcScore		valSrcQuery로 계산된 Score (해당 Query를 사용하지 않으면 해당사항 없음)
		 * 
		 * @return luceneScoring(title 가중치 1.5배) + 북마트 등록 횟수(hit) * 0.1 + valSrcScore
		 */
		@Override
		public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {
			
			IndexReader r = context.reader();
			// 검색된 Document set과 각각 doc의 정보에 대하여 가지고 있음
			Document currentDoc = r.document(doc);
			
			int hits = Integer.parseInt(currentDoc.get(this._field));
			// doc가 등록된 횟수
			System.out.println("CountingQuery - title : " + currentDoc.get("title"));
			System.out.println("              - Hits : " + hits);
			
			return (float)(subQueryScore + valSrcScore + hits * 0.1);
		}
	}

	protected CustomScoreProvider getCustomScoreProvider(
			AtomicReaderContext context) throws IOException {
		return new CountingQueryScoreProvider("hit", context);
		// field(hit)에 대하여 customScoring 수행
	}
}
