package engine.index.dao.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import engine.index.vo.IndexDataBean;

/**
 * Manage DB Connection about indexing
 * column name, table name are must write upper case!!
 * @author KIMSEONHO
 *
 */
public class IndexDAO {

	DataSource ds;
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	/**
	 * connection for db schema(SearchEngine)
	 * @see META-INF/context.xml
	 */
	public IndexDAO() {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/SearchEngine");
			con = ds.getConnection();
		} catch (Exception ex) {
			System.out.println("IndexDAO - Connection Error");
			ex.printStackTrace();
			return;
		}
	}

	/**
	 * dbcp connection pool
	 */
	public void connection() {
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			System.err.println("IndexDAO - connection err");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * db transaction이 많아서 connection은 수동으로 연결하고 끊음
	 */
	public void close() {
		if (con != null)
			try {
				con.close();
			} catch (SQLException ex) {
				System.err.println("IndexDAO - close err");
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
	}

	/**
	 * transaction configure
	 * @param isCommit
	 */
	public void setAutoCommit(boolean isCommit) {
		try {
			con.setAutoCommit(isCommit);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("IndexDAO - setAutoCommit err");
			e.printStackTrace();
		}
	}

	/**
	 * select COUNT(IDX_NUM) from URL_DATA
	 * @return number of table(URL_DATA) data 
	 */
	public int getURLListCount() {
		int urlCount = 0;
		String sql = "select COUNT(IDX_NUM) from URL_DATA";

		try {
			// db access가 많으므로 접속관련은 직접 처리한다. //this.con = ds.getConnection();
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				urlCount = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("IndexDAO getListCount - error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getListCount rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - getListCount pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return urlCount;
	}

	/**
	 * select URL from URL_DATA
	 * if return is empty, table(URL_DATA) was empty
	 * @return URL in table(URL_DATA)
	 */
	public ArrayList<String> getURLList() {
		String sql = "select URL from URL_DATA";

		ArrayList<String> list = new ArrayList<String>();

		try {
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(rs.getString("URL"));
			}

			return list;
		} catch (Exception ex) {
			System.out.println("IndexDAO - getURLList error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getURLList rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getURLList pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return list; // 결과가 없더라도 일단 객체를 반환하고 본다.
	}

	/**
	 * select IDX_NUM from URL_DATA
	 * @return IDX_NUM, error/not exist - empty list
	 */
	public ArrayList<Integer> getIdxNumList() {
		String sql = "select IDX_NUM from URL_DATA";

		ArrayList<Integer> list = new ArrayList<Integer>();

		try {
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(rs.getInt("IDX_NUM"));
			}

			return list;
		} catch (Exception ex) {
			System.out.println("IndexDAO - getIdxNumList error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getIdxNumList rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - getIdxNumList pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return list; // 결과가 없더라도 일단 객체를 반환하고 본다.
	}

	/**
	 * select HIT from URL_DATA where URL = ?
	 * 
	 * @param url			url
	 * @return			URL_DATA.hit, if select(dml) was fail, return 0
	 */
	public int getHit(String url) {
		String sql = "select HIT from URL_DATA where URL = ?";

		int hit = 0;

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, url);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				hit = rs.getInt("HIT");
			}

			return hit;
		} catch (Exception ex) {
			System.out.println("IndexDAO - getHit error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getHit rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getHit pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return hit; // 실패 - 0 반환.
	}

	/**
	 * insert into URL_DATA (IDX_NUM, URL, TITLE, MODIFIED_DATE, REGISTERED_DATE, HIT)
	 * 
	 * @param indexDataBean
	 * @return if insert was fail or reject
	 */
	public boolean InsertIndexData(IndexDataBean indexDataBean) {
		String sql = null;

		int result = 0;

		try {
			sql = "insert into URL_DATA "
					+ "(IDX_NUM, URL, TITLE, MODIFIED_DATE, REGISTERED_DATE, HIT) "
					+ "values(?, ?, ?, ?, ?, ?)";

			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, indexDataBean.getIdxNum()); // IDX_NUM
			pstmt.setString(2, indexDataBean.getUrl()); // URL
			pstmt.setString(3, indexDataBean.getTitle()); // TITLE
			pstmt.setString(4, indexDataBean.getModifiedDate());
			pstmt.setString(5, indexDataBean.getRegisteredDate()); // REGISTERED_DATE
			pstmt.setInt(6, indexDataBean.getHit()); // REGISTERED_DATE

			result = pstmt.executeUpdate();

			if (result == 0) {
				return false;
			}

			return true;

		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexInsert() Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
		}

		return false;
	}

	/**
	 * update URL_DATA set HIT = ? where URL = ?
	 * @param indexDataBean			have in update date
	 * @return	update success or fail
	 * @throws Exception
	 */
	public boolean IndexModify(IndexDataBean indexDataBean) throws Exception {
		String sql = "update URL_DATA set HIT = ? where URL = ?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, indexDataBean.getHit());
			pstmt.setString(2, indexDataBean.getUrl());

			pstmt.executeUpdate();
			return true;
		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexModify() Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexModify rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - IndexModify pstmt close err");
					ex.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * update URL_DATA set HIT = (HIT + 1) where URL = ?
	 * @param url
	 * @return	if update is in succeed, return updated url hit times
	 */
	public int updateURLHit(String url) { // 실패시 0, 성공시 갱신된 값 반환
		String sql = "update URL_DATA set HIT = (HIT + 1) where URL = ?";
		// 기존에 비해 1만큼 증가시킨다.
		int updateHit = 0;

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, url);
			if (pstmt.executeUpdate() == 0) { // 실패
				updateHit = 0;
			} else { // 성공
				updateHit = this.getHit(url);
			}

			return updateHit;
		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexHitModify() Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - IndexHitModify rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - IndexHitModify pstmt close err");
					ex.printStackTrace();
				}
			}
		}
		return updateHit;
	}

	/**
	 * add CONTRIBUTOR and initialize data
	 * insert into CONTRIBUTOR(ID, UPLOAD_COUNT, URL_UPLOAD_COUNT) values(?, 1, 1)
	 * if contributor was exist, contribute count is add one time
	 * @link updateUploadCount
	 * @param contributor		bookmark contributor
	 * @return	if update is in succeed, return updated url hit times
	 */
	public boolean InsertContributor(String contributor) {
		String sql = null;
		boolean isExist = false;

		try {
			isExist = this.isExistContributor(contributor); // 등록이 되어 있는가(중복검사)

			if (isExist) { // 이미 등록되어 있음 OR 실패
				System.out.println("IndexDAO - InsertContributor \n "
						+ contributor + " is already exist.");
				
				this.updateUploadCount(contributor);		// contributor의 uploadCount + 1
			} else { // 등록되어있지 않음(처음)
				sql = "insert into CONTRIBUTOR(ID, UPLOAD_COUNT, URL_UPLOAD_COUNT) values(?, ?, ?)";
				
				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, contributor); // contributor 추가
				pstmt.setInt(2, 1); // Defalult value : 1
				pstmt.setInt(3, 0); // Defalult value : 1
				
				pstmt.executeUpdate();
			}

			return true; // 성공
		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexInsert() Error");
			ex.printStackTrace();
			return false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * update CONTRIBUTOR set UPLOAD_COUNT = (UPLOAD_COUNT + 1) where ID = contributor
	 * @param contributor
	 * @return	if update is in succeed, return updated url hit times
	 */
	public boolean updateUploadCount(String contributor) {
		String sql = "update CONTRIBUTOR set UPLOAD_COUNT = (UPLOAD_COUNT + 1) where ID = ?";

		try {
			pstmt = con.prepareStatement(sql);
					
			//int currentUploadCount = this.getURLUploadCount(contributor);
	
			pstmt.setString(1, contributor);
	
			pstmt.executeUpdate();
			
			return true;
		} catch (Exception ex) {
			System.out.println("IndexDAO - updateUploadCount Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - updateUploadCount rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - updateUploadCount rs close err");
					ex.printStackTrace();
				}
			}
		}

		return false;	// 실패
	}
	
	/**
	 * select URL_UPLOAD_COUNT from CONTRIBUTOR where ID = contributor
	 * @param contributor
	 * @return 0(존재하지 않음), <1(성공), -1(error)
	 */
	public int getURLUploadCount(String contributor) {
		String sql = "select URL_UPLOAD_COUNT from CONTRIBUTOR where ID = ?";
		int uploadCount = -1;
		// 0(존재하지 않음), <1(성공), -1(error)
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, contributor);

			rs = pstmt.executeQuery();

			if (rs.next()) { // 조회 성공
				uploadCount = rs.getInt("URL_UPLOAD_COUNT");
			} else { // 데이터가 존재하지않음
				uploadCount = 0;
				System.out.println("IndexDAO - getURLUploadCount : data not exist");
			}

			return uploadCount;
		} catch (Exception ex) {
			System.out.println("IndexDAO - getURLUploadCount error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - getURLUploadCount rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - getURLUploadCount pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return uploadCount;		// error -1
	}
	
	/**
	 * update CONTRIBUTOR set URL_UPLOAD_COUNT = (URL_UPLOAD_COUNT + updateURLSize) where ID = contributor
	 * @param contributor
	 * @param updateURLSize
	 * @return if update is in succeed, return updated url hit times
	 */
	public boolean updateURLUploadCount(String contributor, int updateURLSize) {
		if(updateURLSize <= 0) {
			return false;		// 잘못 된 값이 들어올 수 있으므로 1차 필터링
		}
		
		String sql = "update CONTRIBUTOR set URL_UPLOAD_COUNT = (URL_UPLOAD_COUNT + ?) where ID = ?";
		
		try {
			pstmt = con.prepareStatement(sql);
	
			pstmt.setInt(1, updateURLSize);
			pstmt.setString(2, contributor);
	
			pstmt.executeUpdate();
			
			return true;
		} catch (Exception ex) {
			System.out.println("IndexDAO - updateURLUploadCount Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - updateURLUploadCount rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - updateURLUploadCount rs close err");
					ex.printStackTrace();
				}
			}
		}

		return false;	// 실패
	}
	
	/**
	 * insert into REGISTER_MEMBER (URL, ID) values(url, register)
	 * url에 대하여 중복검사를 한 후 중복되지 않을 경우 url 최초 등록자로 등록함
	 * contributor는 검색 시스템에 참여하였는가이고
	 * register는 url에 대하여 등록하였는가에 대한 데이터
	 * @param url
	 * @param register
	 * @return  -1 - 실패, 0 - 이미 있음, 1이상(DML을 수행한 수) - 성공
	 */
	public boolean InsertRegisterMember(String url, String register) {
		String sql = null;
		boolean isExist = false;
		int result = 0; // -1 - 실패, 0 - 이미 있음, 1이상(DML을 수행한 수) - 성공

		try {
			isExist = this.isExistRegisterMember(url, register); // 등록이 되어
																	// 있는가(중복검사)

			if (isExist) { // 이미 등록되어 있음 
				System.out.println("IndexDAO - InsertRegisterMember \n "
						+ register + " is already exist.");
				result = 1;
			} else { // 없음
				sql = "insert into REGISTER_MEMBER (URL, ID) values(?, ?)";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, url); // register_member 추가
				pstmt.setString(2, register); // register_member 추가

				result = pstmt.executeUpdate();
			}

		} catch (Exception ex) {
			System.out.println("IndexDAO - InsertRegisterMember() Error");
			// ex.printStackTrace(); // 거슬려서 일단 지웠음..
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - InsertRegisterMember rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - InsertRegisterMember rs close err");
					ex.printStackTrace();
				}
			}
		}

		if (result == 0) {
			return false; // 실패
		} else {
			return true; // 성공
		}
	}

	/**
	 * select ID from REGISTER_MEMBER where URL = url AND ID = register
	 * 한 id(contributor)에 대한 url 중복등록 체크
	 * @param url
	 * @param register
	 * @return		true - already exist, false - not exist in table(REGISTER_MEMBER) data 
	 */
	public boolean isExistRegisterMember(String url, String register) {
		String sql = "select ID from REGISTER_MEMBER where URL = ? AND ID = ?";
		boolean isExist = false;

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, url);
			pstmt.setString(2, register);

			rs = pstmt.executeQuery();

			if (rs.next()) { // 이미 등록되어 있음
				isExist = true;
			} else { // 없음
				isExist = false;
			}

			return isExist;
		} catch (Exception ex) {
			System.out.println("IndexDAO - isExistRegisterMember error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - isExistRegisterMember rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - isExistRegisterMember pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return false;
	}

	/**
	 * select ID from CONTRIBUTOR where ID = id(contributor)
	 * 검색 시스템에 기여한 적이 있는가에 대한 체크
	 * @param id
	 * @return true - already exist, false - not exist in table(CONTRIBUTOR) data
	 */
	public boolean isExistContributor(String id) {
		String sql = "select ID from CONTRIBUTOR where ID = ?";
		boolean isExist = false;

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id); // contributor 추가

			rs = pstmt.executeQuery();

			if (rs.next()) { // 이미 등록되어 있음
				isExist = true;
			} else { // 없음
				isExist = false;
			}

			return isExist;
		} catch (Exception ex) {
			System.out.println("IndexDAO - isExistContributor error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - isExistContributor rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - isExistContributor pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return false;
	}
}
