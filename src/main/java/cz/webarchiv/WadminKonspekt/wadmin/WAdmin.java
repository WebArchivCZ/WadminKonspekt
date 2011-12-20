package cz.webarchiv.WadminKonspekt.wadmin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class WAdmin {

    protected JdbcTemplate template = null;

    private class ResourceRowMapper implements ParameterizedRowMapper<Resource> {

        @Override
        public Resource mapRow(ResultSet rs, int row) throws SQLException {
            Resource resource = new Resource();
            resource.setId(rs.getInt("id"));
            resource.setUrl(rs.getString("url"));
            resource.setAlephId(rs.getString("aleph_id"));
            resource.setTitle(rs.getString("title"));
            resource.setConspectusSubcategoryId(rs.getInt("conspectus_subcategory_id"));
            return resource;
        }
    }

    private class ConspectusRowMapper implements ParameterizedRowMapper<ConspectusSubcategory> {

        @Override
        public ConspectusSubcategory mapRow(ResultSet rs, int row) throws SQLException {
            ConspectusSubcategory result = new ConspectusSubcategory();
            result.setId(rs.getInt("id"));
            result.setConspectusId(rs.getInt("conspectus_id"));
            result.setConspectusSubcategoryId(rs.getString("subcategory_id"));
            return result;
        }
    }

    public WAdmin(String url, String user, String passwd) throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        // dataSource.setUrl("jdbc:mysql://raptor.webarchiv.cz/wadmin");
        dataSource.setUsername(user);
        dataSource.setPassword(passwd);
        dataSource.setConnectionProperties(new Properties());
        dataSource.getConnectionProperties().put("characterEncoding", "UTF-8");
        dataSource.getConnection();
        template = new JdbcTemplate(dataSource);
        template.execute("SET NAMES utf8");
    }

    public WAdmin() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/wadmin");
        // dataSource.setUrl("jdbc:mysql://raptor.webarchiv.cz/wadmin");
        dataSource.setUsername("konspekt");
        dataSource.setPassword("nApP2010");
        dataSource.setConnectionProperties(new Properties());
        dataSource.getConnectionProperties().put("characterEncoding", "UTF-8");
        dataSource.getConnection();
        template = new JdbcTemplate(dataSource);
        template.execute("SET NAMES utf8");
    }

    public List<Resource> getResources() {
        List<Resource> resources = template.query("SELECT id, url, aleph_id, title, conspectus_subcategory_id FROM resources", new ResourceRowMapper());
        return resources;
    }

    public List<Resource> getContractedResources() {
        List<Resource> resources = template.query("SELECT id, url, aleph_id, title, conspectus_subcategory_id " +
                "FROM resources WHERE contract_id IS NOT NULL", new ResourceRowMapper());
        return resources;
    }

    public Resource getResourceByAlephId(String id) {
        try {
            return (Resource) template.queryForObject("SELECT id, url, aleph_id, title, conspectus_subcategory_id "
                    + "FROM resources WHERE aleph_id=?", new Object[]{id}, new ResourceRowMapper());
        } catch (Exception ex) {
            return null;
        }
    }

    public int getKeywordIdAddIfNotExists(String word) {
        if (!this.existsKeyword(word)) {
            this.addKeyword(word);
        }
        return this.getKeywordId(word);
    }

    private int getKeywordId(String word) {
        int id = template.queryForInt("SELECT id FROM keywords WHERE keyword = ?", new Object[]{word});
        return id;
    }

    private void addKeyword(String word) {
        int newId = template.queryForInt("SELECT MAX(id) FROM keywords");
        template.update("INSERT INTO keywords(id, keyword) VALUES(?, ?)", new Object[]{newId + 1, word});
    }

    private boolean existsKeyword(String word) {
        return (template.queryForInt("SELECT count(*) FROM keywords WHERE keyword = ?", new Object[]{word}) > 0);
    }

    public int getConspectusSubcategoryId(String id) {
        try {
            return template.queryForInt("SELECT id FROM conspectus_subcategories WHERE subcategory_id = ?", new Object[]{id});
        } catch (Exception ex) {
            return -1;
        }
    }

    public void updateResource(Resource resource, List<String> keywords) {
        this.updateResource(resource);
        this.updateResourceKeyWords(resource, keywords);
    }

    private void updateResource(Resource resource) {
        // template.update("UPDATE resources SET conspectus_subcategory_id=? WHERE id = ?",
        //        new Object[]{resource.getConspectusSubcategoryId(), resource.getId()});
        template.update("UPDATE resources SET conspectus_id=?, conspectus_subcategory_id = ?, annotation=? WHERE id = ?",
                new Object[]{resource.getConspectusId(), resource.getConspectusSubcategoryId(), resource.getDescription(), resource.getId()});
    }

    private void updateResourceKeyWords(Resource resource, List<String> keywords) {
        template.update("DELETE FROM keywords_resources WHERE resource_id = ?", new Object[]{resource.getId()});
        for (String keyword : keywords) {
            this.addKeyword(resource, keyword);
        }
    }

    private void addKeyword(Resource res, String keyword) {
        int keywordId = this.getKeywordIdAddIfNotExists(keyword);
        boolean exists = (template.queryForInt("SELECT COUNT(*) FROM keywords_resources WHERE resource_id = ? AND keyword_id = ?",
                new Object[]{res.getId(), keywordId}) > 0);
        if (!exists) {
            template.update("INSERT INTO keywords_resources(resource_id, keyword_id) VALUES(?, ?);",
                    new Object[]{res.getId(), keywordId});
        } 
    }
}
