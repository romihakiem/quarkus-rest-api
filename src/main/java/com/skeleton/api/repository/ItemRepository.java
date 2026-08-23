package com.skeleton.api.repository;

import com.skeleton.api.entity.Item;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemRepository implements PanacheRepository<Item> {
    /**
     * Builds a Panache query, adding name LIKE / category filters only when
     * the caller actually supplied them.
     */
    public PanacheQuery<Item> search(String search, String category, Sort sort) {
        StringBuilder query = new StringBuilder("1=1");
        Parameters params = Parameters.with("dummy", 1);

        if (search != null && !search.isBlank()) {
            query.append(" and lower(name) like :search");
            params = params.and("search", "%" + search.toLowerCase() + "%");
        }

        if (category != null && !category.isBlank()) {
            query.append(" and lower(category) = :category");
            params = params.and("category", category.toLowerCase());
        }

        return find(query.toString(), sort, params);
    }
}
