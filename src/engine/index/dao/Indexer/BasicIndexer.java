package engine.index.dao.Indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import common.FileContainer;
import common.PropertiesContainer;

import engine.index.common.UpdateDocField;
import engine.index.common.VecTextField;
import engine.index.vo.IndexDataBean;

/**
 * indexing title, content, etc field
 * @see doc/indexField표
 * @author KIMSEONHO
 *
 */
public class BasicIndexer {
	/**
	 * index된 파일을 저장할 폴더
	 */
	String indexPath;
	
	/**
	 * index할 파일을 저장한 폴더
	 */
	String docsPath;
	
	/**
	 * matching new url idx and url data(bean)
	 */
	HashMap<Integer, IndexDataBean> newIdxBeanMap;
	
	/**
	 * @link IndexEngine.updateIdxNHit
	 */
	HashMap<Integer, Integer> updateIdxNHit;
	
	/**
	 * @link IndexEngine.newIdxList
	 */
	List<Integer> newIdxList;
	
	/**
	 * @link IndexEngine.updateIdxList;
	 */
	ArrayList<Integer> updateIdxList;

	String contributor;

	/**
	 * index(), indexDocs()[recursive]
	 * 
	 * recursive function에서 이용하기 때문에 전역변수로 선언
	 */
	Iterator<Integer> newIdxListIter;

	/**
	 * default constructor
	 * @param indexPath			indexing file folder path
	 * @param docsPath			contents file folder path
	 * @param contributor		bookmark contributor
	 * @param newIdxBeanMap		@link BasicIndexer.newIdxBeanMap
	 * @param updateIdxNMap		@link BasicIndexer.updateIdxNMap
	 * @param newIdxList		@link BasicIndexer.newIdxList
	 * @param updateIdxList		@link BasicIndexer.updateIdxList
	 */
	public BasicIndexer(String indexPath, String docsPath, String contributor,
			HashMap<Integer, IndexDataBean> newIdxBeanMap,
			HashMap<Integer, Integer> updateIdxNMap, List<Integer> newIdxList,
			ArrayList<Integer> updateIdxList) {
		// indexPath, docsPath, contributor, newIdxBeanMap, updateIdxNHit,
		// newIdxList, updateIdxList
		this.indexPath = indexPath;
		this.docsPath = docsPath;
		this.newIdxBeanMap = new HashMap<Integer, IndexDataBean>(newIdxBeanMap);
		this.updateIdxNHit = new HashMap<Integer, Integer>(updateIdxNMap);
		this.newIdxList = new ArrayList<Integer>(newIdxList);
		this.updateIdxList = new ArrayList<Integer>(updateIdxList);

		this.contributor = contributor;

		// 차후에 간단한 배열로 써서 하면 메모리를 아낄 수 있을 것 같다..
		// 일단 테스트니깐 생각나는대로 쓰자..
	}

	/** Index all text files under a directory. */
	@SuppressWarnings("deprecation")
	public int index() {
		String usage = "java org.apache.lucene.demo.IndexFiles"
				+ " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index"
				+ "in INDEX_PATH that can be searched with SearchFiles";

		boolean create = false; // Index된 데이터를 초기화하고 새로 인덱싱을 할 것인가??
		int count = 0;

		if (docsPath == null) {
			System.err.println("BasicIndexer Usage: " + usage);
			System.exit(1);
		}

		final File indexDir = new File(docsPath);
		if (!indexDir.exists() || !indexDir.canRead()) {
			System.out
					.println("BasicIndex : Document directory '"
							+ indexDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		IndexWriter writer = null;
		try {
			System.out.println("BasicIndex : Indexing to directory '"
					+ indexPath + "'...");

			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_CURRENT, analyzer);

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			} // 새로 인덱스를 만들 것인가(CREATE), 아니면 추가 할 것인가(CREATE_OR_APPEND)?

			newIdxListIter = newIdxList.iterator();

			writer = new IndexWriter(dir, iwc);
			count = indexDocs(writer, indexDir);

			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			Date end = new Date();
			System.out
					.println("BasicIndexer "
							+ (end.getTime() - start.getTime())
							+ " total milliseconds");

		} catch (IOException e) {
			System.out.println("BasicIndexer caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("BasicIndex : IndexWriter close err");
				e.printStackTrace();
			}
		}

		return count;
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 * 
	 * NOTE: This method indexes one document per input file. This is slow. For
	 * good throughput, put multiple documents into your input file(s). An
	 * example of this is in the benchmark module, which can create "line doc"
	 * files, one document per line, using the <a href=
	 * "../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 * 
	 * content data가 저장된 folder에 접근하여 indexing을 한 후, index data를 지정 폴더에 저장
	 * 
	 * @param writer
	 *            Writer to the index where the given file/dir info will be
	 *            stored
	 * @param file
	 *            The file to index, or the directory to recurse into to find
	 *            files to index
	 * @throws IOException
	 *             If there is a low-level I/O error
	 * @return new document indexing count
	 */
	int indexDocs(IndexWriter writer, File file) {
		// do not try to index files that cannot be read
		int count = 0;

		/*
		 * target of create indexing list
		 * idx(unique), (new)url, title, path, modified(date)
		 * , contents, registered(date), contributor(source(name) of donate doc),
		 * hits(amount of who is been indexed doc)
		 */
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) { // directory
					for (int i = 0; i < files.length; i++) {
						count += indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				// 중복되는 파일을 인덱싱 할 경우 pass함 (하지 않음)
				// map list에 존재 하지 않는 파일일 경우 인덱싱 하지 않음
				String filename = FileContainer.deleteExtension(file.getName());
				boolean isUpdateIdxList = false, isNewIdxList = false;
				Integer newHit = 0;
				int docIdx = 0;

				Document tmpDoc = new Document();

				IndexDataBean indexDataBean = null;
				int fileNum = Integer.parseInt(filename); // extension을 제거한
															// fileName
				isUpdateIdxList = updateIdxList.contains(fileNum);

				FileInputStream fis = null;
				
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException e3) {
					// TODO Auto-generated catch block
					System.err.println("BasicIndexer - content file read Error");
					e3.printStackTrace();
				}
				
				if (isUpdateIdxList == true) { // 찾은 파일 이름이 updateList에 있는 경우
					UpdateDocField updateField = new UpdateDocField();

					IndexReader reader = null;
					try {
						reader = DirectoryReader.open(FSDirectory
								.open(new File(PropertiesContainer.prop
										.getProperty("indexPath"))));
						IndexSearcher searcher = new IndexSearcher(reader);
						System.out.println("BasicIndexer : " + file);
						
						// hit 계산이 잘못됨, contributor수가 결국 hit 수임;;
//						newHit = updateIdxNHit.get(fileNum) + 1; // * <Integer, Integer>
//						
//						tmpDoc.add(new StringField("hit", newHit.toString(),
//								Field.Store.YES));
						// 시간 갱신
						long time = System.currentTimeMillis(); 
						SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String currentDateTime = dayTime.format(new Date(time));
						
						// 갱신할 데이터를 저장한 tmpDoc
						tmpDoc.add(new StringField("modifiedDate", currentDateTime,
								Field.Store.YES));
						tmpDoc.add(new StringField("contributor",
								this.contributor, Field.Store.YES));
						tmpDoc.add(new StringField("docPath",
								file.getPath(), Field.Store.YES));
						tmpDoc.add(new VecTextField("contents",
								new BufferedReader(
										new InputStreamReader(fis,
												"UTF-8")),
								Field.Store.NO));

						// update hits, contributor, contents(it is not storable, so write one more time)
						// where doc(docIdx)
						// file이름과 idx 번호가 같음.
						updateField.searchAndUpdateDocument(writer, searcher,
								tmpDoc, updateIdxList.indexOf(fileNum));
						} catch (IOException e2) {
						// TODO Auto-generated catch block
						System.err
								.println("BasicIndexer - UpdateDocField Error");
						e2.printStackTrace();
					} finally {
						try {
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err
									.println("BasicIndexer - file close err");
							e.printStackTrace();
						}
					}
				} else if (isUpdateIdxList == false) { // 찾은 파일이 newList에 있는지 검사
					isNewIdxList = newIdxList.contains(Integer
							.parseInt(filename));

					if (isNewIdxList == true) { // 새로운 파일(newList에 있음)
						System.out.println("BasicIndexer : " + file);
						docIdx = newIdxListIter.next();
						indexDataBean = newIdxBeanMap.get(docIdx);

						newHit = 1; // hit init

						try {
							Field idxNum = new StringField("docIdx",
									Integer.toString(docIdx), Field.Store.YES);
							Field urlField = new StringField("url",
									indexDataBean.getUrl(), Field.Store.YES);
							TextField titleField = new TextField(
									"title",
									indexDataBean.getTitle() == null ? "UNDEFINED" :
											indexDataBean.getTitle(),
									Field.Store.YES);
							Field modifiedField = new StringField(
									"modifiedDate",
									indexDataBean.getModifiedDate(),
									Field.Store.YES);
							Field registeredField = new StringField(
									"registeredDate",
									indexDataBean.getRegisteredDate(),
									Field.Store.YES);
							Field docPathField = new StringField("docPath",
									file.getPath(), Field.Store.YES);
							Field hitField = new StringField("hit",
									newHit.toString(), Field.Store.YES);
							Field contributorField = new StringField(
									"contributor", this.contributor,
									Field.Store.YES);

							tmpDoc.add(idxNum);
							tmpDoc.add(urlField);
							tmpDoc.add(titleField);
							tmpDoc.add(modifiedField);
							tmpDoc.add(registeredField);
							tmpDoc.add(docPathField);
							tmpDoc.add(hitField);
							tmpDoc.add(contributorField);

							/*
							 * titleField.setBoost(1.5f); title에 대해서 1.5배 가중치를
							 * 부여
							 */
							titleField.setBoost(1.5f);

							try {
								tmpDoc.add(new VecTextField("contents",
										new BufferedReader(
												new InputStreamReader(fis,
														"UTF-8")),
										Field.Store.NO));
								
								if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
									// New index, so we just add the document
									// (no old document can be there):
									System.out.println("BasicIndexer - adding file" + file);
									writer.addDocument(tmpDoc);
								} else {
									// Existing index (an old copy of this
									// document may have been indexed) so
									// we use updateDocument instead to replace
									// the old one matching the exact
									// path, if present:
									System.out.println("BasicIndexer - updating doc idx : " + docIdx);
									writer.updateDocument(
											new Term("docIdx", Integer.toString(docIdx)),
											tmpDoc);
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								System.err
										.println("BasicIndexer - add contents in Doc err");
								e1.printStackTrace();
							}
						} finally {
							try {
								fis.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.err
										.println("BasicIndexer - file close err");
								e.printStackTrace();
							}
						}
						++count;
					}
				} else { // 기존 있는 파일일 경우(현재 이미 색인이 되있지만 북마크상에는 없는 파일)
					// 아무것도 안한다.
					System.out.println("BasicIndexer - nothing to do");
				}
			}
		}

		return count;
	}
}