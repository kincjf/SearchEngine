package engine.index.common;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
/**
 * update old doc value to new doc
 * @author KIMSEONHO
 *
 */
public class UpdateDocField {

	/**
	 * Searches for a document matching Term and updates it with the fields it
	 * has in common with the document parameter.
	 * 
	 * @param writer
	 * @param searcher
	 * @param updateDoc
	 *            contained fields will be replacements for document identified
	 *            by term
	 * @param docIdx
	 *            identifies a single document in the index which will be
	 *            updated
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if given term matches more than 1 indexed document or none.
	 */
	public void searchAndUpdateDocument(IndexWriter writer,
			IndexSearcher searcher, Document updateDoc, int docIdx)
			throws IOException {
		Document oldDoc;
		if(docIdx == -1) {
			docIdx = 0;
		}
		oldDoc = searcher.doc(docIdx);

		if (oldDoc != null) {
			List<IndexableField> updateDocFields = updateDoc.getFields();
			for (IndexableField updateField : updateDocFields) {
				String fieldName = updateField.name();
				String currentFieldValue = oldDoc.get(fieldName);

				if (currentFieldValue != null) {
					if (fieldName == "contributor") {
						boolean isExistContributor = false;

						String newContributor = updateDoc.get(fieldName);
						StringTokenizer st = new StringTokenizer(
								currentFieldValue, ",");

						/* check if 'contributor' is exist */
						while (st.hasMoreTokens()) {
							if (newContributor.equals(st.nextToken())) {
								isExistContributor = true;
								/* contributor was already exist. */
							}
						}

						if (isExistContributor == false) {
							// url에 대하여 해당 contributor가 존재하지 않을 경우
							currentFieldValue = currentFieldValue + ","
									+ newContributor;
							Integer newHit = st.countTokens() + 1;
							
							oldDoc.removeField(fieldName);
							oldDoc.removeField("hit");
							
							Field updateContributorField = new StringField(
									"contributor", currentFieldValue,
									Field.Store.YES);
							StringField updateHitField = new StringField("hit", newHit.toString(),
									Field.Store.YES);
							
							// replacement field value
							oldDoc.add(updateContributorField);
							oldDoc.add(updateHitField);
						}
					} else {
						/*
						 * remove only one(if multiple fields exist with this
						 * name, it will removes the first occurance)
						 * occurrences of the old field - hit field
						 */
						oldDoc.removeField(fieldName);

						/* insert the replacement */
						oldDoc.add(updateField);
					}
				} else {
					/* update Field isn't have in old Doc(new field) */
					oldDoc.add(updateField);
				}
			}

			/* write the old document to the index with the modifications */
			writer.updateDocument(new Term("docIdx", Integer.toString(docIdx)),
					oldDoc);
		}
	}
}