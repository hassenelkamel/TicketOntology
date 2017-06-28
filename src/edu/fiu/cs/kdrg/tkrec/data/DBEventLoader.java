package edu.fiu.cs.kdrg.tkrec.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import edu.fiu.cs.kdrg.tkrec.core.Event;
import edu.fiu.cs.kdrg.tkrec.util.DBConnection;
import edu.fiu.cs.kdrg.tkrec.util.XMLEventWriter;

public class DBEventLoader {
	/**
	 * query events according query specified by sql, range from [from, to]
	 * inclusively, start from index 0.
	 * 
	 * @param conn
	 * @param sql
	 * @param from
	 * @param to
	 * @return a collection event
	 */
	public static List<Event> queryEvent(Connection conn, String sql) {

		List<Event> events = new ArrayList();
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int colNum = rsmd.getColumnCount();

			int numOfRecords = 0;
			while (rs.next()) {

				Event event = new Event();
				for (int i = 1; i <= colNum; i++) {
					int type = rsmd.getColumnType(i);
					if (type == Types.INTEGER) {
						event.setValue(rsmd.getColumnName(i), rs.getInt(i));
					} else if (type == Types.VARCHAR) {
						String value = rs.getString(i);
						if (value != null)
							event.setValue(rsmd.getColumnName(i), value);
					} else if (type == Types.TIMESTAMP) {
						if (rs.getTimestamp(i) != null)
							event.setValue(rsmd.getColumnName(i), rs.getTimestamp(i).getTime() + "");
					} else {
						String value = rs.getString(i);
						if (value != null)
							event.setValue(rsmd.getColumnName(i), value);
					}
				}
				events.add(event);
				numOfRecords++;
//				if (numOfRecords % 1000 == 0) {
//					System.out.println(String.format("load instances %d", numOfRecords));
//				}
			}

			if (pstm != null)
				pstm.close();
			if (conn != null)
				conn.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return events;
	}

	/**
	 * Load Database data to xml file.
	 * @param filename
	 * @throws Exception
	 */
	public static void loadDBData2File(String filename) throws Exception {
		Connection conn = DBConnection.connect();
		if (null == conn)
			System.out.println("connection is null");

		String sql = "SELECT * FROM tickets where RESOLUTION not like " + "'%known issue%' " + "and "
				+ "RESOLUTION not like " + "'%no action%' " + "and " + " RESOLUTION not like" + " '%duplicat%' " + "and "
				+ "RESOLUTION not like " + "'%no issue%' " + "and " + "RESOLUTION not like " + "'%false alert%' " + "and "
				+ "RESOLUTION not like " + "'%bau%' " + "and " + "RESOLUTION not like " + "'%informed customer%' "
				+ "and " + "RESOLUTION not like " + "'%closing%' " + "and " + "RESOLUTION not like " + "'%closed%' " + "and "
				+ "RESOLUTION not like " + "'%unknown%' " + "and " + "RESOLUTION not like " + "'%#%' " + "and "
				+ "ACCOUNT_ID=1;";
		
		System.out.println(sql);
		
		List<Event> events = DBEventLoader.queryEvent(conn, sql);

		XMLEventWriter xmlWriter = new XMLEventWriter();
		xmlWriter.write(events, filename);
	}

}