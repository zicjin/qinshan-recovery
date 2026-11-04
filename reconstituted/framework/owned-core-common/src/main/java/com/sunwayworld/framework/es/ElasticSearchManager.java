package com.sunwayworld.framework.es;

import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.utils.BeanUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
   prefix = "spring.elasticsearch.rest",
   name = {"enabled"},
   havingValue = "true"
)
public class ElasticSearchManager {
   @Autowired
   private ElasticsearchOperations operations;
   @Value("${spring.elasticsearch.rest.highlights.pre-tags:<span style = 'color:red'>}")
   private String preTags;
   @Value("${spring.elasticsearch.rest.highlights.post-tags:</span>}")
   private String postTags;
   @Value("${spring.elasticsearch.rest.matching-min-ratio:80%}")
   private String matchingMinRatio;

   public <T> List<T> searchList(Query query, Class<T> entityType) {
      SearchHits<T> hints = this.operations.search(query, entityType);
      return this.toList(hints);
   }

   public <T> Pagination<T> searchPagination(Query query, Class<T> entityType) {
      SearchHits<T> hints = this.operations.search(query, entityType);
      List<T> itemList = this.toList(hints);
      if (query.getPageable().isUnpaged()) {
         return new Pagination<>(itemList);
      } else {
         int total = (int)hints.getTotalHits();
         Pageable pageable = query.getPageable();
         return new Pagination<>(total, pageable.getPageSize(), pageable.getPageNumber(), itemList);
      }
   }

   public HighlightBuilder getHighlightBuilder(String... fields) {
      HighlightBuilder highlightBuilder = new HighlightBuilder();

      for (String field : fields) {
         highlightBuilder.field(field);
      }

      highlightBuilder.preTags(new String[]{this.preTags});
      highlightBuilder.postTags(new String[]{this.postTags});
      return highlightBuilder;
   }

   public String getDefaultMatchingMinRatio() {
      return this.matchingMinRatio;
   }

   private <T> List<T> toList(SearchHits<T> hints) {
      return hints.stream().map(h -> {
         if (h.getContent() == null) {
            return h.getContent();
         } else {
            Map<String, List<String>> map = h.getHighlightFields();
            if (map.isEmpty()) {
               return h.getContent();
            } else {
               T item = h.getContent();
               map.forEach((k, v) -> {
                  if (!v.isEmpty()) {
                     BeanUtils.setPropertyValue(item, k, v.get(0));
                  }
               });
               return item;
            }
         }
      }).collect(Collectors.toList());
   }
}
