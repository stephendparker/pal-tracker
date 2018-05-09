package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private DataSource ds;
    private JdbcTemplate jdbcTemp;
    private GeneratedKeyHolder gkh = new GeneratedKeyHolder();

    public JdbcTimeEntryRepository(DataSource ds) {
        this.ds = ds;
        this.jdbcTemp = new JdbcTemplate(ds);
    }

    ;

    @Override
    public TimeEntry find(long id) {
        String sql = "SELECT * FROM time_entries WHERE id = ?";

        try {

            TimeEntry timeEntry = (TimeEntry) jdbcTemp.queryForObject(
                    sql, new Object[]{id}, new TimeEntryRowMapper());
            return timeEntry;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }

    }

    @Override
    public List<TimeEntry> list() {

        String sql = "SELECT * FROM time_entries";

        try {

            List<TimeEntry> list =  (List) jdbcTemp.query(sql,  new TimeEntryRowMapper());
            return list;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }


    }

    @Override
    public TimeEntry update(long id, TimeEntry any) {


        final PreparedStatementCreator psc = new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {

                java.sql.Date convertedDate = java.sql.Date.valueOf(any.getDate());
                final PreparedStatement ps = connection.prepareStatement("update time_entries set project_id=?,user_id=?,date=?,hours=?  where ID =?"
                );
                ps.setLong(1, any.getProjectId());
                ps.setLong(2, any.getUserId());
                ps.setDate(3, convertedDate);
                ps.setInt(4, any.getHours());
                ps.setLong(5, id);

                return ps;
            }
        };

        // The newly generated key will be saved in this object

        jdbcTemp.update(psc);



        return find(id);
    }

    @Override
    public void delete(long id) {
        final PreparedStatementCreator psc = new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {


                final PreparedStatement ps = connection.prepareStatement("delete from time_entries where ID =?"
                );
                ps.setLong(1, id);
                return ps;
            }
        };
        jdbcTemp.update(psc);

    }

    @Override
    public TimeEntry create(TimeEntry any) {
        //   jdbcTemp.update("INSERT INTO time_entries (id,project_id,user_id,date,hours) VALUES(?,?,?,?,?)",
        //         new Object[] { any.getProjectId(),any.getUserId(),any.getDate(),any.getHours() });

        final PreparedStatementCreator psc = new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {

                java.sql.Date convertedDate = java.sql.Date.valueOf(any.getDate());
                final PreparedStatement ps = connection.prepareStatement("INSERT INTO time_entries (project_id,user_id,date,hours) VALUES(?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, any.getProjectId());
                ps.setLong(2, any.getUserId());
                ps.setDate(3, convertedDate);
                ps.setInt(4, any.getHours());

                return ps;
            }
        };

        // The newly generated key will be saved in this object
        final KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemp.update(psc, holder);

        final long newNameId = holder.getKey().longValue();


        return find(holder.getKey().longValue());
    }


    private class TimeEntryRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            TimeEntry tm = new TimeEntry();
            tm.setId(rs.getLong("id"));
            tm.setProjectId(rs.getLong("project_id"));
            tm.setUserId(rs.getLong("user_id"));
            tm.setDate(rs.getDate("date").toLocalDate());
            tm.setHours(rs.getInt("hours"));
            return tm;


        }
    }
}