package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;

import java.util.*;

/**
 * Class to generate a query to search Snippets, implements the Builder pattern
 */
public class SnippetSearchQuery {
    /**
     * Query to be used in the database, must have parametrized variables
     */
    private String query;
    /**
     * Parameters to be used in the query, in order
     */
    private Object[] params;

    public String getQuery(){ return this.query; }

    public Object[] getParams(){ return this.params; }

    /**
     * Private constructor to follow the builder patter
     * @param query Query to be used in the database
     * @param params List with the parameters in order
     */
    private SnippetSearchQuery(String query, List<Object> params){
        this.query = query;
        this.params = params.toArray();
    }

    /**
     * Inner, static Builder class to implement the builder pattern
     */
    public static class Builder {
        /**
         * Map used to map types of search to the corresponding queries they translate to
         */
        private final Map<SnippetDao.Types, String> typeMap = new HashMap<SnippetDao.Types, String>(){{
            put(SnippetDao.Types.ALL, " AS s LEFT OUTER JOIN snippet_tags AS ts ON s.id = ts.snippet_id LEFT OUTER JOIN tags AS t ON t.id = ts.tag_id WHERE lower(t.name) LIKE lower(?) OR lower(s.title) LIKE lower(?) OR lower(s.code) LIKE lower(?)");
            put(SnippetDao.Types.TAG, " AS s INNER JOIN snippet_tags AS ts ON s.id = ts.snippet_id INNER JOIN tags AS t ON t.id = ts.tag_id WHERE lower(t.name) LIKE lower(?)");
            put(SnippetDao.Types.TITLE, " AS s WHERE lower(s.title) LIKE lower(?)");
            put(SnippetDao.Types.CONTENT, " AS s WHERE lower(s.code) LIKE lower(?)");
        }};
        /**
         * Map used to map the locations or sources of snippets to be searched among to the corresponding queries they translate to
         */
        private Map<SnippetDao.Locations, String> locationsMap = new HashMap<SnippetDao.Locations, String>(){{
            put(SnippetDao.Locations.HOME, "(SELECT * FROM complete_snippets)");
            put(SnippetDao.Locations.FAVORITES, "(SELECT sn.id, sn.user_id, sn.username, sn.reputation, sn.code, sn.title, sn.description, sn.language, sn.date_created, sn.icon FROM complete_snippets AS sn JOIN favorites AS fav ON fav.snippet_id = sn.id WHERE fav.user_id = ?)");
            put(SnippetDao.Locations.FOLLOWING, "(SELECT sn.id, sn.user_id, sn.username, sn.reputation, sn.code, sn.title, sn.description, sn.language, sn.date_created, sn.icon FROM complete_snippets AS sn JOIN snippet_tags AS st ON st.snippet_id = sn.id JOIN follows AS fol ON st.tag_id = fol.tag_id WHERE fol.user_id = ?)");
        }};
        /**
         * Map used to translate the given enum for order types into their query equivalent
         */
        private final Map<SnippetDao.Orders, String> ordersMap = new HashMap<SnippetDao.Orders, String>(){{
            put(SnippetDao.Orders.ASC, "ASC");
            put(SnippetDao.Orders.DESC, "DESC");
        }};
        /**
         * Map used to translate the types over which the order is done into their SQL equivalents
         */
        private final Map<SnippetDao.Types, String> orderTypesMap = new HashMap<SnippetDao.Types, String>(){{
            put(SnippetDao.Types.ALL, " s.title ");
            put(SnippetDao.Types.TAG, " t.name ");
            put(SnippetDao.Types.TITLE, " s.title ");
            put(SnippetDao.Types.CONTENT, " s.code ");
        }};
        private final Map<SnippetDao.QueryTypes, String> queryTypesMap = new HashMap<SnippetDao.QueryTypes, String>(){{
            put(SnippetDao.QueryTypes.COUNT, "SELECT COUNT(DISTINCT s.id) FROM ");
            put(SnippetDao.QueryTypes.SEARCH, "SELECT DISTINCT s.id, s.user_id, s.username, s.reputation, s.code, s.title, s.description, s.language, s.date_created, s.icon FROM ");
        }};
        /**
         * StringBuilder in order to make the building of the query more performant
         */
        private final StringBuilder query = new StringBuilder();
        /**
         * List of parameters to be used in the query
         */
        private final List<Object> params = new ArrayList<>();

        /**
         * Constructor for the Builder inner class
         * @param location Source of the snippets
         * @param userId ID of the user performing the operation, not needed if in HOME
         * @param type Type of search, over which parameter the search is going to be performed
         * @param term Term to be used for the search
         */
        public Builder(SnippetDao.QueryTypes queryType, SnippetDao.Locations location, Long userId, SnippetDao.Types type, String term){
            this.query
                    .append(this.queryTypesMap.get(queryType))
                    .append(this.locationsMap.get(location));
            if (userId != null){ params.add(userId); }
            this.query.append(this.typeMap.get(type));
            params.add("%" + term + "%");
            if (type.equals(SnippetDao.Types.ALL)){
                params.add("%" + term + "%");
                params.add("%" + term + "%");
            }
        }

        /**
         * Method to add order to the results
         * @param order Order of the results
         * @param type Field over which the order is going to be performed
         * @return this same instance of the Builder
         */
        public Builder setOrder(SnippetDao.Orders order, SnippetDao.Types type){
            if (!order.equals(SnippetDao.Orders.NO)){
                this.query.append(" ORDER BY")
                        .append(this.orderTypesMap.get(type))
                        .append(this.ordersMap.get(order));
            }
            return this;
        }

        public Builder setPaging(int page, int pageSize){
            this.query.append(" LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add(pageSize * (page - 1));
            return this;
        }

        public SnippetSearchQuery build(){
            return new SnippetSearchQuery(this.query.toString(), this.params);
        }
    }
}
